package com.example.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class WinScreenController implements Initializable {

    static String winMessage = "";
    @FXML
    Label winlabel;
    @FXML
    Button playButton;
    @FXML
    Button QuitButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        winlabel.setText(winMessage);
    }

    public void playAgain(ActionEvent playAgainClicked) throws IOException {
        Parent parent = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("Game.fxml")));
        Stage stage = (Stage) ((Node) playAgainClicked.getSource()).getScene().getWindow();
        stage.setScene(new Scene(parent));
        stage.show();
    }

    public void quit(ActionEvent quitPressed) throws IOException {
        Parent parent = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("Title.fxml")));
        Stage stage = (Stage) ((Node) quitPressed.getSource()).getScene().getWindow();
        stage.setScene(new Scene(parent));
        stage.show();
    }
}
