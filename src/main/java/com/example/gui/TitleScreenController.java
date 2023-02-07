package com.example.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class TitleScreenController {

    public void setGameScene(ActionEvent startClicked) throws IOException {
        Parent parent = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("Game.fxml")));
        Stage stage = (Stage) ((Node) startClicked.getSource()).getScene().getWindow();
        stage.setScene(new Scene(parent));
        stage.show();
    }
}
