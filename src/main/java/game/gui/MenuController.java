package game.gui;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class MenuController {
    @FXML
    private Button startButton;
    @FXML
    private Button rulesButton;

    private Stage stage;
    private Scene scene;
    private Parent parent;

    public void startGame(ActionEvent actionEvent) throws IOException {
        parent = FXMLLoader.load(getClass().getResource("/fxml/ui.fxml"));
        stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        scene = new Scene(parent);
        stage.setScene(scene);
        stage.show();
    }

    public void handleRulesButton(ActionEvent actionEvent){
        var alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText("Rules");
        alert.setContentText("Your goal is to reach the green field at the right side! You can only move forward and to your right! ");
        alert.showAndWait();
    }

    public void handleExitButton(ActionEvent actionEvent){
        Platform.exit();
    }
}
