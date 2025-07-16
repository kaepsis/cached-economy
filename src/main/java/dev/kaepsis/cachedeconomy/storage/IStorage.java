package dev.kaepsis.cachedeconomy.storage;

import org.bukkit.entity.Player;

import java.util.List;

public interface IStorage {

    double getCachedBalance(String playerName);

    double getBalance(String playerName);

    void setBalance(String playerName, double amount);

    void registerPlayer(Player player);

    List<String> getRegisteredPlayers();

    String getBalanceFormatted(String playerName);

}
