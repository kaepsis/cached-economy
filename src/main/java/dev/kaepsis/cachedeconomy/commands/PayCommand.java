package dev.kaepsis.cachedeconomy.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Default;
import com.github.kaepsis.Chat;
import dev.kaepsis.cachedeconomy.config.GeneralConfig;
import dev.kaepsis.cachedeconomy.config.LangConfig;
import dev.kaepsis.cachedeconomy.storage.BalanceUtils;
import dev.kaepsis.cachedeconomy.storage.impl.CacheStorage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

@CommandAlias("pay")
public class PayCommand extends BaseCommand {
    private final Chat chat;
    private final LangConfig langConfig;
    private final CacheStorage cacheStorage;
    private final BalanceUtils balanceUtils;
    private final String currencySymbol;

    public PayCommand() {
        this.chat = Chat.getInstance();
        this.langConfig = LangConfig.getInstance();
        this.cacheStorage = CacheStorage.getInstance();
        this.balanceUtils = BalanceUtils.getInstance();
        this.currencySymbol = GeneralConfig.getInstance().currencySymbol;
    }

    @CommandCompletion("@players")
    @Default
    public void execute(Player player, String targetName, double amount) {
        List<String> registeredPlayers = cacheStorage.getRegisteredPlayers();

        if (!isValidTransaction(player, targetName, amount, registeredPlayers)) {
            return;
        }

        Player target = Bukkit.getPlayer(targetName);
        processTransaction(player, target, targetName, amount);
    }

    private boolean isValidTransaction(Player player, String targetName, double amount, List<String> registeredPlayers) {
        if (!registeredPlayers.contains(targetName)) {
            chat.send(player, langConfig.dbPlayerNotFound,
                    "{target}", targetName
            );
            return false;
        }

        if (player.getName().equalsIgnoreCase(targetName)) {
            chat.send(player, langConfig.payCannotSendToYourself);
            return false;
        }

        if (Bukkit.getOnlinePlayers().stream().noneMatch(p -> p.getName().equalsIgnoreCase(targetName))) {
            chat.send(player, langConfig.gnPlayerNotFound,
                    "{target}", targetName,
                    "{symbol}", currencySymbol
            );
            return false;
        }

        if (balanceUtils.isNotValidAmount(String.valueOf(amount))) {
            chat.send(player, langConfig.gnInvalidAmount,
                    "{amount}", amount,
                    "{symbol}", currencySymbol
            );
            return false;
        }

        double senderBalance = cacheStorage.getCachedBalance(player.getName());
        if (senderBalance < amount) {
            chat.send(player, langConfig.gnInsufficientBalance);
            return false;
        }

        return true;
    }

    private void processTransaction(Player sender, Player target, String targetName, double amount) {
        double senderBalance = cacheStorage.getCachedBalance(sender.getName());
        double targetBalance = cacheStorage.getCachedBalance(targetName);

        cacheStorage.setBalance(sender.getName(), senderBalance - amount);
        cacheStorage.setBalance(targetName, targetBalance + amount);

        chat.send(sender, langConfig.payMoneySent,
                "{amount}", amount,
                "{target}", targetName,
                "{symbol}", currencySymbol
        );

        chat.send(target, langConfig.payMoneyReceived,
                "{amount}", amount,
                "{sender}", sender.getName(),
                "{symbol}", currencySymbol
        );
    }
}