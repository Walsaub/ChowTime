package com.chowtime.services;

import com.chowtime.database.DatabaseConnection;
import com.chowtime.models.RestaurantTable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TableService {
    private static final String AVAILABLE_TABLES_QUERY = """
            SELECT id, tableNumber, capacity, status
            FROM RestaurantTable
            WHERE status = ?
            ORDER BY tableNumber
            """;

    public List<RestaurantTable> getAvailableTables() throws SQLException{
        List<RestaurantTable> tables = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getDatabaseConnection();
             PreparedStatement statement = connection.prepareStatement(AVAILABLE_TABLES_QUERY)) {
            statement.setString(1, "available");

            try (ResultSet result = statement.executeQuery()) {
                while (result.next()){
                    RestaurantTable table = new RestaurantTable(
                            result.getInt("id"),
                            result.getInt("tableNumber"),
                            result.getInt("capacity"),
                            result.getString("status")
                    );
                    tables.add(table);
                }
            }
        }
        return tables;
    }
}
