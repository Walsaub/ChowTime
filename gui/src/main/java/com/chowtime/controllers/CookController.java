package com.chowtime.controllers;

import com.chowtime.models.KitchenOrder;
import com.chowtime.models.KitchenOrderItem;
import com.chowtime.services.KitchenService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class CookController {
    @FXML
    private TableView<KitchenOrder> ordersTable;

    @FXML
    private TableColumn<KitchenOrder, Integer> orderIdColumn;

    @FXML
    private TableColumn<KitchenOrder, Integer> tableNumberColumn;

    @FXML
    private TableColumn<KitchenOrder, String> waiterColumn;

    @FXML
    private TableColumn<KitchenOrder, String> statusColumn;

    @FXML
    private TableColumn<KitchenOrder, String> dateColumn;

    @FXML
    private TableView<KitchenOrderItem> itemsTable;

    @FXML
    private TableColumn<KitchenOrderItem, String> itemNameColumn;

    @FXML
    private TableColumn<KitchenOrderItem, Integer> quantityColumn;

    @FXML
    private TableColumn<KitchenOrderItem, String> optionsColumn;

    @FXML
    private ComboBox<String> statusComboBox;

    @FXML
    private Label messageLabel;

    private final KitchenService kitchenService = new KitchenService();

    @FXML
    public void initialize() {

        orderIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        tableNumberColumn.setCellValueFactory(new PropertyValueFactory<>("tableNumber"));
        waiterColumn.setCellValueFactory(new PropertyValueFactory<>("waiterName"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("orderStatus"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("orderDateTime"));

        itemNameColumn.setCellValueFactory(new PropertyValueFactory<>("itemName"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        optionsColumn.setCellValueFactory(new PropertyValueFactory<>("options"));

        statusComboBox.setItems(FXCollections.observableArrayList(
                "preparing", "ready", "completed", "cancelled"
        ));

        ordersTable.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldOrder, newOrder) -> {

                    if (newOrder != null) {
                        itemsTable.setItems(
                                FXCollections.observableArrayList(
                                        newOrder.getItems()
                                )
                        );

                        statusComboBox.setValue(null);
                        messageLabel.setText(
                                "Selected Order #" + newOrder.getId());
                    } else {
                        itemsTable.getItems().clear();
                    }
                });

        loadOrders();
    }

    private void loadOrders() {

        try {
            List<KitchenOrder> orders = kitchenService.getActiveOrders();

            ordersTable.setItems(FXCollections.observableArrayList(orders));

            itemsTable.getItems().clear();
            statusComboBox.setValue(null);

            messageLabel.setText(orders.size() + " active order(s).");
        } catch (SQLException e) {
            messageLabel.setText("Unable to load orders.");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleRefresh() {
        loadOrders();
    }

    @FXML
    private void handleUpdateStatus() {

        KitchenOrder selectedOrder = ordersTable.getSelectionModel().getSelectedItem();

        if (selectedOrder == null) {
            messageLabel.setText("Please select an order.");
            return;
        }

        String newStatus = statusComboBox.getValue();

        if (newStatus == null) {
            messageLabel.setText("Please select a status.");
            return;
        }

        try {

            kitchenService.updateOrderStatus(selectedOrder.getId(), newStatus);

            messageLabel.setText("Order #" + selectedOrder.getId() + " updated to " + newStatus + ".");
            loadOrders();
        } catch (SQLException | IllegalArgumentException e) {

            messageLabel.setText(e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogout() {

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/chowtime/tables.fxml")
            );

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