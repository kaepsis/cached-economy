package dev.kaepsis.cachedeconomy.listeners;

import dev.kaepsis.cachedeconomy.Main;
import dev.kaepsis.cachedeconomy.manager.PlayerManager;
import dev.kaepsis.cachedeconomy.storage.impl.CacheStorage;
import dev.kaepsis.cachedeconomy.storage.impl.PlayerStorage;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {
    private final PlayerManager playerManager;
    private final PlayerStorage playerStorage;
    private final CacheStorage cacheStorage;

    public PlayerListener() {
        this.playerManager = PlayerManager.getInstance();
        this.playerStorage = PlayerStorage.getInstance();
        this.cacheStorage = CacheStorage.getInstance();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (playerManager.isNotRegistered(player.getName())) {
            playerStorage.registerPlayer(player);
        }
        cacheStorage.registerPlayer(player);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        String playerName = player.getName();

        double balance = cacheStorage.getBalance(playerName);
        playerStorage.setBalance(playerName, balance);
        Main.savedPlayers.remove(playerName);
    }
}