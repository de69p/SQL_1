package org.example;

import java.sql.*;

public class Main {
    public static void main(String[] args) {
        String url = "jdbc:postgresql://localhost:5432/postgres";
        String user = "postgres";
        String password = "postgres";

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            String createUserTableSql = """
                    CREATE TABLE "user" (
                        user_id SERIAL PRIMARY KEY,
                        name VARCHAR(255) NOT NULL,
                        surname VARCHAR(255) NOT NULL,
                        date_of_registration DATE NOT NULL
                    )
                    """;
            try (PreparedStatement createUserTableStmt = conn.prepareStatement(createUserTableSql)) {
                createUserTableStmt.executeUpdate();
            }

            String createWalletTableSql = """
                    CREATE TABLE wallet (
                        wallet_id SERIAL PRIMARY KEY,
                        currency VARCHAR(255) NOT NULL,
                        amount DECIMAL(10,2) NOT NULL,
                        user_id INTEGER NOT NULL,
                        created DATE NOT NULL,
                        FOREIGN KEY (user_id) REFERENCES "user"(user_id)
                    )
                    """;
            try (PreparedStatement createWalletTableStmt = conn.prepareStatement(createWalletTableSql)) {
                createWalletTableStmt.executeUpdate();
            }

            String insertUserSql = """
                    INSERT INTO "user" (name, surname, date_of_registration)
                    VALUES (?, ?, ?)
                    """;
            try (PreparedStatement insertUserStmt = conn.prepareStatement(insertUserSql)) {
                insertUserStmt.setString(1, "Arsen");
                insertUserStmt.setString(2, "Depa");
                insertUserStmt.setDate(3, java.sql.Date.valueOf("2023-03-01"));
                insertUserStmt.executeUpdate();
            }

            String insertWalletSql = """
                    INSERT INTO wallet (currency, amount, user_id, created)
                    VALUES (?, ?, ?, ?)
                    """;
            try (PreparedStatement insertWalletStmt = conn.prepareStatement(insertWalletSql)) {
                insertWalletStmt.setString(1, "USD");
                insertWalletStmt.setDouble(2, 1000.0);
                insertWalletStmt.setInt(3, 1);
                insertWalletStmt.setDate(4, java.sql.Date.valueOf("2023-03-01"));
                insertWalletStmt.executeUpdate();
            }

            String selectWalletSql = """
                    SELECT "user".name, wallet.currency, wallet.amount
                    FROM "user"
                    JOIN wallet ON "user".user_id = wallet.user_id
                    """;
            try (PreparedStatement selectWalletStmt = conn.prepareStatement(selectWalletSql)) {
                try (ResultSet rs = selectWalletStmt.executeQuery()) {
                    while (rs.next()) {
                        String userName = rs.getString("name");
                        String currency = rs.getString("currency");
                        double amount = rs.getDouble("amount");
                        System.out.println(userName + " has " + amount + " " + currency);
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
