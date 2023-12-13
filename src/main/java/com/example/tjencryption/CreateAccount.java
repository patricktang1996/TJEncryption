package com.example.tjencryption;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.sql.*;

public class CreateAccount {
    private static final String JDBC_URL = "jdbc:mysql://database-1.ch0uqgjn65pq.us-east-1.rds.amazonaws.com:3306/mswdev2023cloudandsecurity";
    private static final String USERNAME = "admin";
    private static String PASSWORD = "adminadmin";
    @FXML
    private TextField username;
    @FXML
    private TextField password;
    @FXML
    private Label alertText;

    PasswordHashing ph= new PasswordHashing();

    @FXML
    protected void onCreateClick(){
        String acc = username.getText();
        String pass = password.getText();
        if(acc.isEmpty()||pass.isEmpty()){
            alertText.setText("Username and password can't be empty");
            alertText.setVisible(true);
        } else{
            performCreate();
        }
    }

    private void performCreate() {
        String acc = username.getText();
        String pass = password.getText();

        String hashedPassword = ph.hashPassword(pass);

        try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD)) {
            String insertQuery = "INSERT INTO users (userName, userPass) VALUES (?, ?)";
            try (PreparedStatement insertStatement = connection.prepareStatement(insertQuery)) {
                insertStatement.setString(1, acc);
                insertStatement.setString(2, hashedPassword);

                int rowsAffected = insertStatement.executeUpdate();

                if (rowsAffected > 0) {
                    alertText.setText("User account created successfully");
                } else {
                    alertText.setText("Failed to create user account");
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    protected void onBackClick(){
        Login.changeStage("login-view.fxml");
        Login.stage.setTitle("Login");
    }
}
