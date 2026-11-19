package com.chowtime.services;

import com.chowtime.database.DatabaseConnection;
import com.chowtime.models.MostOrderedFood;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ManagerService {
    public List<MostOrderedFood> getMostOrderedFood() throws SQLException {

        List<MostOrderedFood> foods = new ArrayList<>();

        String query = """
                SELECT mi.itemName,
                       SUM(oi.quantity) AS totalQuantity
                FROM OrderItem oi
                JOIN MenuItem mi ON oi.menuItemID = mi.id
                JOIN CustomerOrder co ON oi.orderID = co.id
                GROUP BY mi.id, mi.itemName
                ORDER BY totalQuantity DESC, mi.itemName
                """;

        try (Connection connection = DatabaseConnection.getDatabaseConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet result = statement.executeQuery()) {

            while (result.next()) {

                foods.add(new MostOrderedFood(
                        result.getString("itemName"),
                        result.getInt("totalQuantity")
                ));
            }
        }

        return foods;
    }
}
