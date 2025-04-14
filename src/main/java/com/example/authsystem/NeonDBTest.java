package com.example.authsystem;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class NeonDBTest {
    public static void main(String[] args) {
        String url = "jdbc:postgresql://ep-dawn-fire-a56g8r0t-pooler.us-east-2.aws.neon.tech/neondb?sslmode=require";
        String user = "neondb_owner";
        String password = "npg_86cHsgzaEltm";

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            System.out.println("✅ Connection successful!");
        } catch (SQLException e) {
            System.err.println("❌ Connection failed: " + e.getMessage());
        }
    }
}

