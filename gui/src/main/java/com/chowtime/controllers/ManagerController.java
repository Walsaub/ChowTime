package com.chowtime.controllers;

import com.chowtime.models.ManagerEmployee;
import com.chowtime.models.ManagerFoodItem;
import com.chowtime.models.MostOrderedFood;
import com.chowtime.services.EmployeeService;
import com.chowtime.services.ManagerService;
import com.chowtime.services.MenuService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;

public class ManagerController {

    @FXML private Label messageLabel;

    // Employees

    @FXML
    private TableView<ManagerEmployee> employeesTable;
    @FXML private TableColumn<ManagerEmployee, Integer> employeeIDColumn;
    @FXML private TableColumn<ManagerEmployee, String> employeeFirstNameColumn;
    @FXML private TableColumn<ManagerEmployee, String> employeeLastNameColumn;
    @FXML private TableColumn<ManagerEmployee, String> employeeUsernameColumn;
    @FXML private TableColumn<ManagerEmployee, String> employeeRoleColumn;

    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private ComboBox<String> roleComboBox;

    // Food Items

    @FXML
    private TableView<ManagerFoodItem> foodItemsTable;
    @FXML private TableColumn<ManagerFoodItem, Integer> foodIDColumn;
    @FXML private TableColumn<ManagerFoodItem, String> foodNameColumn;
    @FXML private TableColumn<ManagerFoodItem, String> foodCategoryColumn;
    @FXML private TableColumn<ManagerFoodItem, BigDecimal> foodPriceColumn;
    @FXML private TableColumn<ManagerFoodItem, Boolean> foodAvailableColumn;
    @FXML private TableColumn<ManagerFoodItem, Boolean> foodCustomizableColumn;

    @FXML private TextField foodNameField;
    @FXML private TextField foodDescriptionField;
    @FXML private TextField foodPriceField;
    @FXML private ComboBox<String> foodCategoryComboBox;
    @FXML private CheckBox foodAvailableCheckBox;
    @FXML private CheckBox foodCustomizableCheckBox;

    // Report

    @FXML
    private TableView<MostOrderedFood> mostOrderedTable;
    @FXML private TableColumn<MostOrderedFood, String> mostOrderedNameColumn;
    @FXML private TableColumn<MostOrderedFood, Integer> mostOrderedQuantityColumn;

    private final EmployeeService employeeService = new EmployeeService();
    private final MenuService menuService = new MenuService();
    private final ManagerService managerService = new ManagerService();

