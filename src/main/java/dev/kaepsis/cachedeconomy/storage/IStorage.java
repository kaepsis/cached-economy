package dev.kaepsis.cachedeconomy.storage;

import org.bukkit.entity.Player;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface IStorage {

    CompletableFuture<Double> getCachedBalance(String playerName);

    double getBalance(String playerName);

    void setBalance(String playerName, double amount);

    void registerPlayer(Player player);

    List<String> getRegisteredPlayers();

}
