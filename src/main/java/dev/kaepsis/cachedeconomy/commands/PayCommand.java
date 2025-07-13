package dev.kaepsis.cachedeconomy.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Default;
import com.github.kaepsis.Chat;
import dev.kaepsis.cachedeconomy.config.GeneralConfig;
import dev.kaepsis.cachedeconomy.config.LangConfig;
import dev.kaepsis.cachedeconomy.storage.impl.CacheStorage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

@CommandAlias("pay")
@SuppressWarnings("unused")
public class PayCommand extends BaseCommand {

    @CommandCompletion("@players")
    @Default
    public void execute(Player player, String targetName, double amount) {
        List<String> registeredPlayers = CacheStorage.getInstance().getRegisteredPlayers();
        if (!registeredPlayers.contains(targetName)) {
            Chat.getInstance().send(player, LangConfig.getInstance().PLAYER_NOT_FOUND, "{target}", targetName);
            return;
        }
        Player target = Bukkit.getPlayer(targetName);
        if (player == target) {
            Chat.getInstance().send(player, LangConfig.getInstance().CANT_SEND_TO_YOURSELF);
            return;
        }
        boolean isOnline = Bukkit.getOnlinePlayers().stream()
                .anyMatch(p -> p.getName().equalsIgnoreCase(targetName));
        CacheStorage.getInstance().getCachedBalance(player.getName()).thenAccept(balance -> {
            if (isNotValidAmount(amount)) {
                Chat.getInstance().send(player, LangConfig.getInstance().INVALID_AMOUNT, "{amount}", amount);
                return;
            }
            if (balance < amount) {
                Chat.getInstance().send(player, LangConfig.getInstance().INSUFFICIENT_BALANCE);
                return;
            }
            CacheStorage.getInstance().getCachedBalance(targetName).thenAccept(targetBalance -> {
                double updatedSenderBalance = balance - amount;
                double updatedTargetBalance = targetBalance + amount;
                CacheStorage.getInstance().setBalance(player.getName(), updatedSenderBalance);
                CacheStorage.getInstance().setBalance(targetName, updatedTargetBalance);
                Chat.getInstance().send(player, LangConfig.getInstance().MONEY_SENT, "{amount}", amount, "{symbol}", GeneralConfig.getInstance().currencySymbol, "{target}", targetName);
                Chat.getInstance().send(target, LangConfig.getInstance().MONEY_RECEIVED, "{amount}", amount, "{symbol}", GeneralConfig.getInstance().currencySymbol, "{sender}", player.getName());
            });
        });
    }

    boolean isNotValidAmount(double amount) {
        return amount < 0 || amount > Double.MAX_VALUE || Double.isNaN(amount) || Double.isInfinite(amount) || Integer.signum((int) amount) != 1;
    }

}
