package com.chowtime.controllers;

import com.chowtime.models.Employee;
import com.chowtime.models.RestaurantTable;
import com.chowtime.services.TableService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.SQLException;
import java.util.List;

public class TableController {

    @FXML
    private TableView<RestaurantTable> tablesTableView;

    @FXML
    private TableColumn<RestaurantTable, Integer> tableNumberColumn;

    @FXML
    private TableColumn<RestaurantTable, Integer> capacityColumn;

    @FXML
    private TableColumn<RestaurantTable, String> statusColumn;

    @FXML
    private ComboBox<Employee> waiterComboBox;

    @FXML
    private Label selectedTableLabel;

    @FXML
    private Label messageLabel;

    private final TableService tableService = new TableService();

    // populate the table of the restaurant tables when the view run
    @FXML
    public void initialize(){

        tableNumberColumn.prefWidthProperty().bind(tablesTableView.widthProperty().multiply(1.0/3));
        capacityColumn.prefWidthProperty().bind(tablesTableView.widthProperty().multiply(1.0/3));
        statusColumn.prefWidthProperty().bind(tablesTableView.widthProperty().multiply(1.0/3));

        //Connect the table columns to the model getters
        tableNumberColumn.setCellValueFactory(new PropertyValueFactory<>("tableNumber"));
        capacityColumn.setCellValueFactory(new PropertyValueFactory<>("capacity"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        tablesTableView.getSelectionModel().selectedItemProperty().
                addListener( (observable, oldTable, newTable) -> {
            if (newTable != null){
                selectedTableLabel.setText("Selected Table: " + newTable.getTableNumber());
            } else {
                selectedTableLabel.setText("No table selected");
            }
        });

        loadAvailableTables();
        loadWaiters();
    }

    // load all the available tables
    private void loadAvailableTables(){
        try {
            List<RestaurantTable> tables = tableService.getAvailableTables();
            tablesTableView.setItems(FXCollections.observableArrayList(tables));
            if (tables.isEmpty()){
                messageLabel.setText("No available tables found.");
            } else {
                messageLabel.setText(tables.size() + " available table(s) found.");
            }
        } catch (SQLException e){
            messageLabel.setText("Unable to load restaurant tables.");
            e.printStackTrace();
        }
    }

    // get all the waiters that the restaurant have
    private void loadWaiters(){
        try {
            List<Employee> waiters = tableService.getWaiters();
            waiterComboBox.setItems(FXCollections.observableArrayList(waiters));
        } catch (SQLException e) {
            messageLabel.setText("Unable to load waiters.");
            e.printStackTrace();
        }
    }

    // handles the tables status/assigned employee update
    @FXML
    private void handleAssignWaiter(){
        RestaurantTable selectedTable = tablesTableView.getSelectionModel().getSelectedItem();
        Employee selectedWaiter = waiterComboBox.getValue();

        if (selectedTable == null){
            messageLabel.setText("Please select a table first.");
            return;
        }
        if (selectedWaiter == null){
            messageLabel.setText("Please select a waiter first.");
            return;
        }

        try {
            boolean assigned = tableService.assignTable(selectedTable.getId(), selectedWaiter.getId());

            if (assigned){
                int tableNumber = selectedTable.getTableNumber();
                String waiterName = selectedWaiter.toString();

                tablesTableView.getSelectionModel().clearSelection();
                waiterComboBox.getSelectionModel().clearSelection();

                loadAvailableTables();
                messageLabel.setText(
                        "Table " + tableNumber + " assigned to " + waiterName + "."
                );
            } else {
                messageLabel.setText("This table is no longer available. Refreshed the available table.");
                loadAvailableTables();
            }
        } catch (SQLException e){
            messageLabel.setText("Unable to assign the table.");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleRefresh(){
        loadAvailableTables();
        loadWaiters();
    }
}
