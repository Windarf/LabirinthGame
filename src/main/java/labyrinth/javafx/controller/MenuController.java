package labyrinth.javafx.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;
import labyrinth.results.GameResultRepository;
import labyrinth.util.javafx.ControllerHelper;
import labyrinth.util.javafx.FileChooserHelper;
import lombok.NonNull;
import org.tinylog.Logger;

import javax.inject.Inject;
import java.io.IOException;

public class MenuController {
    @Inject
    private FXMLLoader fxmlLoader;
    @FXML
    private TextField usernameTextField;
    @Inject
    private GameResultRepository gameResultRepository;

    @FXML
    private void initialize() {
        usernameTextField.setText(System.getProperty("user.name"));
    }

    public void handleStartButton(ActionEvent actionEvent) throws IOException {
        handleRulesButton();
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        ControllerHelper.loadAndShowFXML(fxmlLoader, "/fxml/ui.fxml", stage);
        fxmlLoader.<GameController>getController().setPlayerName(usernameTextField.getText());
        Logger.info("The user's name is set to {}, loading game scene", usernameTextField.getText());
    }

    public void loadScoreboardAction(
            @NonNull final ActionEvent actionEvent) throws IOException {

        FileChooserHelper.show(
                        true,
                        (Stage) ((Node) actionEvent.getSource()).getScene().getWindow()
                )
                .ifPresent(file -> {
                    try {
                        gameResultRepository.loadFromFile(file);
                    } catch (IOException ex) {
                        Logger.error(ex.getMessage());
                    }
                });

        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        ControllerHelper.loadAndShowFXML(fxmlLoader, "/fxml/highscores.fxml", stage);
    }

    public void handleRulesButton(){
        var alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Rules");
        alert.setHeaderText("Rules and Controls");
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setContent(new Label("""
                Your goal is to reach the green field at the right side!\s
                However you can only move forward and to your right!
                You can control the game with the keyboard arrows or by clicking with the mouse!\s
                """));
        dialogPane.getContent().setStyle("-fx-font-size: 18px;");
        alert.showAndWait();
    }

    public void handleExitButton(){
        Platform.exit();
    }
}
