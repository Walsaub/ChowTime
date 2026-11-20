package com.chowtime.services;

import com.chowtime.database.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class HelpRequestService {
    private static final String CREATE_REQUEST_QUERY = """
            INSERT INTO HelpRequest
                (tableID, handledByEmployeeID, requestTime, requestStatus)
            VALUES
                (?, NULL, NOW(), 'pending')
            """;

    public void createHelpRequest(int tableID) throws SQLException{
        try (Connection connection = DatabaseConnection.getDatabaseConnection();
             PreparedStatement statement = connection.prepareStatement(CREATE_REQUEST_QUERY)) {
            statement.setInt(1, tableID);
            statement.executeUpdate();
        }
    }
}
