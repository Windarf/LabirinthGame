package game.gui;

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

    public void startGame(ActionEvent actionEvent) throws IOException {

        String username = usernameTextField.getText();
        if (username.isEmpty()) {
            usernameTextField.setPromptText("Please enter your name!");
        } else {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ui.fxml"));
            Parent parent = loader.load();
            GameController gameController = loader.getController();
            gameController.displayName(username);
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            Scene scene = new Scene(parent);
            stage.setScene(scene);
            stage.show();
        }
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
