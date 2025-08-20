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

    private final String currencySymbol;
    private final Chat chat;
    private final CacheStorage cacheStorage;
    private final PlayerStorage playerStorage;
    private final LangConfig langConfig;

    public BalanceCommand() {
        this.currencySymbol = GeneralConfig.getInstance().currencySymbol;
        this.chat = Chat.getInstance();
        this.cacheStorage = CacheStorage.getInstance();
        this.playerStorage = PlayerStorage.getInstance();
        this.langConfig = LangConfig.getInstance();
    }

    @Default
    public void root(Player player) {
        chat.send(player, langConfig.ecoBalanceYours,
                "{amount}", cacheStorage.getBalanceFormatted(player.getName()),
                "{symbol}", currencySymbol
        );
    }

    @CommandPermission("cachedeconomy.balance.others")
    @Syntax("bal <player>")
    @CommandCompletion("@registeredPlayers")
    @Default
    public void withArguments(Player player, String targetName) {
        boolean isOnline = Bukkit.getPlayer(targetName) != null;

        if (!isOnline && !playerStorage.isPlayerRegistered(targetName)) {
            chat.send(player, langConfig.dbPlayerNotFound, "{target}", targetName);
            return;
        }

        String formattedBalance = isOnline ?
                cacheStorage.getBalanceFormatted(targetName) :
                playerStorage.getBalanceFormatted(targetName);

        chat.send(player, langConfig.ecoBalanceTarget,
                "{symbol}", currencySymbol,
                "{target}", targetName,
                "{amount}", formattedBalance
        );
    }
}