package com.chowtime.controllers;

import com.chowtime.models.Employee;
import com.chowtime.services.EmployeeService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
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
                

                //TODO: Open the dashboard
                String fxmlPath = switch (employee.getRoleName()) {
                    case "Manager" -> "/com/chowtime/manager-dashboard.fxml";
                    case "Waiter" -> "/com/chowtime/waiter-dashboard.fxml";
                    case "Cook" -> "/com/chowtime/cook-dashboard.fxml";
                    default -> null;
                };

                FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
                Scene scene = new Scene(loader.load());
                Stage stage = (Stage) passwordField.getScene().getWindow();
                stage.setTitle("Chow Time - Dashboard");
                stage.setScene(scene);
                stage.centerOnScreen();
            } else {
                messageLabel.setText("Incorrect username or password.");
                passwordField.clear();
            }
        } catch (SQLException | IOException e){
            messageLabel.setText("Unable to connect to the database.");
            e.printStackTrace();
        }
    }
}
