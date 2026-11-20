package com.chowtime.services;

import com.chowtime.database.DatabaseConnection;
import com.chowtime.models.KitchenOrderItem;
import com.chowtime.models.WaiterHelpRequest;
import com.chowtime.models.WaiterOrder;
import com.chowtime.models.WaiterTable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class WaiterService {
    private static final String TABLES_QUERY = """
            SELECT id, tableNumber, capacity, status
            FROM RestaurantTable
            WHERE waiterID = ?
            ORDER BY tableNumber
            """;

    private static final String ORDERS_QUERY = """
            SELECT co.id AS orderID,
                   rt.tableNumber,
                   co.orderDateTime,
                   co.orderStatus
            FROM CustomerOrder co
            JOIN RestaurantTable rt ON co.tableID = rt.id
            WHERE rt.waiterID = ?
              AND co.orderStatus NOT IN ('completed', 'cancelled')
            ORDER BY co.orderDateTime DESC
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
            JOIN RestaurantTable rt ON co.tableID = rt.id
            WHERE rt.waiterID = ?
              AND co.orderStatus NOT IN ('completed', 'cancelled')
            GROUP BY oi.id, oi.orderID, mi.itemName, oi.quantity
            ORDER BY oi.orderID, mi.itemName
            """;

    private static final String HELP_REQUESTS_QUERY = """
            SELECT hr.id,
                   rt.tableNumber,
                   hr.requestTime,
                   hr.requestStatus
            FROM HelpRequest hr
            JOIN RestaurantTable rt ON hr.tableID = rt.id
            WHERE rt.waiterID = ?
              AND hr.requestStatus <> 'completed'
            ORDER BY hr.requestTime ASC
            """;

    private static final String UPDATE_HELP_REQUEST_QUERY = """
            UPDATE HelpRequest
            SET requestStatus = ?,
                handledByEmployeeID = ?
            WHERE id = ?
            """;

    public List<WaiterTable> getAssignedTables(int waiterID) throws SQLException{
        List<WaiterTable> tables = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getDatabaseConnection();
             PreparedStatement statement = connection.prepareStatement(TABLES_QUERY)) {
            statement.setInt(1, waiterID);

            try (ResultSet result = statement.executeQuery()) {
                while (result.next()){
                    tables.add(new WaiterTable(
                            result.getInt("id"),
                            result.getInt("tableNumber"),
                            result.getInt("capacity"),
                            result.getString("status")
                    ));
                }
            }
        }
        return tables;
    }


    public List<WaiterOrder> getAssignedOrders(int waiterID) throws SQLException{
        Map<Integer, WaiterOrder> orders = new LinkedHashMap<>();
        try (Connection connection = DatabaseConnection.getDatabaseConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(ORDERS_QUERY)) {
                statement.setInt(1, waiterID);
                try (ResultSet result = statement.executeQuery()) {
                    while (result.next()){
                        int orderID = result.getInt("orderID");
                        orders.put(orderID, new WaiterOrder(
                                orderID,
                                result.getInt("tableNumber"),
                                result.getString("orderDateTime"),
                                result.getString("orderStatus")
                        ));
                    }
                }
            }
            try (PreparedStatement statement = connection.prepareStatement(ORDER_ITEMS_QUERY)) {
                statement.setInt(1, waiterID);
                try (ResultSet result = statement.executeQuery()) {
                    while (result.next()){
                        WaiterOrder order = orders.get(result.getInt("orderID"));

                        if (order != null){
                            order.addItem(new KitchenOrderItem(
                                    result.getString("itemName"),
                                    result.getInt("quantity"),
                                    result.getString("selectedOptions")
                            ));
                        }
                    }
                }
            }
        }
        return new ArrayList<>(orders.values());
    }

    public List<WaiterHelpRequest> getHelpRequests(int waiterID) throws SQLException{
        List<WaiterHelpRequest> requests = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getDatabaseConnection();
             PreparedStatement statement = connection.prepareStatement(HELP_REQUESTS_QUERY)) {

            statement.setInt(1, waiterID);
            try (ResultSet result = statement.executeQuery()) {
                while (result.next()){
                    requests.add(new WaiterHelpRequest(
                            result.getInt("id"),
                            result.getInt("tableNumber"),
                            result.getString("requestTime"),
                            result.getString("requestStatus")
                    ));
                }
            }
        }
        return requests;
    }

    public void updateHelpRequestStatus(int requestID, int employeeID, String newStatus) throws SQLException{

        if (!newStatus.equals("pending")
                && !newStatus.equals("in_progress")
                && !newStatus.equals("completed")) {
            throw new IllegalArgumentException("Invalid help request status.");
        }

        String currentStatusQuery = """
            SELECT requestStatus
            FROM HelpRequest
            WHERE id = ?
            FOR UPDATE
            """;

        try (Connection connection = DatabaseConnection.getDatabaseConnection()) {

            connection.setAutoCommit(false);

            try {
                String currentStatus;

                try (PreparedStatement statement = connection.prepareStatement(currentStatusQuery)) {
                    statement.setInt(1, requestID);
                    try (ResultSet result = statement.executeQuery()) {
                        if (!result.next()){
                            throw new SQLException("Help request not found.");
                        }
                        currentStatus = result.getString("requestStatus");
                    }
                }

                boolean validTransition = switch (currentStatus.toLowerCase()) {
                    case "pending" -> newStatus.equals("in_progress");
                    case "in_progress" -> newStatus.equals("completed");
                    default -> false;
                };

                if (!validTransition) {
                    throw new SQLException(
                            "Invalid status change from " + currentStatus + " to " + newStatus
                    );
                }

                try (PreparedStatement statement = connection.prepareStatement(UPDATE_HELP_REQUEST_QUERY)){
                    statement.setString(1, newStatus);
                    statement.setInt(2, employeeID);
                    statement.setInt(3, requestID);

                    statement.executeUpdate();
                }
                connection.commit();
            } catch (SQLException | RuntimeException e){
                connection.rollback();
                throw e;
            } finally {
                connection.setAutoCommit(true);
            }
        }
    }
}
