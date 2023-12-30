package com.example.tjencryption;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.sql.*;

public class CreateAccount {
    private static final String JDBC_URL = "jdbc:mysql://database-2.cqbnfgzzclyo.ap-southeast-2.rds.amazonaws.com:3306/mswdev2023cloudandsecurity";
    private static final String USERNAME = "admin";
    private static String PASSWORD = "adminadmin";
    @FXML
    private TextField username;
    @FXML
    private TextField password;
    @FXML
    private Label alertText;

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
        String hashedPassword = Hashing.hashing(pass);
        String hashedName = Hashing.hashing(acc);
        try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD)) {
            if (isUsernameExists(connection, hashedName)) {
                alertText.setText("Username already exists. Choose a different username.");
            } else {
                String insertQuery = "INSERT INTO user (userName, userPass) VALUES (?, ?)";
                try (PreparedStatement insertStatement = connection.prepareStatement(insertQuery)) {
                    insertStatement.setString(1, hashedName);
                    insertStatement.setString(2, hashedPassword);
                    int rowsAffected = insertStatement.executeUpdate();
                    if (rowsAffected > 0) {
                        alertText.setText("User account created successfully");
                    } else {
                        alertText.setText("Failed to create user account");
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private boolean isUsernameExists(Connection connection, String username) throws SQLException {
        String query = "SELECT COUNT(*) FROM user WHERE userName = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, username);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    int count = resultSet.getInt(1);
                    return count > 0;
                }
            }
        }
        return false;
    }

    @FXML
    protected void onBackClick(){
        Login.changeStage("login-view.fxml");
        Login.stage.setTitle("Login");
    }
}
