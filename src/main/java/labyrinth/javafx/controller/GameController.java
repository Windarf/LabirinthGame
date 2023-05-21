package labyrinth.javafx.controller;

import javafx.animation.Animation;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import labyrinth.results.GameResult;
import labyrinth.results.GameResultRepository;
import labyrinth.util.javafx.ControllerHelper;
import labyrinth.util.javafx.Stopwatch;
import lombok.NonNull;
import lombok.Setter;
import org.tinylog.Logger;
import labyrinth.state.Direction;
import labyrinth.state.Position;
import labyrinth.state.PuzzleState;

import javax.inject.Inject;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;

public class GameController {

    @FXML
    private Label messageLabel;
    @FXML
    private GridPane grid;
    @FXML
    private TextField numberOfMovesField;
    @FXML
    private Button resetButton;
    @FXML
    private Button giveUpFinishButton;
    @FXML
    private Label stopwatchLabel;
    @Inject
    private FXMLLoader fxmlLoader;
    @Inject
    private GameResultRepository gameResultRepository;
    @Setter
    private String playerName;

    private final ImageView pieceViews = new ImageView("/images/blue.png");

    private PuzzleState state;

    private IntegerProperty numberOfMoves = new SimpleIntegerProperty();
    private BooleanProperty gameOver = new SimpleBooleanProperty();

    private Stopwatch stopwatch = new Stopwatch();
    private Instant startTime;

    @FXML
    private void initialize() {
        createBindings();
        stopwatchLabel.textProperty().bind(stopwatch.hhmmssProperty());
        populateGrid();
        Platform.runLater(() -> messageLabel.setText(String.format("Good luck, %s!", playerName)));
        registerHandlersAndListeners();
        startGame();
    }

    private void createBindings() {
        numberOfMovesField.textProperty().bind(numberOfMoves.asString());
    }

    private void startGame() {
        state = new PuzzleState();
        numberOfMoves.set(0);
        gameOver.set(state.isGoal());

        startTime = Instant.now();
        if (stopwatch.getStatus() == Animation.Status.PAUSED) {
            stopwatch.reset();
        }
        stopwatch.start();

        clearGrid();
        showStateOnGrid();
    }

    private void registerHandlersAndListeners() {
        gameOver.addListener(this::handleGameOver);
        registerKeyEventHandler();
    }

    private void registerKeyEventHandler() {
        final KeyCombination restartKeyCombination = new KeyCodeCombination(KeyCode.R, KeyCombination.CONTROL_DOWN);
        final KeyCombination quitKeyCombination = new KeyCodeCombination(KeyCode.Q, KeyCombination.CONTROL_DOWN);
        Platform.runLater(() -> grid.getScene().setOnKeyPressed(
                keyEvent -> {
                    if (restartKeyCombination.match(keyEvent)) {
                        Logger.debug("Restarting game...");
                        startGame();
                    } else if (quitKeyCombination.match(keyEvent)) {
                        Logger.debug("Exiting...");
                        Platform.exit();
                    } else if (keyEvent.getCode() == KeyCode.UP) {
                        Logger.debug("Up arrow pressed");
                        performMove(Direction.UP);
                    } else if (keyEvent.getCode() == KeyCode.RIGHT) {
                        Logger.debug("Right arrow pressed");
                        performMove(Direction.RIGHT);
                    } else if (keyEvent.getCode() == KeyCode.DOWN) {
                        Logger.debug("Down arrow pressed");
                        performMove(Direction.DOWN);
                    } else if (keyEvent.getCode() == KeyCode.LEFT) {
                        Logger.debug("Left arrow pressed");
                        performMove(Direction.LEFT);
                    }
                }
        ));
    }

    @FXML
    private void handleMouseClick(MouseEvent event) {
        var source = (Node) event.getSource();
        var row = GridPane.getRowIndex(source);
        var col = GridPane.getColumnIndex(source);
        Logger.debug("Click on square (%d,%d)", row, col);
        var direction = getDirectionFromClickPosition(row, col);
        direction.ifPresentOrElse(this::performMove,
                () -> Logger.warn("Click does not correspond to any direction"));
    }

