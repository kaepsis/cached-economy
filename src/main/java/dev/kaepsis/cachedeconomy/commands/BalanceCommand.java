package dev.kaepsis.cachedeconomy.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.github.kaepsis.Chat;
import dev.kaepsis.cachedeconomy.config.GeneralConfig;
import dev.kaepsis.cachedeconomy.config.LangConfig;
import dev.kaepsis.cachedeconomy.storage.impl.CacheStorage;
import dev.kaepsis.cachedeconomy.storage.impl.PlayerStorage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@CommandAlias("balance|bal")
public class BalanceCommand extends BaseCommand {

    @Default
    public void root(Player player) {
        Chat.getInstance().send(player,
                LangConfig.getInstance().ecoBalanceYours, "{amount}",
                CacheStorage.getInstance().getBalanceFormatted(player.getName()), "{symbol}", GeneralConfig.getInstance().currencySymbol
        );
    }

    @CommandPermission("cachedeconomy.balance.others")
    @Syntax("bal <player>")
    @CommandCompletion("@registeredPlayers")
    @Default
    public void withArguments(Player player, String targetName) {
        if (Bukkit.getOnlinePlayers().stream().noneMatch(p -> p.getName().equalsIgnoreCase(targetName))) {
            Chat.getInstance().send(player,
                    LangConfig.getInstance().ecoBalanceTarget, "{amount}",
                    PlayerStorage.getInstance().getBalanceFormatted(targetName), "{symbol}", GeneralConfig.getInstance().currencySymbol,
                    "{target}", targetName
            );
            return;
        }
        Chat.getInstance().send(player,
                LangConfig.getInstance().ecoBalanceTarget, "{amount}",
                CacheStorage.getInstance().getBalanceFormatted(player.getName()), "{symbol}", GeneralConfig.getInstance().currencySymbol,
                "{target}", targetName
        );
    }

}
