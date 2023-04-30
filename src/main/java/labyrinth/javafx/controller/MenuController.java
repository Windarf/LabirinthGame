package labyrinth.javafx.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import labyrinth.results.GameResultRepository;
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
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        fxmlLoader.setLocation(getClass().getResource("/fxml/ui.fxml"));
        Parent root = fxmlLoader.load();
        fxmlLoader.<GameController>getController().setPlayerName(usernameTextField.getText());
        stage.setScene(new Scene(root));
        stage.show();
        Logger.info("The user's name is set to {}, loading game scene", usernameTextField.getText());
    }

    public void loadScoreboardAction(
            @NonNull final ActionEvent actionEvent) {

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
    }


    public void handleHighScoreButton(ActionEvent actionEvent) throws IOException {

        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        fxmlLoader.setLocation(getClass().getResource("/fxml/highscores.fxml"));
        Parent root = fxmlLoader.load();
        stage.setScene(new Scene(root));
        stage.show();
    }

    public void handleRulesButton(){
        var alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText("Rules");
        alert.setContentText("Your goal is to reach the green field at the right side! You can only move forward and to your right! ");
        alert.showAndWait();
    }

    public void handleExitButton(){
        Platform.exit();
    }
}
