package com.chowtime.services;

import com.chowtime.database.DatabaseConnection;
import com.chowtime.models.Employee;
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

    // get all the available restaurant tables
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

    // get all the waiters in the restaurant
    public List<Employee> getWaiters() throws SQLException{
        List<Employee> waiters = new ArrayList<>();

        String query = """
                SELECT e.id, e.firstName, e.lastname, e.username, r.roleName
                FROM Employee e
                JOIN Role r ON e.roleID = r.id
                WHERE r.roleName = ?
                ORDER BY e.firstName
                """;

        try (Connection connection = DatabaseConnection.getDatabaseConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, "Waiter");

            try (ResultSet result = statement.executeQuery()) {
                while (result.next()){
                    Employee employee = new Employee(
                            result.getInt("id"),
                            result.getString("firstName"),
                            result.getString("lastname"),
                            result.getString("username"),
                            result.getString("roleName")
                    );
                    waiters.add(employee);
                }
            }
        }

        return waiters;
    }

    // update the restaurant table status and that table assigned employee
    public boolean assignTable(int tableID, int waiterID) throws SQLException{
        String query = """
                UPDATE RestaurantTable
                SET status = ?, waiterID = ?
                WHERE id = ? AND status = ?
                """;

        try (Connection connection = DatabaseConnection.getDatabaseConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, "occupied");
            statement.setInt(2, waiterID);
            statement.setInt(3, tableID);
            statement.setString(4, "available");

            return statement.executeUpdate() == 1;
        }
    }

}
