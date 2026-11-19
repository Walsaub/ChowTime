package com.chowtime.services;

import com.chowtime.database.DatabaseConnection;
import com.chowtime.models.Employee;
import com.chowtime.models.ManagerEmployee;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class EmployeeService {
    private static final String LOGIN_QUERY = """
            SELECT e.id, e.firstName, e.lastname, e.username, e.passwordHash, r.roleName
            FROM Employee e
            JOIN Role r ON e.roleID = r.id
            WHERE e.username = ?
            """;

    public Employee login(String username, String password) throws SQLException{
        try (Connection connection = DatabaseConnection.getDatabaseConnection();
             PreparedStatement statement = connection.prepareStatement(LOGIN_QUERY)) {
            statement.setString(1, username);
            try (ResultSet result = statement.executeQuery()) {
                if (result.next()){
                    String passwordHash = result.getString("passwordHash");
                    //TODO: replace the string check with a hash check
                    if (passwordHash != null && BCrypt.checkpw(password, passwordHash)){
                        return new Employee(
                                result.getInt("id"),
                                result.getString("firstName"),
                                result.getString("lastName"),
                                result.getString("username"),
                                result.getString("roleName")
                        );
                    }
                }
            }
        }
        return null;
    }

    public void addEmployee(int roleID, String firstName, String lastName, String username, String password) throws SQLException {
        String query = """
            INSERT INTO Employee
                (roleID, firstName, lastname, username, passwordHash)
            VALUES (?, ?, ?, ?, ?)
            """;
        String passwordHash = BCrypt.hashpw(password, BCrypt.gensalt());
        try (Connection connection = DatabaseConnection.getDatabaseConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, roleID);
            statement.setString(2, firstName);
            statement.setString(3, lastName);
            statement.setString(4, username);
            statement.setString(5, passwordHash);
            statement.executeUpdate();
        }
    }

    public void updateEmployee(int employeeID, int roleID, String firstName, String lastName, String username) throws SQLException {
        String query = """
            UPDATE Employee
            SET roleID = ?,
                firstName = ?,
                lastname = ?,
                username = ?
            WHERE id = ?
            """;

        try (Connection connection = DatabaseConnection.getDatabaseConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, roleID);
            statement.setString(2, firstName);
            statement.setString(3, lastName);
            statement.setString(4, username);
            statement.setInt(5, employeeID);

            statement.executeUpdate();
        }
    }

    public void updateEmployeePassword(int employeeID, String password)
            throws SQLException {
        String query = """
            UPDATE Employee
            SET passwordHash = ?
            WHERE id = ?
            """;

        String passwordHash = BCrypt.hashpw(password, BCrypt.gensalt());

        try (Connection connection = DatabaseConnection.getDatabaseConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, passwordHash);
            statement.setInt(2, employeeID);

            statement.executeUpdate();
        }
    }

    public void deleteEmployee(int employeeID) throws SQLException {

        String query = """
            DELETE FROM Employee
            WHERE id = ?
            """;

        try (Connection connection = DatabaseConnection.getDatabaseConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, employeeID);

            statement.executeUpdate();
        }
    }

    public boolean usernameExists(String username) throws SQLException {

        String query = """
            SELECT id
            FROM Employee
            WHERE username = ?
            """;

        try (Connection connection = DatabaseConnection.getDatabaseConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, username);

            try (ResultSet result = statement.executeQuery()) {
                return result.next();
            }
        }
    }

    public List<ManagerEmployee> getAllEmployees() throws SQLException {

        List<ManagerEmployee> employees = new ArrayList<>();

        String query = """
            SELECT e.id, e.firstName, e.lastname,
                   e.username, r.roleName, r.id AS roleID
            FROM Employee e
            JOIN Role r ON e.roleID = r.id
            ORDER BY e.firstName, e.lastname
            """;

        try (Connection connection = DatabaseConnection.getDatabaseConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet result = statement.executeQuery()) {

            while (result.next()) {

                employees.add(new ManagerEmployee(
                        result.getInt("id"),
                        result.getString("firstName"),
                        result.getString("lastname"),
                        result.getString("username"),
                        result.getString("roleName"),
                        result.getInt("roleID")
                ));
            }
        }

        return employees;
    }

    public List<String> getRoleNames() throws SQLException {

        List<String> roles = new ArrayList<>();

        String query = """
            SELECT roleName
            FROM Role
            ORDER BY roleName
            """;

        try (Connection connection = DatabaseConnection.getDatabaseConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet result = statement.executeQuery()) {

            while (result.next()) {
                roles.add(result.getString("roleName"));
            }
        }

        return roles;
    }

    public int getRoleID(String roleName) throws SQLException {

        String query = """
            SELECT id
            FROM Role
            WHERE roleName = ?
            """;

        try (Connection connection = DatabaseConnection.getDatabaseConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, roleName);

            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    return result.getInt("id");
                }
            }
        }

        throw new SQLException("Role not found.");
    }
}
