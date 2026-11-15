package com.chowtime.controllers;

import com.chowtime.models.RestaurantTable;
import com.chowtime.services.TableService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
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
    private Label messageLabel;

    private final TableService tableService = new TableService();

    @FXML
    public void initialize(){
        //Connect the table columns to the model getters
        tableNumberColumn.setCellValueFactory(new PropertyValueFactory<>("tableNumber"));
        capacityColumn.setCellValueFactory(new PropertyValueFactory<>("capacity"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        loadAvailableTables();
    }

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

    @FXML
    private void handleRefresh(){
        loadAvailableTables();
    }
}
