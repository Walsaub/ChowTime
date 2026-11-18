package com.chowtime.services;

import com.chowtime.database.DatabaseConnection;
import com.chowtime.models.MenuOption;
import com.chowtime.models.OrderCartItem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class OrderService {

    public int createOrder(int tableID, int waiterID, List<OrderCartItem> items) throws SQLException {
        if (items == null || items.isEmpty()){
            throw new IllegalArgumentException("The order must contain at least one item.");
        }

        String orderQuery = """
                INSERT INTO CustomerOrder (tableID, enteredByEmployeeID, orderStatus)
                VALUES (?, ?, 'received')
                """;

        String itemQuery = """
                INSERT INTO OrderItem (orderID, menuItemID, quantity)
                Values (?, ?, ?)
                """;

        String optionQuery = """
                INSERT INTO OrderItemOption (orderItemID, optionID, quantity)
                VALUES (?, ?, ?)
                """;

        try (Connection connection = DatabaseConnection.getDatabaseConnection()) {
            connection.setAutoCommit(false);
            try {
                int orderID;

                // Create the main order
                try (PreparedStatement orderStatement = connection.prepareStatement(orderQuery, PreparedStatement.RETURN_GENERATED_KEYS)) {
                    orderStatement.setInt(1, tableID);
                    orderStatement.setInt(2, waiterID);

                    orderStatement.executeUpdate();

                    try (ResultSet keys = orderStatement.getGeneratedKeys()) {
                        if (!keys.next()){
                            throw new SQLException("Failed to create the order.");
                        }
                        orderID = keys.getInt(1);
                    }
                }

                // Add the food items and its selected options
                try (PreparedStatement itemStatement = connection.prepareStatement(
                        itemQuery,
                        PreparedStatement.RETURN_GENERATED_KEYS);
                     PreparedStatement optionStatement = connection.prepareStatement(optionQuery)) {

                    for (OrderCartItem item : items){
                        itemStatement.setInt(1, orderID);
                        itemStatement.setInt(2, item.getMenuItem().getId());
                        itemStatement.setInt(3, item.getQuantity());
                        itemStatement.executeUpdate();

                        int orderItemID;

                        try (ResultSet keys = itemStatement.getGeneratedKeys()) {
                            if (!keys.next()){
                                throw new SQLException("Failed to create order item.");
                            }
                            orderItemID = keys.getInt(1);
                        }

                        // Save selected customization option
                        for (MenuOption option : item.getSelectedOptions()){
                            optionStatement.setInt(1, orderItemID);
                            optionStatement.setInt(2, option.getId());
                            optionStatement.setInt(3, 1);

                            optionStatement.executeUpdate();
                        }
                    }
                }

                connection.commit();

                return orderID;
            } catch (SQLException | RuntimeException e) {
                connection.rollback();
                throw e;
            } finally {
                connection.setAutoCommit(true);
            }
        }
    }
}
