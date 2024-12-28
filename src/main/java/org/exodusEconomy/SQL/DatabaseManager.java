package org.exodusEconomy.SQL;

import java.io.*;
import java.util.logging.*;
import java.sql.*;


public class DatabaseManager {

    private Connection connection;
    private File datafolder;

    public DatabaseManager(File datafolder, Logger logger) {
        try {
            this.datafolder = datafolder;
            File databaseFile = new File(datafolder, "economy.db");
            if (!databaseFile.exists()) {
                logger.info("Database doesn't exist. Creating database...");
                if (!datafolder.exists()) {
                    datafolder.mkdirs();
                }
                databaseFile.createNewFile();
                logger.info("Database created !");
            }
            else logger.info("Database exists ! Connecting...");

            String url = "jdbc:sqlite:" + databaseFile.getAbsolutePath();
            this.connection = DriverManager.getConnection(url);

            ResultSet tables = connection.getMetaData().getTables(null, null, "player_economy", null);
            if(tables.next()) logger.info("Table player_economy exists !");
            else {
                logger.info("Table player_economy doesn't exist. Creating...");
                String createTableQuery = "CREATE TABLE player_economy (" +
                        "uuid TEXT PRIMARY KEY," +
                        "balance DOUBLE DEFAULT 0.0," +
                        "public BOOLEAN DEFAULT true" +
                        ");";
                try (PreparedStatement statement = connection.prepareStatement(createTableQuery)) {
                    statement.execute();
                }
                logger.info("Table player_economy has been created !");
            }
            logger.info("Database connection successful.");
        } catch (Exception e) {
            logger.severe("Error initializing/connecting to the database.");
        }
    }

    public void closeDatabase(Logger logger) {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                logger.info("Database connection closed.");
            }
        } catch (SQLException e) {
            logger.info("Error closing the database connection.");
        }
    }

    // GETTERS
    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                String url = "jdbc:sqlite:" + new File(datafolder + "/economy.db");
                connection = DriverManager.getConnection(url);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to reopen the database connection.", e);
        }
        return connection;
    }

}
