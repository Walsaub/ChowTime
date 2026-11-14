package com.chowtime.controllers;

import com.chowtime.services.DatabaseService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

public class DatabaseController {

    @FXML
    private Label statusLabel;

    @FXML
    private ListView<String> tableList;

    private final DatabaseService databaseService = new DatabaseService();

    @FXML
    public void initialize(){
        try {
            databaseService.initializeDatabase();
            tableList.getItems().setAll(databaseService.getDatabaseTables());
            statusLabel.setText("Connected successfully! Database is ready.");
        } catch (Exception e) {
            statusLabel.setText("Database connection failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
