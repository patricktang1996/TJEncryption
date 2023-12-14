package com.example.tjencryption;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import java.sql.*;

public class LoginController {
    private static final String JDBC_URL = "jdbc:mysql://database-1.cqbnfgzzclyo.ap-southeast-2.rds.amazonaws.com:3306/mswdev2023cloudandsecurity";
    private static final String USERNAME = "admin";
    private static String PASSWORD = "adminadmin";

    PasswordHashing ph= new PasswordHashing();

    @FXML
    private TextField username;
    @FXML
    private TextField password;
    @FXML
    private Label alertText;

    @FXML
    protected void onLoginButtonClick() {
        //get username and password
        String acc = username.getText();
        String pass = password.getText();
        if(acc.isEmpty()||pass.isEmpty()){
            alertText.setText("Username and password can't be empty");
            alertText.setVisible(true);
        } else{
            performLogin();
        }
    }

    private void performLogin() {
        String acc = username.getText();
        String pass = password.getText();
        String hashedPassword = ph.hashPassword(pass);

        try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD)) {
            String loginQuery = "SELECT * FROM user WHERE userName = ? AND userPass = ?";
            try (PreparedStatement loginStatement = connection.prepareStatement(loginQuery)) {
                loginStatement.setString(1, acc);
                //loginStatement.setString(2, pass);
                loginStatement.setString(2, hashedPassword);
                ResultSet resultSet = loginStatement.executeQuery();

                if (resultSet.next()) {
                    alertText.setText("Successful login");
                    Login.changeStage("dashboard-view.fxml");
                    Login.stage.setTitle("Text encryption");
                } else {
                    alertText.setText("Wrong acc/password");
                }

                resultSet.close();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    protected void onCreateButtonClick() {
        Login.changeStage("create-view.fxml");
        Login.stage.setTitle("Create a new account");
    }

}