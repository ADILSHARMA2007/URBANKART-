package com.urbankart.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/urbancart";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "Patti=Palampur@123"; // Change to your MySQL password

    private static Connection connection = null;

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("MySQL JDBC Driver not found", e);
        }
    }

    public static Connection getConnection() {
        try {
            // Check if connection is null, closed, or invalid
            if (connection == null || connection.isClosed() || !connection.isValid(2)) {
                reconnect();
            }
        } catch (SQLException e) {
            System.err.println("Connection validation failed: " + e.getMessage());
            reconnect();
        }
        return connection;
    }

    private static void reconnect() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            System.out.println("Database connection established successfully.");
        } catch (SQLException e) {
            throw new RuntimeException("Failed to connect to database: " + e.getMessage(), e);
        }
    }

    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                connection = null;
                System.out.println("Database connection closed.");
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
    }

    // Test connection method
    public static boolean testConnection() {
        try (Connection testConn = DriverManager.getConnection(URL, USERNAME, PASSWORD)) {
            return testConn.isValid(2);
        } catch (SQLException e) {
            System.err.println("Connection test failed: " + e.getMessage());
            return false;
        }
    }
}