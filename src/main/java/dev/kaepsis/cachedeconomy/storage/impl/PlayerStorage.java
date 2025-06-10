package dev.kaepsis.cachedeconomy.storage.impl;

import dev.kaepsis.cachedeconomy.Main;
import dev.kaepsis.cachedeconomy.config.GeneralConfig;
import dev.kaepsis.cachedeconomy.services.DatabaseService;
import dev.kaepsis.cachedeconomy.storage.IStorage;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class PlayerStorage implements IStorage {

    private static PlayerStorage instance = null;

    private PlayerStorage() {
    }

    public static PlayerStorage getInstance() {
        if (instance == null) {
            instance = new PlayerStorage();
        }
        return instance;
    }

    @Override
    public CompletableFuture<Double> getCachedBalance(String playerName) {
        return CompletableFuture.supplyAsync(() -> 0D);
    }

    @Override
    public double getBalance(String playerName) {
        String query = "SELECT balance FROM players WHERE name = ?";
        try {
            PreparedStatement preparedStatement = DatabaseService.getInstance().connection.prepareStatement(query);
            preparedStatement.setString(1, playerName);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (!resultSet.next()) {
                return 0D;
            }
            return resultSet.getDouble("balance");
        } catch (SQLException e) {
            Main.logger.severe("Error while getting player's balance: " + e.getMessage());
            return 0D;
        }
    }

    @Override
    public void setBalance(String playerName, double amount) {
        CompletableFuture.runAsync(() -> {
            String query = "UPDATE players SET balance = ? WHERE name = ?";
            try {
                PreparedStatement preparedStatement = DatabaseService.getInstance().connection.prepareStatement(query);
                preparedStatement.setDouble(1, amount);
                preparedStatement.setString(2, playerName);
                preparedStatement.execute();
            } catch (SQLException e) {
                Main.logger.severe("Error while updating player: " + e.getMessage());
            }
        });
    }

    @Override
    public void registerPlayer(Player player) {
        CompletableFuture.runAsync(() -> {
            String query = "INSERT INTO players (name, uuid, balance) VALUES (?, ?, ?)";
            try {
                PreparedStatement preparedStatement = DatabaseService.getInstance().connection.prepareStatement(query);
                preparedStatement.setString(1, player.getName());
                preparedStatement.setString(2, player.getUniqueId().toString());
                preparedStatement.setDouble(3, GeneralConfig.getInstance().startingBalance);
                preparedStatement.execute();
            } catch (SQLException e) {
                Main.logger.severe("Error while registering player: " + e.getMessage());
            }
        });
    }

    @Override
    public List<String> getRegisteredPlayers() {
        String query = "SELECT name FROM players";
        try {
            PreparedStatement preparedStatement = DatabaseService.getInstance().connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();
            List<String> names = new ArrayList<>();
            while (resultSet.next()) {
                names.add(resultSet.getString("name"));
            }
            return names;
        } catch (SQLException e) {
            Main.logger.severe("Error while getting registered players: " + e.getMessage());
        }
        return List.of();
    }

}
