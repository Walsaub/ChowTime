package com.chowtime.controllers;

import com.chowtime.models.Employee;
import com.chowtime.services.EmployeeService;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.sql.SQLException;

public class EmployeeLoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label messageLabel;

    private final EmployeeService employeeService = new EmployeeService();

    @FXML
    private void handleLogin(){
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()){
            messageLabel.setText("Please enter your username and password.");
            return;
        }

        try {
            Employee employee = employeeService.login(username, password);

            if (employee != null){
                messageLabel.setText("");

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Login Successful");
                alert.setHeaderText("Welcome to Chow Time!");
                alert.setContentText("Welcome, " + employee.getFirstName());
                alert.showAndWait();

                //TODO: Open the dashboard
            } else {
                messageLabel.setText("Incorrect username or password.");
                passwordField.clear();
            }
        } catch (SQLException e){
            messageLabel.setText("Unable to connect to the database.");
            e.printStackTrace();
        }
    }
}
