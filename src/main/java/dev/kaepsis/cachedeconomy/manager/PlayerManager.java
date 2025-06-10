package dev.kaepsis.cachedeconomy.manager;

import dev.kaepsis.cachedeconomy.storage.impl.CacheStorage;
import org.bukkit.Bukkit;

public class PlayerManager {

    private static PlayerManager instance = null;

    private PlayerManager() {

    }

    public static PlayerManager getInstance() {
        if (instance == null) {
            instance = new PlayerManager();
        }
        return instance;
    }

    public boolean isOnline(String playerName) {
        return Bukkit.getOnlinePlayers().stream()
                .anyMatch(p -> p.getName().equalsIgnoreCase(playerName));
    }

    public boolean isNotRegistered(String playerName) {
        return !CacheStorage.getInstance().getRegisteredPlayers().contains(playerName);
    }

}
