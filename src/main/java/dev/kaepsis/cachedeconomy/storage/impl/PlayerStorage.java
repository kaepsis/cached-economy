package dev.kaepsis.cachedeconomy.storage.impl;

import dev.kaepsis.cachedeconomy.Main;
import dev.kaepsis.cachedeconomy.config.GeneralConfig;
import dev.kaepsis.cachedeconomy.services.DatabaseService;
import dev.kaepsis.cachedeconomy.storage.BalanceUtils;
import dev.kaepsis.cachedeconomy.storage.IStorage;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerStorage implements IStorage {
    private static final long BALANCE_CACHE_DURATION = 300000;
    private static final long PLAYERS_LIST_CACHE_DURATION = 60000;
    private static PlayerStorage instance = null;
    private final Map<String, CacheEntry<Double>> balanceCache;
    private final Map<String, CacheEntry<List<String>>> playersListCache;

    private PlayerStorage() {
        this.balanceCache = new ConcurrentHashMap<>(1000);
        this.playersListCache = new ConcurrentHashMap<>(1);

        new BukkitRunnable() {
            @Override
            public void run() {
                cleanupCaches();
            }
        }.runTaskTimerAsynchronously(Main.instance, 6000L, 6000L);
    }

    public static PlayerStorage getInstance() {
        if (instance == null) {
            synchronized (PlayerStorage.class) {
                if (instance == null) {
                    instance = new PlayerStorage();
                }
            }
        }
        return instance;
    }

    private void cleanupCaches() {
        balanceCache.entrySet().removeIf(entry -> entry.getValue().isExpired());
        playersListCache.entrySet().removeIf(entry -> entry.getValue().isExpired());
    }

    @Override
    public double getCachedBalance(String playerName) {
        CacheEntry<Double> entry = balanceCache.get(playerName);
        if (entry != null && !entry.isExpired()) {
            return entry.value;
        }
        return Main.savedPlayers.getOrDefault(playerName, 0D);
    }

    @Override
    public double getBalance(String playerName) {
        CacheEntry<Double> entry = balanceCache.get(playerName);
        if (entry != null && !entry.isExpired()) {
            return entry.value;
        }

        double balance = loadBalanceFromDb(playerName);
        balanceCache.put(playerName, new CacheEntry<>(balance, BALANCE_CACHE_DURATION));
        return balance;
    }

    private double loadBalanceFromDb(String playerName) {
        String query = "SELECT balance FROM players WHERE name = ?";
        try (Connection conn = DatabaseService.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, playerName);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    double balance = rs.getDouble("balance");
                    Main.savedPlayers.put(playerName, balance);
                    return balance;
                }
            }
        } catch (SQLException e) {
            Main.logger.severe("Error while getting player's balance: " + e.getMessage());
        }
        return 0D;
    }

    @Override
    public String getBalanceFormatted(String playerName) {
        return BalanceUtils.getInstance().formatBalance(getBalance(playerName));
    }

    @Override
    public void setBalance(String playerName, double amount) {
        balanceCache.put(playerName, new CacheEntry<>(amount, BALANCE_CACHE_DURATION));
        Main.savedPlayers.put(playerName, amount);

        CompletableFuture.runAsync(() -> {
            String query = "UPDATE players SET balance = ? WHERE name = ?";
            try (Connection conn = DatabaseService.getInstance().getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {

                stmt.setDouble(1, amount);
                stmt.setString(2, playerName);
                stmt.execute();
            } catch (SQLException e) {
                Main.logger.severe("Error while updating player balance: " + e.getMessage());
            }
        });
    }

    @Override
    public void registerPlayer(Player player) {
        CompletableFuture.runAsync(() -> {
            String query = "INSERT INTO players (name, uuid, balance) VALUES (?, ?, ?) " +
                    "ON DUPLICATE KEY UPDATE uuid = ?, balance = ?";
            try (Connection conn = DatabaseService.getInstance().getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {

                double startingBalance = GeneralConfig.getInstance().startingBalance;
                String uuid = player.getUniqueId().toString();

                stmt.setString(1, player.getName());
                stmt.setString(2, uuid);
                stmt.setDouble(3, startingBalance);
                stmt.setString(4, uuid);
                stmt.setDouble(5, startingBalance);

                stmt.execute();

                balanceCache.put(player.getName(), new CacheEntry<>(startingBalance, BALANCE_CACHE_DURATION));
                Main.savedPlayers.put(player.getName(), startingBalance);
            } catch (SQLException e) {
                Main.logger.severe("Error while registering player: " + e.getMessage());
            }
        });
    }

    @Override
    public List<String> getRegisteredPlayers() {
        CacheEntry<List<String>> entry = playersListCache.get("players");
        if (entry != null && !entry.isExpired()) {
            return new ArrayList<>(entry.value);
        }

        List<String> players = loadPlayersFromDb();
        playersListCache.put("players", new CacheEntry<>(players, PLAYERS_LIST_CACHE_DURATION));
        return new ArrayList<>(players);
    }

    public boolean isPlayerRegistered(String playerName) {
        return getRegisteredPlayers().contains(playerName);
    }

    private List<String> loadPlayersFromDb() {
        String query = "SELECT name FROM players";
        try (Connection conn = DatabaseService.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            List<String> names = new ArrayList<>();
            while (rs.next()) {
                names.add(rs.getString("name"));
            }
            return names;
        } catch (SQLException e) {
            Main.logger.severe("Error while getting registered players: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    private static class CacheEntry<T> {
        final T value;
        final long expirationTime;

        CacheEntry(T value, long duration) {
            this.value = value;
            this.expirationTime = System.currentTimeMillis() + duration;
        }

        boolean isExpired() {
            return System.currentTimeMillis() > expirationTime;
        }
    }
}