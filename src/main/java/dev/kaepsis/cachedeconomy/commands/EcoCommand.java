package dev.kaepsis.cachedeconomy.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import dev.kaepsis.cachedeconomy.config.GeneralConfig;
import dev.kaepsis.cachedeconomy.config.LangConfig;
import dev.kaepsis.cachedeconomy.manager.ChatManager;
import dev.kaepsis.cachedeconomy.manager.PlayerManager;
import dev.kaepsis.cachedeconomy.storage.impl.CacheStorage;
import dev.kaepsis.cachedeconomy.storage.impl.PlayerStorage;
import org.bukkit.command.CommandSender;

import java.util.concurrent.CompletableFuture;

@CommandAlias("eco")
@CommandPermission("cachedeconomy.eco")
@SuppressWarnings("unused")
public class EcoCommand extends BaseCommand {

    @Subcommand("give")
    @Syntax("<player> <amount>")
    @CommandCompletion("@registeredPlayers")
    public void give(CommandSender sender, String targetName, double amount) {
        if (PlayerManager.getInstance().isNotRegistered(targetName)) {
            ChatManager.getInstance().send(sender, LangConfig.getInstance().PLAYER_NOT_FOUND, "{target}", targetName);
            return;
        }
        CompletableFuture<Double> balanceFuture = PlayerManager.getInstance().isOnline(targetName)
                ? CacheStorage.getInstance().getCachedBalance(targetName)
                : PlayerStorage.getInstance().getCachedBalance(targetName);
        balanceFuture.thenAccept(balance -> {
            double updatedBalance = balance + amount;
            if (!PlayerManager.getInstance().isOnline(targetName)) {
                PlayerStorage.getInstance().setBalance(targetName, updatedBalance);
            } else {
                CacheStorage.getInstance().setBalance(targetName, updatedBalance);
                ChatManager.getInstance().send(sender, LangConfig.getInstance().ECO_GIVE_RECEIVER, "{amount}", amount, "{symbol}", GeneralConfig.getInstance().currencySymbol);
            }
            ChatManager.getInstance().send(sender, LangConfig.getInstance().ECO_GIVE_SENDER, "{target}", targetName, "{amount}", amount, "{symbol}", GeneralConfig.getInstance().currencySymbol);
        });
    }

    @Subcommand("set")
    @Syntax("<player> <amount>")
    @CommandCompletion("@registeredPlayers")
    public void set(CommandSender sender, String targetName, double amount) {
        if (PlayerManager.getInstance().isNotRegistered(targetName)) {
            ChatManager.getInstance().send(sender, LangConfig.getInstance().PLAYER_NOT_FOUND, "{target}", targetName);
            return;
        }
        CompletableFuture<Double> balanceFuture = PlayerManager.getInstance().isOnline(targetName)
                ? CacheStorage.getInstance().getCachedBalance(targetName)
                : PlayerStorage.getInstance().getCachedBalance(targetName);
        balanceFuture.thenAccept(balance -> {
            if (!PlayerManager.getInstance().isOnline(targetName)) {
                PlayerStorage.getInstance().setBalance(targetName, balance);
            } else {
                CacheStorage.getInstance().setBalance(targetName, balance);
                ChatManager.getInstance().send(sender, LangConfig.getInstance().ECO_SET_RECEIVER, "{total}", amount, "{symbol}", GeneralConfig.getInstance().currencySymbol);
            }
            ChatManager.getInstance().send(sender, LangConfig.getInstance().ECO_SET_SENDER, "{target}", targetName, "{total}", amount, "{symbol}", GeneralConfig.getInstance().currencySymbol);
        });
    }

    @Subcommand("reset")
    @Syntax("<player|*>")
    @CommandCompletion("@registeredPlayers")
    public void reset(CommandSender sender, String targetName) {
        if (targetName.equals("*")) {
            for (String name : PlayerStorage.getInstance().getRegisteredPlayers()) {
                PlayerStorage.getInstance().setBalance(name, GeneralConfig.getInstance().startingBalance);
            }
            ChatManager.getInstance().send(sender, LangConfig.getInstance().ECO_RESET_ALL, "{total}", GeneralConfig.getInstance().startingBalance, "{symbol}", GeneralConfig.getInstance().currencySymbol);
            return;
        }
        if (PlayerManager.getInstance().isNotRegistered(targetName)) {
            ChatManager.getInstance().send(sender, LangConfig.getInstance().PLAYER_NOT_FOUND, "{target}", targetName);
            return;
        }
        if (!PlayerManager.getInstance().isOnline(targetName)) {
            PlayerStorage.getInstance().setBalance(targetName, GeneralConfig.getInstance().startingBalance);
        } else {
            CacheStorage.getInstance().setBalance(targetName, GeneralConfig.getInstance().startingBalance);
            ChatManager.getInstance().send(sender, LangConfig.getInstance().ECO_RESET_RECEIVER, "{total}", GeneralConfig.getInstance().startingBalance, "{symbol}", GeneralConfig.getInstance().currencySymbol);
        }
        ChatManager.getInstance().send(sender, LangConfig.getInstance().ECO_RESET_SENDER, "{target}", targetName, "{total}", GeneralConfig.getInstance().startingBalance, "{symbol}", GeneralConfig.getInstance().currencySymbol);
    }

}
