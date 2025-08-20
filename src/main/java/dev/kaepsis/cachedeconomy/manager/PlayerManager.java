package dev.kaepsis.cachedeconomy.manager;

import dev.kaepsis.cachedeconomy.storage.impl.PlayerStorage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Collection;

public class PlayerManager {

    private static volatile PlayerManager instance;
    private final PlayerStorage playerStorage;

    private PlayerManager() {
        this.playerStorage = PlayerStorage.getInstance();
    }

    public static PlayerManager getInstance() {
        if (instance == null) {
            synchronized (PlayerManager.class) {
                if (instance == null) {
                    instance = new PlayerManager();
                }
            }
        }
        return instance;
    }

    public boolean isOnline(String playerName) {
        Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();
        return onlinePlayers.stream()
                .anyMatch(player -> player.getName().equalsIgnoreCase(playerName));
    }

    public boolean isNotRegistered(String playerName) {
        return !playerStorage.getRegisteredPlayers().contains(playerName);
    }
}