package dev.kaepsis.cachedeconomy.storage.impl;

import dev.kaepsis.cachedeconomy.Main;
import dev.kaepsis.cachedeconomy.config.GeneralConfig;
import dev.kaepsis.cachedeconomy.storage.BalanceUtils;
import dev.kaepsis.cachedeconomy.storage.IStorage;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class CacheStorage implements IStorage {

    private static final int TOP_PLAYERS_LIMIT = 10;
    private static final long CACHE_DURATION = 1000;
    private static CacheStorage instance;
    private final PriorityQueue<Map.Entry<String, Double>> topPlayers;
    private List<String> cachedPlayersList;
    private long lastPlayersListUpdate;

    private CacheStorage() {
        topPlayers = new PriorityQueue<>((e1, e2) -> Double.compare(e2.getValue(), e1.getValue()));
    }

    public static CacheStorage getInstance() {
        if (instance == null) {
            instance = new CacheStorage();
        }
        return instance;
    }

    @Override
    public double getCachedBalance(String playerName) {
        return Main.savedPlayers.getOrDefault(playerName, GeneralConfig.getInstance().startingBalance);
    }

    @Override
    public double getBalance(String playerName) {
        return 0D;
    }

    @Override
    public void setBalance(String playerName, double amount) {
        CompletableFuture.runAsync(() -> {
            Main.savedPlayers.put(playerName, amount);
            updateTopPlayers(playerName, amount);
        });
    }

    @Override
    public String getBalanceFormatted(String playerName) {
        double balance = getCachedBalance(playerName);
        return BalanceUtils.getInstance().formatBalance(balance);
    }

    @Override
    public void registerPlayer(Player player) {
        double balance = PlayerStorage.getInstance().getBalance(player.getName());
        Main.savedPlayers.put(player.getName(), balance);
    }

    @Override
    public List<String> getRegisteredPlayers() {
        long currentTime = System.currentTimeMillis();
        if (cachedPlayersList == null || currentTime - lastPlayersListUpdate > CACHE_DURATION) {
            cachedPlayersList = new ArrayList<>(Main.savedPlayers.keySet());
            lastPlayersListUpdate = currentTime;
        }
        return cachedPlayersList;
    }


    public List<Map.Entry<String, Double>> getTopTen() {
        ArrayList<Map.Entry<String, Double>> sortedList = new ArrayList<>(topPlayers);
        sortedList.sort((e1, e2) -> Double.compare(e2.getValue(), e1.getValue()));
        return sortedList;
    }

    public Map.Entry<String, Double> getTopTenAt(int position) {
        if (position < 0 || position >= TOP_PLAYERS_LIMIT) return null;
        ArrayList<Map.Entry<String, Double>> sortedList = new ArrayList<>(topPlayers);
        sortedList.sort((e1, e2) -> Double.compare(e2.getValue(), e1.getValue()));
        return sortedList.get(position);
    }


    private synchronized void updateTopPlayers(String playerName, double balance) {
        topPlayers.removeIf(entry -> entry.getKey().equalsIgnoreCase(playerName));
        topPlayers.offer(new AbstractMap.SimpleEntry<>(playerName, balance));
        while (topPlayers.size() > TOP_PLAYERS_LIMIT) {
            topPlayers.poll();
        }
    }

    public void loadAllPlayers() {
        List<String> players = PlayerStorage.getInstance().getRegisteredPlayers();
        for (String playerName : players) {
            double balance = PlayerStorage.getInstance().getBalance(playerName);
            Main.savedPlayers.put(playerName, balance);
            updateTopPlayers(playerName, balance);
        }
    }
}