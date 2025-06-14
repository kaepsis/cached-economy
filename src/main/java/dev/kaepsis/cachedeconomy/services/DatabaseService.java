package dev.kaepsis.cachedeconomy.services;

import dev.kaepsis.cachedeconomy.Main;
import dev.kaepsis.cachedeconomy.config.GeneralConfig;
import lombok.Setter;

import java.sql.*;

public class DatabaseService {

    static final String USER = GeneralConfig.getInstance().username;
    static final String PASSWORD = GeneralConfig.getInstance().password;
    static final String DATABASE = GeneralConfig.getInstance().database;
    static final String HOST = GeneralConfig.getInstance().host;
    static final int PORT = GeneralConfig.getInstance().port;
    static final boolean AUTO_RECONNECT = GeneralConfig.getInstance().autoReconnect;
    static final String URL = String.format("jdbc:mysql://%s:%d/%s?characterEncoding=latin1&%s", HOST, PORT, DATABASE, AUTO_RECONNECT);
    private static DatabaseService instance = null;
    @Setter
    public Connection connection;

    private DatabaseService() {
    }

    public static DatabaseService getInstance() {
        if (instance == null) {
            instance = new DatabaseService();
        }
        return instance;
    }

    public synchronized void openConnection() {
        try {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            setConnection(connection);
            createDefaultTable();
            Main.logger.info("Database connection established");
        } catch (SQLException e) {
            Main.logger.severe("Database connection failed: " + e.getMessage());
        }
    }

    void createDefaultTable() {
        String query = """
                CREATE TABLE IF NOT EXISTS players (
                    id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
                    name VARCHAR(17),
                    uuid VARCHAR(36),
                    balance DOUBLE(10, 2)
                );
                """;
        try {
            connection.prepareStatement(query).execute();
        } catch (SQLException e) {
            Main.logger.severe("Cannot create default table: " + e.getMessage());
        }
    }

    public void cachePlayers() {
        String query = "SELECT name, balance FROM players";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String name = resultSet.getString("name");
                double balance = resultSet.getDouble("balance");
                Main.savedPlayers.put(name, balance);
            }
        } catch (SQLException e) {
            Main.logger.severe("Error while loading players: " + e.getMessage());
        }
    }

    public void removeCachedPlayers() {
        Main.savedPlayers.forEach((name, balance) -> {
            String query = "UPDATE players SET balance = ? WHERE name = ?";
            try {
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setDouble(1, balance);
                preparedStatement.setString(2, name);
                preparedStatement.execute();
            } catch (SQLException e) {
                Main.logger.severe("Error while removing cached players: " + e.getMessage());
            }
        });
    }

}