    @FXML
    public void initialize(){
        configureEmployeeTable();
        configureFoodTable();
        configureReportTable();

        employeesTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldEmployee, newEmployee) -> {

                    if (newEmployee != null) {
                        populateEmployeeFields(newEmployee);
                    }
                });

        foodItemsTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldItem, newItem) -> {

                    if (newItem != null) {
                        populateFoodFields(newItem);
                    }
                });

        loadDashboard();
    }

    private void configureEmployeeTable(){
        employeeIDColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        employeeFirstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        employeeLastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        employeeUsernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        employeeRoleColumn.setCellValueFactory(new PropertyValueFactory<>("roleName"));

        employeeIDColumn.prefWidthProperty().bind(employeesTable.widthProperty().multiply(1.0/5));
        employeeFirstNameColumn.prefWidthProperty().bind(employeesTable.widthProperty().multiply(1.0/5));
        employeeLastNameColumn.prefWidthProperty().bind(employeesTable.widthProperty().multiply(1.0/5));
        employeeUsernameColumn.prefWidthProperty().bind(employeesTable.widthProperty().multiply(1.0/5));
        employeeRoleColumn.prefWidthProperty().bind(employeesTable.widthProperty().multiply(1.0/5));
    }

    private void configureFoodTable() {

        foodIDColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        foodNameColumn.setCellValueFactory(new PropertyValueFactory<>("itemName"));
        foodCategoryColumn.setCellValueFactory(new PropertyValueFactory<>("categoryName"));
        foodPriceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        foodAvailableColumn.setCellValueFactory(new PropertyValueFactory<>("available"));
        foodCustomizableColumn.setCellValueFactory(new PropertyValueFactory<>("customizable"));

        foodIDColumn.prefWidthProperty().bind(foodItemsTable.widthProperty().multiply(1.0/6));
        foodNameColumn.prefWidthProperty().bind(foodItemsTable.widthProperty().multiply(1.0/6));
        foodCategoryColumn.prefWidthProperty().bind(foodItemsTable.widthProperty().multiply(1.0/6));
        foodPriceColumn.prefWidthProperty().bind(foodItemsTable.widthProperty().multiply(1.0/6));
        foodAvailableColumn.prefWidthProperty().bind(foodItemsTable.widthProperty().multiply(1.0/6));
        foodCustomizableColumn.prefWidthProperty().bind(foodItemsTable.widthProperty().multiply(1.0/6));
    }

    private void configureReportTable() {

        mostOrderedNameColumn.setCellValueFactory(new PropertyValueFactory<>("itemName"));
        mostOrderedQuantityColumn.setCellValueFactory(new PropertyValueFactory<>("totalQuantity"));

        mostOrderedNameColumn.prefWidthProperty().bind(mostOrderedTable.widthProperty().multiply(1.0/2));
        mostOrderedQuantityColumn.prefWidthProperty().bind(mostOrderedTable.widthProperty().multiply(1.0/2));
    }

    private void loadDashboard() {
        loadEmployees();
        loadRoles();
        loadFoodItems();
        loadCategories();
        loadReport();
    }

    // EMPLOYEE MANAGEMENT

    private void loadEmployees() {
        try {
            employeesTable.setItems(FXCollections.observableArrayList(employeeService.getAllEmployees()));
        } catch (SQLException e) {
            messageLabel.setText("Unable to load employees.");
            e.printStackTrace();
        }
    }

    private void loadRoles() {
        try {
            roleComboBox.setItems(FXCollections.observableArrayList(employeeService.getRoleNames()));
        } catch (SQLException e) {
            messageLabel.setText("Unable to load employee roles.");
            e.printStackTrace();
        }
    }

    private void populateEmployeeFields(ManagerEmployee employee) {

        firstNameField.setText(employee.getFirstName());
        lastNameField.setText(employee.getLastName());
        usernameField.setText(employee.getUsername());
        roleComboBox.setValue(employee.getRoleName());

        // Password is intentionally not loaded.
        passwordField.clear();
    }

    @FXML
    private void handleAddEmployee() {
        if (!validateEmployeeFields(true)) {
            return;
        }
        try {
            if (employeeService.usernameExists(usernameField.getText().trim())) {
                messageLabel.setText("Username already exists.");
                return;
            }

            int roleID = employeeService.getRoleID(roleComboBox.getValue());

            employeeService.addEmployee(
                    roleID,
                    firstNameField.getText().trim(),
                    lastNameField.getText().trim(),
                    usernameField.getText().trim(),
                    passwordField.getText()
            );

            loadEmployees();
            clearEmployeeFields();

            messageLabel.setText("Employee added.");

        } catch (SQLException e) {
            messageLabel.setText("Unable to add employee.");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleUpdateEmployee() {

        ManagerEmployee selectedEmployee = employeesTable.getSelectionModel().getSelectedItem();

        if (selectedEmployee == null) {
            messageLabel.setText("Please select an employee.");
            return;
        }

        if (!validateEmployeeFields(false)) {
            return;
        }

        try {

            String username = usernameField.getText().trim();

            if (!username.equals(selectedEmployee.getUsername()) && employeeService.usernameExists(username)) {

                messageLabel.setText("Username already exists.");
                return;
            }

            int roleID = employeeService.getRoleID(roleComboBox.getValue());

            employeeService.updateEmployee(
                    selectedEmployee.getId(),
                    roleID,
                    firstNameField.getText().trim(),
                    lastNameField.getText().trim(),
                    username
            );

            loadEmployees();

            messageLabel.setText("Employee updated.");

        } catch (SQLException e) {
            messageLabel.setText("Unable to update employee.");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleUpdatePassword() {

        ManagerEmployee selectedEmployee = employeesTable.getSelectionModel().getSelectedItem();

        if (selectedEmployee == null) {
            messageLabel.setText("Please select an employee.");
            return;
        }

        String password = passwordField.getText();

        if (password == null || password.isBlank()) {
            messageLabel.setText("Enter a new password.");
            return;
        }

        try {

            employeeService.updateEmployeePassword(selectedEmployee.getId(), password);
            passwordField.clear();

            messageLabel.setText("Password updated.");

        } catch (SQLException e) {
            messageLabel.setText("Unable to update password.");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleDeleteEmployee() {

        ManagerEmployee selectedEmployee = employeesTable.getSelectionModel().getSelectedItem();

        if (selectedEmployee == null) {
            messageLabel.setText("Please select an employee.");
            return;
        }

        if (!confirmAction("Delete this employee?")) {
            return;
        }

        try {
            employeeService.deleteEmployee(selectedEmployee.getId());

            loadEmployees();
            clearEmployeeFields();

            messageLabel.setText("Employee deleted.");
        } catch (SQLException e) {
            messageLabel.setText("Unable to delete employee. They may have existing records.");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleClearEmployeeFields() {
        clearEmployeeFields();
    }

    private void clearEmployeeFields() {

        employeesTable.getSelectionModel().clearSelection();

        firstNameField.clear();
        lastNameField.clear();
        usernameField.clear();
        passwordField.clear();
        roleComboBox.setValue(null);
    }

    private boolean validateEmployeeFields(boolean requirePassword) {

        if (firstNameField.getText().isBlank()
                || lastNameField.getText().isBlank()
                || usernameField.getText().isBlank()
                || roleComboBox.getValue() == null) {

            messageLabel.setText("Complete all employee fields.");
            return false;
        }

        if (requirePassword && passwordField.getText().isBlank()) {
            messageLabel.setText("Enter an employee password.");
            return false;
        }

        return true;
    }

    // FOOD ITEM MANAGEMENT

    private void loadFoodItems() {
        try {
            foodItemsTable.setItems(FXCollections.observableArrayList(menuService.getAllFoodItems()));
        } catch (SQLException e) {
            messageLabel.setText("Unable to load food items.");
            e.printStackTrace();
        }
    }

    private void loadCategories() {
        try {
            foodCategoryComboBox.setItems(FXCollections.observableArrayList(menuService.getAllCategoryNames()));
        } catch (SQLException e) {
            messageLabel.setText("Unable to load categories.");
            e.printStackTrace();
        }
    }

    private void populateFoodFields(ManagerFoodItem item) {

        foodNameField.setText(item.getItemName());
        foodDescriptionField.setText(item.getDescription());
        foodPriceField.setText(item.getPrice().toString());

        foodCategoryComboBox.setValue(item.getCategoryName());

        foodAvailableCheckBox.setSelected(item.isAvailable());
        foodCustomizableCheckBox.setSelected(item.isCustomizable());
    }

    @FXML
    private void handleAddFoodItem() {

        if (!validateFoodFields()) {
            return;
        }

        try {
            int categoryID = menuService.getCategoryID(foodCategoryComboBox.getValue());

            menuService.addFoodItem(
                    foodNameField.getText().trim(),
                    foodDescriptionField.getText().trim(),
                    new BigDecimal(foodPriceField.getText().trim()),
                    categoryID,
                    foodAvailableCheckBox.isSelected(),
                    foodCustomizableCheckBox.isSelected()
            );

            loadFoodItems();
            clearFoodFields();

            messageLabel.setText("Food item added.");

        } catch (SQLException | NumberFormatException e) {
            messageLabel.setText("Unable to add food item.");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleUpdateFoodItem() {

        ManagerFoodItem selectedItem = foodItemsTable.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            messageLabel.setText("Please select a food item.");
            return;
        }

        if (!validateFoodFields()) {
            return;
        }

        try {
            int categoryID = menuService.getCategoryID(foodCategoryComboBox.getValue());

            menuService.updateFoodItem(
                    selectedItem.getId(),
                    foodNameField.getText().trim(),
                    foodDescriptionField.getText().trim(),
                    new BigDecimal(foodPriceField.getText().trim()),
                    categoryID,
                    foodAvailableCheckBox.isSelected(),
                    foodCustomizableCheckBox.isSelected()
            );

            loadFoodItems();

            messageLabel.setText("Food item updated.");

        } catch (SQLException | NumberFormatException e) {
            messageLabel.setText("Unable to update food item.");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleDeleteFoodItem() {
        ManagerFoodItem selectedItem = foodItemsTable.getSelectionModel().getSelectedItem();

        if (selectedItem == null) {
            messageLabel.setText("Please select a food item.");
            return;
        }

        if (!confirmAction("Delete this food item?")) {
            return;
        }

        try {
            menuService.deleteFoodItem(selectedItem.getId());

            loadFoodItems();
            clearFoodFields();

            messageLabel.setText("Food item deleted.");
        } catch (SQLException e) {
            messageLabel.setText("Unable to delete food item. It may have existing orders or options.");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleClearFoodFields() {
        clearFoodFields();
    }

    private void clearFoodFields() {

        foodItemsTable.getSelectionModel().clearSelection();

        foodNameField.clear();
        foodDescriptionField.clear();
        foodPriceField.clear();
        foodCategoryComboBox.setValue(null);

        foodAvailableCheckBox.setSelected(true);
        foodCustomizableCheckBox.setSelected(false);
    }

    private boolean validateFoodFields() {

        if (foodNameField.getText().isBlank()
                || foodDescriptionField.getText().isBlank()
                || foodPriceField.getText().isBlank()
                || foodCategoryComboBox.getValue() == null) {

            messageLabel.setText("Complete all food item fields.");
            return false;
        }

        try {
            BigDecimal price = new BigDecimal(foodPriceField.getText().trim());

            if (price.compareTo(BigDecimal.ZERO) < 0) {
                messageLabel.setText("Price cannot be negative.");
                return false;
            }
        } catch (NumberFormatException e) {
            messageLabel.setText("Enter a valid price.");
            return false;
        }

        return true;
    }

    // REPORT

    private void loadReport() {
        try {
            mostOrderedTable.setItems(FXCollections.observableArrayList(managerService.getMostOrderedFood()));
        } catch (SQLException e) {
            messageLabel.setText("Unable to load report.");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleRefreshReport() {
        loadReport();
    }

    // GENERAL

    @FXML
    private void handleRefresh() {
        loadDashboard();
        messageLabel.setText("Dashboard refreshed.");
    }

    private boolean confirmAction(String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);

        alert.setTitle("Confirmation");
        alert.setHeaderText(null);
        alert.setContentText(message);

        return alert.showAndWait().filter(response ->
                        response == ButtonType.OK)
                .isPresent();
    }

    @FXML
    private void handleLogout() {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/chowtime/tables.fxml"));

            Scene scene = new Scene(loader.load());

            Stage stage = (Stage) messageLabel.getScene().getWindow();

            stage.setScene(scene);
            stage.setTitle("Chow Time - Restaurant Tables");
            stage.centerOnScreen();

        } catch (IOException e) {
            messageLabel.setText("Unable to log out.");
            e.printStackTrace();
        }
    }

}
