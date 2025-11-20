package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class MariaDBConnectionTest {

    // IMPORTANT: CUSTOMIZE THESE VALUES for your MariaDB setup
    private static final String DB_URL = "jdbc:mariadb://localhost:3306/adventureworks";
    private static final String USER = "root";      // Replace with your actual username
    private static final String PASS = "Bssnm2003"; // Replace with your actual password
    private static final String DRIVER = "org.mariadb.jdbc.Driver";

    public static void main(String[] args) {
        //Transaction handling
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            // Step 1: Load the MariaDB JDBC Driver
            Class.forName(DRIVER);

            // Step 1: Establish the Connection
            System.out.println("Attempting to connect to MariaDB database...");
            connection = DriverManager.getConnection(DB_URL, USER, PASS);
            System.out.println("Connection successful!");

            // Step 2 & 3: Create Statement and Execute Query
            statement = connection.createStatement();

            // Query to fetch 5 employees (assuming an Employee table exists in AdventureWorks)
            String sql = "SELECT LastName, FirstName FROM person_person LIMIT 5";
            System.out.println("Executing query: " + sql);
            resultSet = statement.executeQuery(sql);

            // Step 4: Iterative processing of the result sets
            System.out.println("--- Query Results ---");
            int count = 0;
            while (resultSet.next()) {
                String lastName = resultSet.getString("LastName");
                String firstName = resultSet.getString("FirstName");
                System.out.println("Employee: " + firstName + " " + lastName);
                count++;
            }
            if (count == 0) {
                System.out.println("No results found. Check table name or query.");
            }

        } catch (Exception e) {
            System.err.println("\n--- CONNECTION FAILED ---");
            // Print the error details to help with troubleshooting
            e.printStackTrace();
        } finally {
            // Step 5: Close resources in the 'finally' block
            try {
                if (resultSet != null) resultSet.close();
                if (statement != null) statement.close();
                if (connection != null) connection.close();
                System.out.println("\nSuccessfully closed database resources.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}