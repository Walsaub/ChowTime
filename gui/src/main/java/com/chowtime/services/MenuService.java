package com.chowtime.services;

import com.chowtime.database.DatabaseConnection;
import com.chowtime.models.MenuItem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MenuService {

    private static final String AVAILABLE_ITEMS_QUERY = """
            SELECT mi.id, mi.itemName, mi.description, mi.price, mi.isCustomizable, mc.categoryName
            FROM MenuItem mi
            LEFT JOIN MenuCategory mc
                ON mi.categoryID = mc.id
                WHERE mi.isAvailable = TRUE
                ORDER BY mc.categoryName, mi.itemName
            """;

    // get all the available menu items
    public List<MenuItem> getAvailableItems() throws SQLException{
        List<MenuItem> items = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getDatabaseConnection();
             PreparedStatement statement = connection.prepareStatement(AVAILABLE_ITEMS_QUERY);
             ResultSet result = statement.executeQuery()){
            while (result.next()){
                MenuItem item = new MenuItem(
                        result.getInt("id"),
                        result.getString("itemName"),
                        result.getString("description"),
                        result.getString("categoryName"),
                        result.getBigDecimal("price"),
                        result.getBoolean("isCustomizable")
                );
                items.add(item);
            }
        }

        return items;
    }
}
