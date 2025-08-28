package dev.kaepsis.cachedeconomy.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.github.kaepsis.Chat;
import dev.kaepsis.cachedeconomy.config.GeneralConfig;
import dev.kaepsis.cachedeconomy.config.LangConfig;
import dev.kaepsis.cachedeconomy.manager.PlayerManager;
import dev.kaepsis.cachedeconomy.storage.impl.CacheStorage;
import dev.kaepsis.cachedeconomy.storage.impl.PlayerStorage;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("eco")
@CommandPermission("cachedeconomy.eco")
public class EcoCommand extends BaseCommand {
    private final Chat chat;
    private final LangConfig langConfig;
    private final GeneralConfig generalConfig;
    private final PlayerManager playerManager;
    private final CacheStorage cacheStorage;
    private final PlayerStorage playerStorage;
    private final String currencySymbol;

    public EcoCommand() {
        this.chat = Chat.getInstance();
        this.langConfig = LangConfig.getInstance();
        this.generalConfig = GeneralConfig.getInstance();
        this.playerManager = PlayerManager.getInstance();
        this.cacheStorage = CacheStorage.getInstance();
        this.playerStorage = PlayerStorage.getInstance();
        this.currencySymbol = generalConfig.currencySymbol;
    }

    @Subcommand("give")
    @Syntax("<player> <amount>")
    @CommandCompletion("@registeredPlayers")
    public void give(CommandSender sender, String targetName, double amount) {
        if (playerManager.isNotRegistered(targetName)) {
            chat.send(sender, langConfig.dbPlayerNotFound,
                    "{target}", targetName
            );
            return;
        }

        double balance = playerManager.isOnline(targetName) ?
                cacheStorage.getCachedBalance(targetName) :
                playerStorage.getCachedBalance(targetName);

        double updatedBalance = balance + amount;
        updateBalanceAndNotify(sender, targetName, amount, updatedBalance, langConfig.ecoGiveReceiver, langConfig.ecoGiveSender);
    }

    @Subcommand("set")
    @Syntax("<player> <amount>")
    @CommandCompletion("@registeredPlayers")
    public void set(CommandSender sender, String targetName, double amount) {
        if (playerManager.isNotRegistered(targetName)) {
            chat.send(sender, langConfig.dbPlayerNotFound,
                    "{target}", targetName
            );
            return;
        }

        updateBalanceAndNotify(sender, targetName, amount, amount, langConfig.ecoSetReceiver, langConfig.ecoSetSender);
    }

    @Subcommand("reset")
    @Syntax("<player|*>")
    @CommandCompletion("@registeredPlayers")
    public void reset(CommandSender sender, String targetName) {
        double startingBalance = generalConfig.startingBalance;

        if (targetName.equals("*")) {
            resetAllPlayers(sender);
            return;
        }

        if (playerManager.isNotRegistered(targetName)) {
            chat.send(sender, langConfig.dbPlayerNotFound,
                    "{target}", targetName
            );
            return;
        }

        updateBalanceAndNotify(sender, targetName, startingBalance, startingBalance,
                langConfig.ecoResetReceiver, langConfig.ecoResetSender);
    }

    private void resetAllPlayers(CommandSender sender) {
        double startingBalance = generalConfig.startingBalance;
        for (String name : playerStorage.getRegisteredPlayers()) {
            if (playerManager.isOnline(name)) {
                Player target = Bukkit.getPlayer(name);
                cacheStorage.setBalance(name, startingBalance);
                chat.send(target, langConfig.ecoResetReceiver,
                        "{amount}", startingBalance,
                        "{symbol}", currencySymbol
                );
            } else {
                playerStorage.setBalance(name, startingBalance);
            }
        }
        chat.send(sender, langConfig.ecoResetAll,
                "{amount}", startingBalance,
                "{symbol}", currencySymbol
        );
    }

    private void updateBalanceAndNotify(CommandSender sender, String targetName, double amount,
                                        double newBalance, String receiverMessage, String senderMessage) {
        if (playerManager.isOnline(targetName)) {
            Player target = Bukkit.getPlayer(targetName);
            cacheStorage.setBalance(targetName, newBalance);
            chat.send(target, receiverMessage,
                    "{amount}", amount,
                    "{symbol}", currencySymbol
            );
        } else {
            playerStorage.setBalance(targetName, newBalance);
        }

        chat.send(sender, senderMessage,
                "{target}", targetName,
                "{amount}", amount,
                "{symbol}", currencySymbol
        );
    }
}