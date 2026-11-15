package com.chowtime.services;

import com.chowtime.database.DatabaseConnection;
import com.chowtime.models.Employee;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
                    if (passwordHash != null && Objects.equals(password, passwordHash)/*BCrypt.checkpw(password, passwordHash)*/){
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
}
