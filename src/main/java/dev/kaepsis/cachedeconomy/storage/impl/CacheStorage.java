package dev.kaepsis.cachedeconomy.storage.impl;

import dev.kaepsis.cachedeconomy.Main;
import dev.kaepsis.cachedeconomy.config.GeneralConfig;
import dev.kaepsis.cachedeconomy.storage.IStorage;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class CacheStorage implements IStorage {

    private static CacheStorage instance;

    private CacheStorage() {
    }

    public static CacheStorage getInstance() {
        if (instance == null) {
            instance = new CacheStorage();
        }
        return instance;
    }

    @Override
    public CompletableFuture<Double> getCachedBalance(String playerName) {
        return CompletableFuture.supplyAsync(() -> Main.savedPlayers.getOrDefault(playerName, GeneralConfig.getInstance().startingBalance));
    }

    @Override
    public double getBalance(String playerName) {
        return 0D;
    }

    @Override
    public void setBalance(String playerName, double amount) {
        CompletableFuture.runAsync(() -> Main.savedPlayers.put(playerName, amount));
    }

    @Override
    public void registerPlayer(Player player) {
        double balance = PlayerStorage.getInstance().getBalance(player.getName());
        Main.savedPlayers.put(player.getName(), balance);
    }

    @Override
    public List<String> getRegisteredPlayers() {
        return Main.savedPlayers.keySet().stream().toList();
    }
}