    private void performMove(Direction direction) {
        if (state.canMove(direction)) {
            Logger.info("Move: {}", direction);
            var oldState = state.clone();
            state.move(direction);
            Logger.trace("New state: {}", state);
            updateStateOnGrid(oldState, state);
            numberOfMoves.set(numberOfMoves.get() + 1);
            if (state.isGoal()) {
                gameOver.set(true);
            }
        } else {
            Logger.warn("Invalid move: {}", direction);
        }
    }

    private void handleGameOver(ObservableValue<? extends Boolean> observableValue, boolean oldValue, boolean newValue) {
        if (newValue) {
            Logger.info("Player {} has solved the game in {} steps", playerName, numberOfMoves.get());
            stopwatch.stop();
            messageLabel.setText(String.format("Congratulations, %s!", playerName));
            resetButton.setDisable(true);
            giveUpFinishButton.setText("Finish");
        }
    }
    public void handleGiveUpFinishButton(@NonNull final ActionEvent actionEvent) throws IOException {
        final var buttonText = ((Button) actionEvent.getSource()).getText();
        Logger.debug("{} is pressed", buttonText);
        if (Objects.equals(buttonText, "Give Up")) {

            Logger.info("The game has been given up");
        }

        Logger.debug("Saving result");
        gameResultRepository.addOne(createGameResult());

        Logger.debug("Loading HighScoreController");
        ControllerHelper.loadAndShowFXML(
                fxmlLoader,
                "/fxml/highscores.fxml",
                (Stage) ((Node) actionEvent.getSource()).getScene().getWindow()
        );
    }

    public void handleResetButton(ActionEvent actionEvent) {
        Logger.debug("{} is pressed", ((Button) actionEvent.getSource()).getText());
        Logger.info("Resetting game");

        state = new PuzzleState();
        gameOver.set(state.isGoal());
        clearGrid();
        showStateOnGrid();
    }

    private void populateGrid() {
        for (var row = 0; row < grid.getRowCount(); row++) {
            for (var col = 0; col < grid.getColumnCount(); col++) {
                var square = new StackPane();
                square.getStyleClass().add("square");
                square.setOnMouseClicked(this::handleMouseClick);
                grid.add(square, col, row);
            }
        }
    }

    private void clearGrid() {
        for (var row = 0; row < 14; row++) {
            for (var col = 0; col < 14; col++) {
                getGridNodeAtPosition(grid, row, col)
                        .ifPresent(node -> ((StackPane) node).getChildren().clear());
            }
        }
    }

    private void showStateOnGrid() {
        var pos = state.getPosition(0);
        var pieceView = pieceViews;
        getGridNodeAtPosition(grid, pos)
                .ifPresent(node -> ((StackPane) node).getChildren().add(pieceView));
    }

    private void updateStateOnGrid(PuzzleState oldState, PuzzleState newState) {
            var oldPos = oldState.getPosition(0);
            var newPos = newState.getPosition(0);
            if (!newPos.equals(oldPos)) {
                Logger.trace("Piece {} has been moved from {} to {}", 0, oldPos, newPos);
                movePieceOnGrid(oldPos, newPos);
            }
    }

    private void movePieceOnGrid( Position from, Position to) {
        var pieceView = pieceViews;
        getGridNodeAtPosition(grid, from)
                .ifPresent(node -> ((StackPane) node).getChildren().remove(pieceView));
        getGridNodeAtPosition(grid, to)
                .ifPresent(node -> ((StackPane) node).getChildren().add(pieceView));
    }

    private Optional<Direction> getDirectionFromClickPosition(int row, int col) {
        var blockPos = state.getPosition(PuzzleState.BLOCK);
        Direction direction = null;
        try {
            direction = Direction.of(row - blockPos.row(), col - blockPos.col());
        } catch (IllegalArgumentException e) {
        }
        return Optional.ofNullable(direction);
    }

    private static Optional<Node> getGridNodeAtPosition(GridPane gridPane, Position pos) {
        return getGridNodeAtPosition(gridPane, pos.row(), pos.col());
    }

    private static Optional<Node> getGridNodeAtPosition(GridPane gridPane, int row, int col) {
        return gridPane.getChildren().stream()
                .filter(child -> GridPane.getRowIndex(child) == row && GridPane.getColumnIndex(child) == col)
                .findFirst();
    }

    private GameResult createGameResult() {
        return GameResult.builder()
                .player(playerName)
                .solved(state.isGoal())
                .duration(Duration.between(startTime, Instant.now()))
                .steps(numberOfMoves.get())
                .build();
    }
}