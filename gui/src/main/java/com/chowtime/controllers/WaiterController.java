package com.chowtime.controllers;

import com.chowtime.models.KitchenOrderItem;
import com.chowtime.models.WaiterHelpRequest;
import com.chowtime.models.WaiterOrder;
import com.chowtime.models.WaiterTable;
import com.chowtime.services.WaiterService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

public class WaiterController {
    @FXML
    private Label welcomeLabel;

    @FXML
    private Label messageLabel;

    // Orders

    @FXML
    private TableView<WaiterOrder> ordersTable;

    @FXML
    private TableColumn<WaiterOrder, Integer> orderIdColumn;

    @FXML
    private TableColumn<WaiterOrder, Integer> orderTableNumberColumn;

    @FXML
    private TableColumn<WaiterOrder, String> orderStatusColumn;

    @FXML
    private TableColumn<WaiterOrder, String> orderDateColumn;

    @FXML
    private TableView<KitchenOrderItem> orderItemsTable;

    @FXML
    private TableColumn<KitchenOrderItem, String> orderItemNameColumn;

    @FXML
    private TableColumn<KitchenOrderItem, Integer> orderItemQuantityColumn;

    @FXML
    private TableColumn<KitchenOrderItem, String> orderItemOptionsColumn;

    // Tables

    @FXML
    private TableView<WaiterTable> tablesTable;

    @FXML
    private TableColumn<WaiterTable, Integer> tableNumberColumn;

    @FXML
    private TableColumn<WaiterTable, Integer> tableCapacityColumn;

    @FXML
    private TableColumn<WaiterTable, String> tableStatusColumn;

    // Help requests

    @FXML
    private TableView<WaiterHelpRequest> helpRequestsTable;

    @FXML
    private TableColumn<WaiterHelpRequest, Integer> helpRequestIdColumn;

    @FXML
    private TableColumn<WaiterHelpRequest, Integer> helpTableNumberColumn;

    @FXML
    private TableColumn<WaiterHelpRequest, String> helpRequestTimeColumn;

    @FXML
    private TableColumn<WaiterHelpRequest, String> helpRequestStatusColumn;

    private final WaiterService waiterService = new WaiterService();

    private int employeeID;
    private String waiterName;

    public void setWaiterContext(int employeeID, String waiterName){
        this.employeeID = employeeID;
        this.waiterName = waiterName;
        welcomeLabel.setText(("Welcome, " + waiterName));
        loadDashboard();
    }

