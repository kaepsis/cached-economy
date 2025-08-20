package dev.kaepsis.cachedeconomy.api;

import dev.kaepsis.cachedeconomy.storage.impl.CacheStorage;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class CachedEconomyAPI {

    private static CachedEconomyAPI instance;
    private final CacheStorage storage;

    private CachedEconomyAPI() {
        this.storage = CacheStorage.getInstance();
    }

    public static CachedEconomyAPI getInstance() {
        if (instance == null) {
            instance = new CachedEconomyAPI();
        }
        return instance;
    }

    public double getBalance(String playerName) {
        return storage.getCachedBalance(playerName);
    }

    public double getBalance(Player player) {
        return getBalance(player.getName());
    }

    public CompletableFuture<Void> setBalance(String playerName, double amount) {
        return CompletableFuture.runAsync(() -> storage.setBalance(playerName, amount));
    }

    public CompletableFuture<Void> setBalance(Player player, double amount) {
        return setBalance(player.getName(), amount);
    }

    public CompletableFuture<Void> addBalance(String playerName, double amount) {
        return CompletableFuture.runAsync(() -> {
            double currentBalance = storage.getCachedBalance(playerName);
            storage.setBalance(playerName, currentBalance + amount);
        });
    }

    public CompletableFuture<Void> addBalance(Player player, double amount) {
        return addBalance(player.getName(), amount);
    }

    public CompletableFuture<Void> subtractBalance(String playerName, double amount) {
        return CompletableFuture.runAsync(() -> {
            double currentBalance = storage.getCachedBalance(playerName);
            storage.setBalance(playerName, Math.max(0, currentBalance - amount));
        });
    }

    public CompletableFuture<Void> subtractBalance(Player player, double amount) {
        return subtractBalance(player.getName(), amount);
    }

    public boolean hasBalance(String playerName, double amount) {
        return storage.getCachedBalance(playerName) >= amount;
    }

    public boolean hasBalance(Player player, double amount) {
        return hasBalance(player.getName(), amount);
    }

    public String getFormattedBalance(String playerName) {
        return storage.getBalanceFormatted(playerName);
    }

    public String getFormattedBalance(Player player) {
        return getFormattedBalance(player.getName());
    }

    public void registerPlayer(Player player) {
        storage.registerPlayer(player);
    }

    public List<String> getRegisteredPlayers() {
        return storage.getRegisteredPlayers();
    }

    public boolean isPlayerRegistered(String playerName) {
        return storage.getRegisteredPlayers().contains(playerName);
    }

    public boolean isPlayerRegistered(Player player) {
        return isPlayerRegistered(player.getName());
    }

    public List<Map.Entry<String, Double>> getTopTen() {
        return storage.getTopTen();
    }

    public Map.Entry<String, Double> getTopTenAt(int position) {
        return storage.getTopTenAt(position);
    }

    public int getPlayerRank(String playerName) {
        List<Map.Entry<String, Double>> topList = storage.getTopTen();
        for (int i = 0; i < topList.size(); i++) {
            if (topList.get(i).getKey().equals(playerName)) {
                return i + 1;
            }
        }
        return -1;
    }

    public int getPlayerRank(Player player) {
        return getPlayerRank(player.getName());
    }

    public CompletableFuture<Boolean> transferBalance(String fromPlayer, String toPlayer, double amount) {
        return CompletableFuture.supplyAsync(() -> {
            if (!hasBalance(fromPlayer, amount)) {
                return false;
            }

            double fromBalance = storage.getCachedBalance(fromPlayer);
            double toBalance = storage.getCachedBalance(toPlayer);

            storage.setBalance(fromPlayer, fromBalance - amount);
            storage.setBalance(toPlayer, toBalance + amount);

            return true;
        });
    }

    public CompletableFuture<Boolean> transferBalance(Player fromPlayer, Player toPlayer, double amount) {
        return transferBalance(fromPlayer.getName(), toPlayer.getName(), amount);
    }
}