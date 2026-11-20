package com.chowtime.controllers;

import com.chowtime.database.DatabaseConnection;
import com.chowtime.services.DatabaseService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
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
            boolean sampleDataInserted = databaseService.initializeDatabase();

            openMainWindow();

            if (sampleDataInserted) {
                showTemporaryEmployeeCredentials();
            }
        } catch (Exception e){
            statusLabel.setText("Connection failed. check your credentials.");
            passwordField.clear();
            DatabaseConnection.configure("", "");
            e.printStackTrace();
        } finally {
            loginButton.setDisable(false);
        }
    }

    private void showTemporaryEmployeeCredentials() {

        Alert alert = new Alert(Alert.AlertType.INFORMATION);

        alert.setTitle("Chow Time - Temporary Accounts");
        alert.setHeaderText("Sample employees have been created!");

        alert.setContentText(
                "Use the following temporary credentials to test the application:\n\n"
                        + "WAITER\n"
                        + "Username: johnsmith\n"
                        + "Password: 1234\n\n"
                        + "COOK\n"
                        + "Username: davidbrown\n"
                        + "Password: 1234\n\n"
                        + "MANAGER\n"
                        + "Username: sarahwilson\n"
                        + "Password: 1234\n\n"
                        + "These credentials are for testing only."
        );

        alert.showAndWait();
    }

    //move to the main window
    private void openMainWindow() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/chowtime/tables.fxml"));
        Scene scene = new Scene(loader.load());
        Stage stage = (Stage) loginButton.getScene().getWindow();
        stage.setTitle("Chow Time - Restaurant Tables");
        stage.setScene(scene);
        stage.centerOnScreen();
    }
}
