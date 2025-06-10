package dev.kaepsis.cachedeconomy.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import dev.kaepsis.cachedeconomy.config.GeneralConfig;
import dev.kaepsis.cachedeconomy.config.LangConfig;
import dev.kaepsis.cachedeconomy.manager.ChatManager;
import dev.kaepsis.cachedeconomy.storage.impl.CacheStorage;
import dev.kaepsis.cachedeconomy.storage.impl.PlayerStorage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@CommandAlias("balance|bal")
@SuppressWarnings("unused")
public class BalanceCommand extends BaseCommand {

    @Default
    public void root(Player player) {
        CacheStorage.getInstance().getCachedBalance(player.getName()).thenAccept(balance -> {
            ChatManager.getInstance().send(player,
                    LangConfig.getInstance().ECO_BALANCE_YOURS, "{amount}",
                    balance, "{symbol}", GeneralConfig.getInstance().currencySymbol
            );
        });
    }

    @CommandPermission("cachedeconomy.balance.others")
    @Syntax("bal <player>")
    @CommandCompletion("@registeredPlayers")
    public void withArguments(Player player, String targetName) {
        if (Bukkit.getOnlinePlayers().stream().noneMatch(p -> p.getName().equalsIgnoreCase(targetName))) {
            PlayerStorage.getInstance().getCachedBalance(targetName).thenAccept(balance -> {
                ChatManager.getInstance().send(player,
                        LangConfig.getInstance().ECO_BALANCE_TARGET, "{amount}",
                        balance, "{symbol}", GeneralConfig.getInstance().currencySymbol,
                        "{target}", targetName
                );
            });
            return;
        }
        CacheStorage.getInstance().getCachedBalance(targetName).thenAccept(balance -> {
            ChatManager.getInstance().send(player,
                    LangConfig.getInstance().ECO_BALANCE_TARGET, "{amount}",
                    balance, "{symbol}", GeneralConfig.getInstance().currencySymbol,
                    "{target}", targetName
            );
        });
    }

}
