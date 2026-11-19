package com.chowtime.controllers;

import com.chowtime.models.*;
import com.chowtime.models.MenuItem;
import com.chowtime.services.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MenuController {

    @FXML
    private TabPane categoryTabPane;

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
    private TableColumn<MenuItem, String> allergenColumn;

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

    @FXML
    private TableColumn<OrderCartItem, String> cartItemOptionsColumn;

    private final MenuService menuService = new MenuService();
    private final OrderService orderService = new OrderService();
    private final EmployeeService employeeService = new EmployeeService();
    private final TableService tableService = new TableService();
    private final HelpRequestService helpRequestService = new HelpRequestService();

    private final ObservableList<OrderCartItem> cartItems = FXCollections.observableArrayList();

    private int tableID;
    private int waiterID;

    @FXML
    public void initialize(){

        // resize the tables columns
        itemNameColumn.prefWidthProperty().bind(menuTableView.widthProperty().multiply(1.0/4));
        categoryColumn.prefWidthProperty().bind(menuTableView.widthProperty().multiply(1.0/4));
        priceColumn.prefWidthProperty().bind(menuTableView.widthProperty().multiply(1.0/4));
        allergenColumn.prefWidthProperty().bind(menuTableView.widthProperty().multiply(1.0/4));

        cartItemNameColumn.prefWidthProperty().bind(cartTableView.widthProperty().multiply(1.0/6));
        cartQuantityColumn.prefWidthProperty().bind(cartTableView.widthProperty().multiply(1.0/6));
        cartPriceColumn.prefWidthProperty().bind(cartTableView.widthProperty().multiply(1.0/6));
        cartSubtotalColumn.prefWidthProperty().bind(cartTableView.widthProperty().multiply(1.0/6));
        cartItemOptionsColumn.prefWidthProperty().bind(cartTableView.widthProperty().multiply(2.0/6));

        // Connect the table columns to the model getters
        itemNameColumn.setCellValueFactory(new PropertyValueFactory<>("itemName"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("categoryName"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        allergenColumn.setCellValueFactory(new PropertyValueFactory<>("allergensDescription"));

        cartItemNameColumn.setCellValueFactory(new PropertyValueFactory<>("itemName"));
        cartQuantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        cartPriceColumn.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        cartSubtotalColumn.setCellValueFactory(new PropertyValueFactory<>("subtotal"));
        cartItemOptionsColumn.setCellValueFactory(new PropertyValueFactory<>("optionsDescription"));

        cartTableView.setItems(cartItems);

        loadCategories();
        updateTotal();
    }

    public void setOrderContext(int tableID, int waiterID, int tableNumber, String waiterName){
        this.tableID = tableID;
        this.waiterID = waiterID;
        tableInfoLabel.setText("Table " + tableNumber + " | Waiter: " + waiterName);
    }


    // Load the food categories and create a tab for each one
    private void loadCategories(){
        try {
            List<String> categories = menuService.getAvailableCategories();
            categoryTabPane.getTabs().clear();

            if (categories.isEmpty()){
                messageLabel.setText("No food categories available.");
                menuTableView.getItems().clear();
                return;
            }

            for (String category : categories){
                Tab tab = new Tab(category);
                tab.setOnSelectionChanged(event -> {
                    if (tab.isSelected()){
                        loadCategoryItems(category);
                    }
                });
                categoryTabPane.getTabs().add(tab);
            }

            // Select the first category when the page opens
            categoryTabPane.getSelectionModel().selectFirst();
            loadCategoryItems(categories.get(0));
        } catch (SQLException e){
            messageLabel.setText("Unable to load food categories.");
            e.printStackTrace();
        }
    }

    // Load food items belonging to the selected category
    private void loadCategoryItems(String category){
        try {
            List<MenuItem> items = menuService.getAvailableItemsByCategory(category);
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
            if (quantity < 1 || quantity > 255){
                messageLabel.setText("Quantity must be between 1 and 255.");
                return;
            }
        } catch (NumberFormatException e) {
            messageLabel.setText("Please enter a valid quantity.");
            return;
        }

        // If the food is customizable, open its customization dialog
        if (selectedItem.isCustomizable()){
            List<OptionGroup> optionGroups;

            try {
                optionGroups = menuService.getOptionGroupsForMenuItem(selectedItem.getId());
            } catch (SQLException e) {
                messageLabel.setText("Unable to load customization options");
                e.printStackTrace();
                return;
            }

            if (optionGroups.isEmpty()){
                messageLabel.setText("No customization options are available.");
                return;
            }

            List<MenuOption> selectedOptions = showCustomizationDialog(optionGroups);

            // Canceled dialog
            if (selectedOptions == null){
                return;
            }
            cartItems.add(new OrderCartItem(selectedItem, quantity, selectedOptions));
        } else {
            // Non-customizable item.
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
            cartItems.add(new OrderCartItem(selectedItem, quantity));
        }


        cartTableView.refresh();
        updateTotal();
        quantityField.setText("1");
        messageLabel.setText("Item added to the order.");
    }

    // handle showing a customizable dialog for food items that are customizable
    private List<MenuOption> showCustomizationDialog(List<OptionGroup> optionGroups){
        Dialog<ButtonType> dialog = new Dialog<>();

        dialog.setTitle("Customize Food Item");
        dialog.setHeaderText("Select your food customizations");

        ButtonType addButton = new ButtonType("Add to Order");
        ButtonType cancelButton = ButtonType.CANCEL;

        dialog.getDialogPane().getButtonTypes().addAll(
                addButton, cancelButton);

        VBox content = new VBox(15);
        content.setPadding(new javafx.geometry.Insets(10));

        Map<OptionGroup, List<CheckBox>> groupCheckBoxes = new HashMap<>();

        for (OptionGroup group : optionGroups) {

            VBox groupBox = new VBox(8);

            Label groupLabel = new Label(group.getGroupName()
                            + " (Select "
                            + group.getMinSelections()
                            + "-"
                            + group.getMaxSelections()
                            + ")");

            groupLabel.setStyle("-fx-font-weight: bold;");

            List<CheckBox> checkBoxes = new ArrayList<>();

            for (MenuOption option : group.getOptions()) {

                CheckBox checkBox = new CheckBox(option.getOptionName()
                                + " (+$"
                                + option.getPrice()
                                + ")");

                checkBox.setUserData(option);

                checkBoxes.add(checkBox);
                groupBox.getChildren().add(checkBox);
            }

            groupCheckBoxes.put(group, checkBoxes);

            content.getChildren().addAll(groupLabel, groupBox);
        }

        dialog.getDialogPane().setContent(content);

        // Prevent adding until selections are valid.
        javafx.scene.Node addButtonNode = dialog.getDialogPane().lookupButton(addButton);

        addButtonNode.setDisable(true);

        Runnable validateSelections = () -> {

            boolean valid = true;

            for (OptionGroup group : optionGroups) {

                List<CheckBox> checkBoxes =
                        groupCheckBoxes.get(group);

                int selectedCount = 0;

                for (CheckBox checkBox : checkBoxes) {
                    if (checkBox.isSelected()) {
                        selectedCount++;
                    }
                }

                if (selectedCount < group.getMinSelections() || selectedCount > group.getMaxSelections()) {
                    valid = false;
                    break;
                }
            }

            addButtonNode.setDisable(!valid);
        };

        for (OptionGroup group : optionGroups) {

            List<CheckBox> checkBoxes = groupCheckBoxes.get(group);

            for (CheckBox checkBox : checkBoxes) {
                checkBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
                            if (newValue) {

                                int selectedCount = 0;

                                for (CheckBox other : checkBoxes) {
                                    if (other.isSelected()) {
                                        selectedCount++;
                                    }
                                }

                                if (selectedCount > group.getMaxSelections()) {
                                    checkBox.setSelected(false);
                                }
                            }

                            validateSelections.run();
                        });
            }
        }

        validateSelections.run();

        dialog.getDialogPane().getButtonTypes();

        java.util.Optional<ButtonType> result = dialog.showAndWait();

        if (result.isEmpty() || !result.get().equals(addButton)) {
            return null;
        }

        List<MenuOption> selectedOptions = new ArrayList<>();

        for (OptionGroup group : optionGroups) {

            for (CheckBox checkBox : groupCheckBoxes.get(group)) {

                if (checkBox.isSelected()) {
                    selectedOptions.add((MenuOption) checkBox.getUserData());
                }
            }
        }

        return selectedOptions;
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

    @FXML
    private void handleRequestAssistance() {
        if (tableID <= 0) {
            messageLabel.setText("Table information is unavailable.");
            return;
        }
        try {
            helpRequestService.createHelpRequest(tableID);

            messageLabel.setText("Assistance requested.");
        } catch (SQLException e) {
            messageLabel.setText("Unable to request assistance.");
            e.printStackTrace();
        }
    }
}
