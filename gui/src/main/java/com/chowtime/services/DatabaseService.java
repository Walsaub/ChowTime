package com.chowtime.services;

import com.chowtime.database.DatabaseConnection;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseService {
    public void initializeDatabase() throws Exception {
        //create the database
        try(Connection connection =
                    DatabaseConnection.getServerConnection();
            Statement statement = connection.createStatement()) {
            statement.executeUpdate(
                    "CREATE DATABASE IF NOT EXISTS chow_time"
            );
        }

        //connect to the database and execute the schema
        try(Connection connection =
                    DatabaseConnection.getDatabaseConnection();
            Statement statement = connection.createStatement()) {
            InputStream input = getClass().getResourceAsStream("/database/schema.sql");
            if (input == null){
                throw new Exception("Could not find schema.sql");
            }

            String sql;
            try(input) {
                sql = new String(input.readAllBytes(), StandardCharsets.UTF_8);
            }

            //execute the SQL statements
            for (String query : sql.split(";")){
                query = query.trim();
                if (!query.isEmpty()){
                    statement.execute(query);
                }
            }
        }
    }

    public List<String> getDatabaseTables() throws SQLException {
        List<String> tables = new ArrayList<>();

        try(Connection connection = DatabaseConnection.getDatabaseConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();

            try(ResultSet result = metaData.getTables(
                    connection.getCatalog(),
                    null,
                    "%",
                    new String[]{"TABLE"})) {
                while (result.next()){
                    tables.add(result.getString("TABLE_NAME"));
                }
            }
        }
        return tables;
    }

}
