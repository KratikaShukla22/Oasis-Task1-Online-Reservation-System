package com.oasis.reservation;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {

    private static final String URL = "jdbc:sqlite:reservation.db";

    public static Connection connect() {
        Connection connection = null;

        try {
            connection = DriverManager.getConnection(URL);
            System.out.println("Database connected successfully.");

            createTables(connection);

        } catch (SQLException e) {
            System.out.println("Database connection failed.");
            e.printStackTrace();
        }

        return connection;
    }

    private static void createTables(Connection connection) throws SQLException {


        Statement statement = connection.createStatement();

        String usersTable = """
                CREATE TABLE IF NOT EXISTS users (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    username TEXT UNIQUE NOT NULL,
                    password TEXT NOT NULL
                )
                """;

        String reservationsTable = """
                CREATE TABLE IF NOT EXISTS reservations (
                    pnr INTEGER PRIMARY KEY AUTOINCREMENT,
                    passenger_name TEXT NOT NULL,
                    train_number INTEGER NOT NULL,
                    train_name TEXT NOT NULL,
                    class_type TEXT NOT NULL,
                    journey_date TEXT NOT NULL,
                    source TEXT NOT NULL,
                    destination TEXT NOT NULL
                )
                """;

        statement.execute(usersTable);
        statement.execute(reservationsTable);


        System.out.println("Tables created successfully.");
    }
    public static void createDefaultUser() {

        String sql = "INSERT OR IGNORE INTO users (username, password) VALUES (?, ?)";

        try (Connection connection = connect();
             java.sql.PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, "admin");
            statement.setString(2, "admin123");

            statement.executeUpdate();

            System.out.println("Default user ready.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}