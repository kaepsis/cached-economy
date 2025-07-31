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

    @CommandCompletion("@players")
    @Default
    public void execute(Player player, String targetName, double amount) {
        List<String> registeredPlayers = CacheStorage.getInstance().getRegisteredPlayers();
        if (!registeredPlayers.contains(targetName)) {
            Chat.getInstance().send(player, LangConfig.getInstance().dbPlayerNotFound, "{target}", targetName);
            return;
        }
        Player target = Bukkit.getPlayer(targetName);
        if (player == target) {
            Chat.getInstance().send(player, LangConfig.getInstance().payCannotSendToYourself);
            return;
        }
        boolean isOnline = Bukkit.getOnlinePlayers()
                .stream()
                .anyMatch(p -> p.getName().equalsIgnoreCase(targetName));
        if (!isOnline) {
            Chat.getInstance().send(player, LangConfig.getInstance().gnPlayerNotFound, "{target}", targetName);
            return;
        }
        double balance = CacheStorage.getInstance().getCachedBalance(targetName);
        if (BalanceUtils.getInstance().isNotValidAmount(amount)) {
            Chat.getInstance().send(player, LangConfig.getInstance().gnInvalidAmount, "{amount}", amount);
            return;
        }
        if (balance < amount) {
            Chat.getInstance().send(player, LangConfig.getInstance().gnInsufficientBalance);
            return;
        }
        double targetBalance = CacheStorage.getInstance().getCachedBalance(targetName);
        double updatedSenderBalance = balance - amount;
        double updatedTargetBalance = targetBalance + amount;
        CacheStorage.getInstance().setBalance(player.getName(), updatedSenderBalance);
        CacheStorage.getInstance().setBalance(targetName, updatedTargetBalance);
        Chat.getInstance().send(player, LangConfig.getInstance().payMoneySent, "{amount}", amount, "{symbol}", GeneralConfig.getInstance().currencySymbol, "{target}", targetName);
        Chat.getInstance().send(target, LangConfig.getInstance().payMoneyReceived, "{amount}", amount, "{symbol}", GeneralConfig.getInstance().currencySymbol, "{sender}", player.getName());
    }


}
