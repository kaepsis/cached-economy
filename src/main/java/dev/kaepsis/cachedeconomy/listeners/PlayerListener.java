package dev.kaepsis.cachedeconomy.listeners;

import dev.kaepsis.cachedeconomy.manager.PlayerManager;
import dev.kaepsis.cachedeconomy.storage.impl.CacheStorage;
import dev.kaepsis.cachedeconomy.storage.impl.PlayerStorage;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (PlayerManager.getInstance().isNotRegistered(player.getName())) {
            PlayerStorage.getInstance().registerPlayer(player);
        }
        CacheStorage.getInstance().registerPlayer(player);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        CacheStorage.getInstance().getCachedBalance(player.getName()).thenAccept(balance -> {
            PlayerStorage.getInstance().setBalance(player.getName(), balance);
        });
    }

}
