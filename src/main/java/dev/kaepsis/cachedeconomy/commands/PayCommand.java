package dev.kaepsis.cachedeconomy.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import dev.kaepsis.cachedeconomy.config.GeneralConfig;
import dev.kaepsis.cachedeconomy.config.LangConfig;
import dev.kaepsis.cachedeconomy.manager.ChatManager;
import dev.kaepsis.cachedeconomy.storage.impl.CacheStorage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

@CommandAlias("pay")
@SuppressWarnings("unused")
public class PayCommand extends BaseCommand {

    @CommandCompletion("players")
    public void execute(Player player, String targetName, double amount) {
        List<String> registeredPlayers = CacheStorage.getInstance().getRegisteredPlayers();
        if (!registeredPlayers.contains(targetName)) {
            ChatManager.getInstance().send(player, LangConfig.getInstance().PLAYER_NOT_FOUND, "{target}", targetName);
            return;
        }
        Player target = Bukkit.getPlayer(targetName);
        if (player == target) {
            ChatManager.getInstance().send(player, LangConfig.getInstance().CANT_SEND_TO_YOURSELF);
            return;
        }
        boolean isOnline = Bukkit.getOnlinePlayers().stream()
                .anyMatch(p -> p.getName().equalsIgnoreCase(targetName));
        CacheStorage.getInstance().getCachedBalance(player.getName()).thenAccept(balance -> {
            if (balance < amount) {
                ChatManager.getInstance().send(player, LangConfig.getInstance().INSUFFICIENT_BALANCE);
                return;
            }
            CacheStorage.getInstance().getCachedBalance(targetName).thenAccept(targetBalance -> {
                double updatedSenderBalance = balance - amount;
                double updatedTargetBalance = targetBalance + amount;
                CacheStorage.getInstance().setBalance(player.getName(), updatedSenderBalance);
                CacheStorage.getInstance().setBalance(targetName, updatedTargetBalance);
                ChatManager.getInstance().send(player, LangConfig.getInstance().MONEY_SENT, "{amount}", amount, "{symbol}", GeneralConfig.getInstance().currencySymbol, "{target}", targetName);
                ChatManager.getInstance().send(target, LangConfig.getInstance().MONEY_RECEIVED, "{amount}", amount, "{symbol}", GeneralConfig.getInstance().currencySymbol, "{sender}", player.getName());
            });
        });
    }

}