    @FXML
    public void initialize(){
        // Orders
        orderIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        orderTableNumberColumn.setCellValueFactory(new PropertyValueFactory<>("tableNumber"));
        orderStatusColumn.setCellValueFactory(new PropertyValueFactory<>("orderStatus"));
        orderDateColumn.setCellValueFactory(new PropertyValueFactory<>("orderDateTime"));

        orderIdColumn.prefWidthProperty().bind(ordersTable.widthProperty().multiply(1.0/4));
        orderTableNumberColumn.prefWidthProperty().bind(ordersTable.widthProperty().multiply(1.0/4));
        orderStatusColumn.prefWidthProperty().bind(ordersTable.widthProperty().multiply(1.0/4));
        orderDateColumn.prefWidthProperty().bind(ordersTable.widthProperty().multiply(1.0/4));

        // Order items
        orderItemNameColumn.setCellValueFactory(new PropertyValueFactory<>("itemName"));
        orderItemQuantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        orderItemOptionsColumn.setCellValueFactory(new PropertyValueFactory<>("options"));

        orderItemNameColumn.prefWidthProperty().bind(orderItemsTable.widthProperty().multiply(1.0/3));
        orderItemQuantityColumn.prefWidthProperty().bind(orderItemsTable.widthProperty().multiply(1.0/3));
        orderItemOptionsColumn.prefWidthProperty().bind(orderItemsTable.widthProperty().multiply(1.0/3));

        // Tables
        tableNumberColumn.setCellValueFactory(new PropertyValueFactory<>("tableNumber"));
        tableCapacityColumn.setCellValueFactory(new PropertyValueFactory<>("capacity"));
        tableStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        tableNumberColumn.prefWidthProperty().bind(tablesTable.widthProperty().multiply(1.0/3));
        tableCapacityColumn.prefWidthProperty().bind(tablesTable.widthProperty().multiply(1.0/3));
        tableStatusColumn.prefWidthProperty().bind(tablesTable.widthProperty().multiply(1.0/3));

        // Help requests
        helpRequestIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        helpTableNumberColumn.setCellValueFactory(new PropertyValueFactory<>("tableNumber"));
        helpRequestTimeColumn.setCellValueFactory(new PropertyValueFactory<>("requestTime"));
        helpRequestStatusColumn.setCellValueFactory(new PropertyValueFactory<>("requestStatus"));

        helpRequestIdColumn.prefWidthProperty().bind(helpRequestsTable.widthProperty().multiply(1.0/4));
        helpTableNumberColumn.prefWidthProperty().bind(helpRequestsTable.widthProperty().multiply(1.0/4));
        helpRequestTimeColumn.prefWidthProperty().bind(helpRequestsTable.widthProperty().multiply(1.0/4));
        helpRequestStatusColumn.prefWidthProperty().bind(helpRequestsTable.widthProperty().multiply(1.0/4));

        // Show items belonging to selected order
        ordersTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldOrder, newOrder) -> {
                    if (newOrder != null){
                        orderItemsTable.setItems(FXCollections.observableArrayList(newOrder.getItems()));
                    } else {
                        orderItemsTable.getItems().clear();
                    }
                });
    }

    private void loadDashboard(){
        if (employeeID <= 0){
            messageLabel.setText("Waiter account not initialized.");
            return;
        }

        loadTables();
        loadOrders();
        loadHelpRequests();
    }

    private void loadTables(){
        try {
            tablesTable.setItems(FXCollections.observableArrayList(waiterService.getAssignedTables(employeeID)));
        } catch (SQLException e) {
            messageLabel.setText("Unable to load assigned tables.");
            e.printStackTrace();
        }
    }

    private void loadOrders(){
        try {
            ordersTable.setItems(FXCollections.observableArrayList(waiterService.getAssignedOrders(employeeID)));
            orderItemsTable.getItems().clear();
        } catch (SQLException e) {
            messageLabel.setText("Unable to load orders.");
            e.printStackTrace();
        }
    }

    private void loadHelpRequests(){
        try {
            helpRequestsTable.setItems(FXCollections.observableArrayList(waiterService.getHelpRequests(employeeID)));
        } catch (SQLException e) {
            messageLabel.setText("Unable to load help requests.");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleRefresh(){
        loadDashboard();
        messageLabel.setText("Dashboard refreshed.");
    }

    @FXML
    private void handleOpenFoodOrdering(){
        WaiterTable selectedTable = tablesTable.getSelectionModel().getSelectedItem();
        if (selectedTable == null){
            messageLabel.setText("Please select a table.");
            return;
        }

        if (!"Occupied".equalsIgnoreCase(selectedTable.getStatus())){
            messageLabel.setText("Assign a waiter to the table before ordering.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/chowtime/menu.fxml"));
            Scene scene = new Scene(loader.load());
            MenuController menuController = loader.getController();
            menuController.setOrderContext(
                    selectedTable.getId(),
                    employeeID,
                    selectedTable.getTableNumber(),
                    waiterName
            );
            Stage stage = (Stage) tablesTable.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Food Selection");
        } catch (IOException e){
            messageLabel.setText("Unable to open food ordering.");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleMarkRequestHandled(){
        WaiterHelpRequest selectedRequest = helpRequestsTable.getSelectionModel().getSelectedItem();
        if (selectedRequest == null){
            messageLabel.setText("Please select a help request.");
            return;
        }

        try {
            waiterService.handleHelpRequest(selectedRequest.getId(), employeeID);
            loadHelpRequests();
            messageLabel.setText("Help request marked as handled.");
        } catch (SQLException e){
            messageLabel.setText("Unable to update help request.");
            e.printStackTrace();
        }
    }
}
