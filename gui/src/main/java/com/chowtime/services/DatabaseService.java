package com.chowtime.services;

import com.chowtime.database.DatabaseConnection;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DatabaseService {
    public boolean initializeDatabase() throws Exception {
        //create the database
        try(Connection connection =
                    DatabaseConnection.getServerConnection();
            Statement statement = connection.createStatement()) {
            statement.executeUpdate(
                    "CREATE DATABASE IF NOT EXISTS chow_time"
            );
        }

        //connect to the database and execute the schema
        try (Connection connection = DatabaseConnection.getDatabaseConnection()) {

            // Create the 14 database tables.
            executeSqlFile(connection, "/database/schema.sql");

            // Check if sample data already exists.
            boolean sampleDataInserted = false;

            if (!hasExistingData(connection)) {

                executeSqlFile(connection, "/database/data.sql");

                updateTemporaryPasswords(connection);

                sampleDataInserted = true;
            }

            // Install procedures and triggers.
            executeSqlFile(connection, "/database/procedures.sql");
            executeSqlFile(connection, "/database/triggers.sql");

            return sampleDataInserted;
        }
    }

    private boolean hasExistingData(Connection connection)
            throws SQLException {

        String query = "SELECT COUNT(*) FROM Role";

        try (Statement statement = connection.createStatement();
             ResultSet result = statement.executeQuery(query)) {

            result.next();
            return result.getInt(1) > 0;
        }
    }

    private void updateTemporaryPasswords(Connection connection)
            throws SQLException {

        String query =
                "UPDATE Employee SET passwordHash = ? WHERE username = ?";

        try (PreparedStatement statement =
                     connection.prepareStatement(query)) {

            statement.setString(1,
                    org.mindrot.jbcrypt.BCrypt.hashpw(
                            "1234",
                            org.mindrot.jbcrypt.BCrypt.gensalt()
                    ));
            statement.setString(2, "johnsmith");
            statement.executeUpdate();

            statement.setString(1,
                    org.mindrot.jbcrypt.BCrypt.hashpw(
                            "1234",
                            org.mindrot.jbcrypt.BCrypt.gensalt()
                    ));
            statement.setString(2, "davidbrown");
            statement.executeUpdate();

            statement.setString(1,
                    org.mindrot.jbcrypt.BCrypt.hashpw(
                            "1234",
                            org.mindrot.jbcrypt.BCrypt.gensalt()
                    ));
            statement.setString(2, "sarahwilson");
            statement.executeUpdate();
        }
    }

    private void executeSqlFile(
            Connection connection,
            String filePath
    ) throws Exception {

        InputStream input = getClass().getResourceAsStream(filePath);

        if (input == null) {
            throw new Exception("Could not find " + filePath);
        }

        String sql;

        try (input) {
            sql = new String(input.readAllBytes(), StandardCharsets.UTF_8);
        }

        // Remove USE statements because the connection already selects chow_time.
        sql = sql.replaceAll("(?im)^\\s*USE\\s+chow_time\\s*;\\s*", "");

        // Handle procedure and trigger scripts.
        if (filePath.endsWith("procedures.sql")
                || filePath.endsWith("triggers.sql")) {

            String[] statements = sql.split("(?im)^\\s*DELIMITER\\s+");

            String delimiter = ";";

            for (String part : statements) {

                if (part.trim().isEmpty()) {
                    continue;
                }

                if (part.startsWith("//")) {
                    delimiter = "//";
                    part = part.substring(2);
                } else if (part.startsWith(";")) {
                    delimiter = ";";
                    part = part.substring(1);
                }

                for (String query : part.split(
                        java.util.regex.Pattern.quote(delimiter))) {

                    query = query.trim();

                    if (!query.isEmpty()) {
                        try (Statement statement = connection.createStatement()) {
                            statement.execute(query);
                        }
                    }
                }
            }

        } else {

            // Execute normal SQL files.
            try (Statement statement = connection.createStatement()) {

                for (String query : sql.split(";")) {

                    query = query.trim();

                    if (!query.isEmpty()) {
                        statement.execute(query);
                    }
                }
            }
        }
    }

    public List<String> getDatabaseTables() throws SQLException {

        List<String> tables = new ArrayList<>();

        try (Connection connection =
                     DatabaseConnection.getDatabaseConnection()) {

            DatabaseMetaData metaData = connection.getMetaData();

            try (ResultSet result = metaData.getTables(
                    connection.getCatalog(),
                    null,
                    "%",
                    new String[]{"TABLE"})) {

                while (result.next()) {
                    tables.add(result.getString("TABLE_NAME"));
                }
            }
        }

        return tables;
    }
}