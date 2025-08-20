package dev.kaepsis.cachedeconomy.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import com.github.kaepsis.Chat;
import dev.kaepsis.cachedeconomy.config.GeneralConfig;
import dev.kaepsis.cachedeconomy.config.LangConfig;
import dev.kaepsis.cachedeconomy.storage.impl.CacheStorage;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;

@CommandAlias("baltop")
public class BaltopCommand extends BaseCommand {
    private final Chat chat;
    private final LangConfig langConfig;
    private final CacheStorage cacheStorage;
    private final String currencySymbol;

    public BaltopCommand() {
        this.chat = Chat.getInstance();
        this.langConfig = LangConfig.getInstance();
        this.currencySymbol = GeneralConfig.getInstance().currencySymbol;
        this.cacheStorage = CacheStorage.getInstance();
    }

    @Default
    public void root(Player player) {
        chat.send(player, langConfig.ecoBaltopTop);

        List<Map.Entry<String, Double>> topPlayers = cacheStorage.getTopTen();

        int ranking = 1;
        for (Map.Entry<String, Double> entry : topPlayers) {
            chat.send(player, langConfig.ecoBaltopEntry,
                    "{ranking}", ranking++,
                    "{playerName}", entry.getKey(),
                    "{amount}", entry.getValue(),
                    "{symbol}", currencySymbol
            );
        }
    }
}