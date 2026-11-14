package com.chowtime.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String SERVER_URL =
            "jdbc:mysql://localhost:3306/?serverTimezone=UTC";
    private static final String DATABASE_URL =
            "jdbc:mysql://localhost:3306/chow_time?serverTimezone=UTC";
    private static final String USER =
            System.getenv().getOrDefault("DB_USER", "root");
    //TODO: Add password to run
    private static final String PASSWORD =
            System.getenv().getOrDefault("DB_PASSWORD", "");

    public static Connection getServerConnection() throws SQLException {
        return DriverManager.getConnection(SERVER_URL, USER, PASSWORD);
    }

    public static Connection getDatabaseConnection() throws SQLException {
        return DriverManager.getConnection(DATABASE_URL, USER, PASSWORD);
    }
}
