package com.chowtime.services;

import com.chowtime.database.DatabaseConnection;
import com.chowtime.models.KitchenOrder;
import com.chowtime.models.KitchenOrderItem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class KitchenService {

    private static final String ACTIVE_ORDERS_QUERY = """
            SELECT co.id AS orderID,
                   rt.tableNumber,
                   CONCAT(e.firstName, ' ', e.lastname) AS waiterName,
                   co.orderDateTime,
                   co.orderStatus
            FROM CustomerOrder co
            JOIN RestaurantTable rt ON co.tableID = rt.id
            LEFT JOIN Employee e ON co.enteredByEmployeeID = e.id
            WHERE co.orderStatus NOT IN ('completed', 'cancelled')
            ORDER BY co.orderDateTime ASC
            """;

    private static final String ORDER_ITEMS_QUERY = """
            SELECT oi.orderID,
                   mi.itemName,
                   oi.quantity,
                   GROUP_CONCAT(
                       DISTINCT mo.optionName
                       ORDER BY mo.optionName
                       SEPARATOR ', '
                   ) AS selectedOptions
            FROM OrderItem oi
            JOIN MenuItem mi ON oi.menuItemID = mi.id
            LEFT JOIN OrderItemOption oio
                ON oi.id = oio.orderItemID
            LEFT JOIN MenuOption mo
                ON oio.optionID = mo.id
            JOIN CustomerOrder co ON oi.orderID = co.id
            WHERE co.orderStatus NOT IN ('completed', 'cancelled')
            GROUP BY oi.id, oi.orderID, mi.itemName, oi.quantity
            ORDER BY oi.orderID, mi.itemName
            """;

    public List<KitchenOrder> getActiveOrders() throws SQLException{
        Map<Integer, KitchenOrder> orders = new LinkedHashMap<>();

        try (Connection connection = DatabaseConnection.getDatabaseConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(ACTIVE_ORDERS_QUERY);
                 ResultSet result = statement.executeQuery()) {

                while (result.next()){
                    int orderID = result.getInt("orderID");
                    KitchenOrder order = new KitchenOrder(
                            orderID,
                            result.getInt("tableNumber"),
                            result.getString("waiterName"),
                            result.getString("orderDateTime"),
                            result.getString("orderStatus")
                    );
                    orders.put(orderID, order);
                }
            }

            try (PreparedStatement statement = connection.prepareStatement(ORDER_ITEMS_QUERY);
                 ResultSet result = statement.executeQuery()) {

                while (result.next()){
                    KitchenOrder order = orders.get(result.getInt("orderID"));

                    if (order != null){
                        KitchenOrderItem item = new KitchenOrderItem(
                                result.getString("itemName"),
                                result.getInt("quantity"),
                                result.getString("selectedOptions")
                        );
                        order.addItem(item);
                    }
                }
            }
        }
        return new ArrayList<>(orders.values());
    }

    public void updateOrderStatus(int orderID, String newStatus) throws SQLException{
        if (!List.of("received", "preparing", "ready", "completed", "cancelled").contains(newStatus)){
            throw new IllegalArgumentException("Invalid order status.");
        }

        String currentStatus;

        try (Connection connection = DatabaseConnection.getDatabaseConnection()) {
            String selectQuery = """
                    SELECT orderStatus
                    FROM CustomerOrder
                    WHERE id = ?
                    """;

            try (PreparedStatement statement = connection.prepareStatement(selectQuery)) {
                statement.setInt(1, orderID);

                try (ResultSet result = statement.executeQuery()) {
                    if (!result.next()){
                        throw new SQLException("Order not found.");
                    }
                    currentStatus = result.getString("orderStatus");
                }
            }
            boolean validTransition = switch (currentStatus) {
                case "received" -> newStatus.equals("preparing") || newStatus.equals("cancelled");
                case "preparing" -> newStatus.equals("ready");
                case "ready" -> newStatus.equals("completed");
                default -> false;
            };

            if (!validTransition){
                throw new SQLException("Invalid status change from " + currentStatus + " to " + newStatus);
            }

            String updateQuery = """
                    UPDATE CustomerOrder
                    SET orderStatus = ?
                    WHERE id = ?
                    """;

            try (PreparedStatement statement = connection.prepareStatement(updateQuery)) {
                statement.setString(1, newStatus);
                statement.setInt(2, orderID);
                statement.executeUpdate();
            }
        }
    }
}
