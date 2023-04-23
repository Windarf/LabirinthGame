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

import java.io.IOException;

public class MenuController {
    @FXML
    private TextField usernameTextField;

    @FXML
    private void initialize() {
        usernameTextField.setText(System.getProperty("user.name"));
    }

    public void handleStartButton(ActionEvent actionEvent) throws IOException {

        String username = usernameTextField.getText();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/ui.fxml"));
        Parent root = fxmlLoader.load();
        GameController gameController = fxmlLoader.getController();
        gameController.displayName(username);
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    public void handleHighScoreButton(ActionEvent actionEvent) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/highscores.fxml"));
        Parent root = fxmlLoader.load();
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
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
