package com.chowtime.controllers;

import com.chowtime.database.DatabaseConnection;
import com.chowtime.services.DatabaseService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {
    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label statusLabel;

    @FXML
    private Button loginButton;

    private final DatabaseService databaseService = new DatabaseService();

    @FXML
    private void handleLogin(){
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()){
            statusLabel.setText("Please enter your username and password.");
            return;
        }

        try {
            loginButton.setDisable(true);
            statusLabel.setText("Connecting to MySQL...");

            //store the credentials
            DatabaseConnection.configure(username, password);
            //initialize the database
            databaseService.initializeDatabase();
            openMainWindow();
        } catch (Exception e){
            statusLabel.setText("Connection failed. check your credentials.");
            passwordField.clear();
            DatabaseConnection.configure("", "");
            e.printStackTrace();
        } finally {
            loginButton.setDisable(false);
        }
    }

    //move to the main window
    private void openMainWindow() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/chowtime/database-view.fxml"));
        Scene scene = new Scene(loader.load());
        Stage stage = (Stage) loginButton.getScene().getWindow();
        stage.setTitle("Chow Time - Dashboard");
        stage.setScene(scene);
        stage.centerOnScreen();
    }
}
