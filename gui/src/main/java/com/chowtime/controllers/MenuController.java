package com.chowtime.controllers;

import com.chowtime.models.Employee;
import com.chowtime.models.OrderCartItem;
import com.chowtime.services.EmployeeService;
import com.chowtime.services.MenuService;
import com.chowtime.services.OrderService;
import com.chowtime.services.TableService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import com.chowtime.models.MenuItem;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MenuController {

    @FXML
    private Label tableInfoLabel;

    @FXML
    private Label messageLabel;

    @FXML
    private Label totalLabel;

    @FXML
    private TextField quantityField;

    @FXML
    private TableView<MenuItem> menuTableView;

    @FXML
    private TableColumn<MenuItem, String> itemNameColumn;

    @FXML
    private TableColumn<MenuItem, String> categoryColumn;

    @FXML
    private TableColumn<MenuItem, BigDecimal> priceColumn;

    @FXML
    private TableView<OrderCartItem> cartTableView;

    @FXML
    private TableColumn<OrderCartItem, String> cartItemNameColumn;

    @FXML
    private TableColumn<OrderCartItem, Integer> cartQuantityColumn;

    @FXML
    private TableColumn<OrderCartItem, BigDecimal> cartPriceColumn;

    @FXML
    private TableColumn<OrderCartItem, BigDecimal> cartSubtotalColumn;

    private final MenuService menuService = new MenuService();
    private final OrderService orderService = new OrderService();
    private final EmployeeService employeeService = new EmployeeService();
    private final TableService tableService = new TableService();

    private final ObservableList<OrderCartItem> cartItems = FXCollections.observableArrayList();

    private int tableID;
    private int waiterID;

    @FXML
    public void initialize(){

        // resize the tables columns
        itemNameColumn.prefWidthProperty().bind(menuTableView.widthProperty().multiply(1.0/3));
        categoryColumn.prefWidthProperty().bind(menuTableView.widthProperty().multiply(1.0/3));
        priceColumn.prefWidthProperty().bind(menuTableView.widthProperty().multiply(1.0/3));

        cartItemNameColumn.prefWidthProperty().bind(cartTableView.widthProperty().multiply(1.0/4));
        cartQuantityColumn.prefWidthProperty().bind(cartTableView.widthProperty().multiply(1.0/4));
        cartPriceColumn.prefWidthProperty().bind(cartTableView.widthProperty().multiply(1.0/4));
        cartSubtotalColumn.prefWidthProperty().bind(cartTableView.widthProperty().multiply(1.0/4));

        // Connect the table columns to the model getters
        itemNameColumn.setCellValueFactory(new PropertyValueFactory<>("itemName"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("categoryName"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));

        cartItemNameColumn.setCellValueFactory(new PropertyValueFactory<>("itemName"));
        cartQuantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        cartPriceColumn.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        cartSubtotalColumn.setCellValueFactory(new PropertyValueFactory<>("subtotal"));

        cartTableView.setItems(cartItems);

        loadMenuItems();
        updateTotal();
    }

    public void setOrderContext(int tableID, int waiterID, int tableNumber, String waiterName){
        this.tableID = tableID;
        this.waiterID = waiterID;
        tableInfoLabel.setText("Table " + tableNumber + " | Waiter: " + waiterName);
    }

    private void loadMenuItems(){
        try {
            List<MenuItem> items = menuService.getAvailableItems();
            menuTableView.setItems(FXCollections.observableArrayList(items));
            messageLabel.setText(items.size() + " food item(s) available.");
        } catch (SQLException e){
            messageLabel.setText("Unable to load food items.");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAddItem(){
        MenuItem selectedItem = menuTableView.getSelectionModel().getSelectedItem();

        if (selectedItem == null){
            messageLabel.setText("Please select a food item.");
            return;
        }

        int quantity;

        try {
            quantity = Integer.parseInt(quantityField.getText().trim());
            if (quantity <= 0 || quantity > 255){
                messageLabel.setText("Quantity must be between 1 and 255.");
                return;
            }
        } catch (NumberFormatException e) {
            messageLabel.setText("Please enter a valid quantity.");
            return;
        }

        for (OrderCartItem cartItem : cartItems){
            if (cartItem.getMenuItem().getId() == selectedItem.getId()){
                int newQuantity = cartItem.getQuantity() + quantity;
                if (newQuantity > 255){
                    messageLabel.setText("Maximum quantity per item is 255.");
                    return;
                }
                cartItem.setQuantity(newQuantity);
                cartTableView.refresh();
                updateTotal();
                messageLabel.setText("Item quantity updated.");
                return;
            }
        }

        // Add a new item to the cart
        cartItems.add(new OrderCartItem(selectedItem, quantity));
        updateTotal();
        messageLabel.setText("Item added to the order.");
        quantityField.setText("1");
    }

    @FXML
    private void handleRemoveItem(){
        OrderCartItem selectedItem = cartTableView.getSelectionModel().getSelectedItem();
        if (selectedItem == null){
            messageLabel.setText("Please select an item to remove.");
            return;
        }

        cartItems.remove(selectedItem);
        updateTotal();
        messageLabel.setText("Item removed from the order.");
    }

    private void updateTotal(){
        BigDecimal total = BigDecimal.ZERO;
        for (OrderCartItem item : cartItems){
            total = total.add(item.getSubtotal());
        }
        totalLabel.setText("Total: $" + total.toPlainString());
    }

    @FXML
    private void handlePlaceOrder(){
        if (cartItems.isEmpty()){
            messageLabel.setText("Please add at least one food item.");
            return;
        }

        if (tableID <= 0 || waiterID <= 0){
            messageLabel.setText("Table or waiter information is missing.");
            return;
        }

        try {
            int orderID = orderService.createOrder(tableID, waiterID, new ArrayList<>(cartItems));
            cartItems.clear();
            updateTotal();
            messageLabel.setText("Order #" + orderID + " created successfully.");
        } catch (SQLException e){
            messageLabel.setText("Unable to save the order.");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCloseTable(){
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Close Table");
        dialog.setHeaderText("Employee authentication is required to close this table.");

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        VBox content = new VBox(10,
                new Label("Username:"),
                usernameField,
                new Label("Password:"),
                passwordField
        );

        content.setPadding(new javafx.geometry.Insets(15));
        dialog.getDialogPane().setContent(content);

        ButtonType confirmButton = new ButtonType("Confirm");
        ButtonType cancelButton = ButtonType.CANCEL;

        dialog.getDialogPane().getButtonTypes().addAll(confirmButton, cancelButton);

        var result = dialog.showAndWait();

        if (result.isEmpty() || result.get() != confirmButton){
            return;
        }

        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()){
            messageLabel.setText("Please enter your username and password.");
            return;
        }

        try {
            // Verify the employee's credentials
            Employee employee = employeeService.login(username, password);

            if (employee == null){
                messageLabel.setText("Incorrect username or password.");
                return;
            }

            // Only managers and waiters can close a table
            if (!employee.getRoleName().equalsIgnoreCase("Manager")
                    && !employee.getRoleName().equalsIgnoreCase("waiter")){
                messageLabel.setText("You are not authorized to close tables.");
                return;
            }

            // Update the table in the database
            boolean closed = tableService.closeTable(tableID);

            if (!closed){
                messageLabel.setText("Unable to close table.");
                return;
            }

            // Return to the restaurant tables page
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/chowtime/tables.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) menuTableView.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Chow Time - Restaurant Tables");
            stage.show();
        } catch (SQLException | IOException e){
            messageLabel.setText("Unable to close table.");
            e.printStackTrace();
        }
    }
}
