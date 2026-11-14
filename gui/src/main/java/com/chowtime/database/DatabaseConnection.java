package com.chowtime.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String SERVER_URL =
            "jdbc:mysql://localhost:3306/?serverTimezone=UTC";
    private static final String DATABASE_URL =
            "jdbc:mysql://localhost:3306/chow_time?serverTimezone=UTC";

    private static String username;
    private static String password;

    public static void configure(String user, String pass){
        username = user;
        password = pass;
    }

    //connect to MySQL server
    public static Connection getServerConnection() throws SQLException {
        return DriverManager.getConnection(SERVER_URL, username, password);
    }

    //connect to Chow Time database
    public static Connection getDatabaseConnection() throws SQLException {
        return DriverManager.getConnection(DATABASE_URL, username, password);
    }
}
