package com.example.tjencryption;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Login extends Application {

    static Stage stage;
    @Override
    public void start(Stage stage) throws IOException {
        Login.stage = stage;
        stage.setTitle("Login");
        changeStage("login-view.fxml");
        stage.show();
        stage.setResizable(false);
    }

    public static void main(String[] args) {
        launch();
    }

    public static void changeStage(String fxml){
        Parent root = null;
        try {
            root = FXMLLoader.load(Login.class.getResource(fxml));
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}