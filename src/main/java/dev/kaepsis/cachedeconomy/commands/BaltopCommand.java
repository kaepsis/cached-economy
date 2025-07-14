package dev.kaepsis.cachedeconomy.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import com.github.kaepsis.Chat;
import dev.kaepsis.cachedeconomy.config.GeneralConfig;
import dev.kaepsis.cachedeconomy.config.LangConfig;
import dev.kaepsis.cachedeconomy.storage.impl.CacheStorage;
import org.bukkit.entity.Player;

import java.util.concurrent.atomic.AtomicInteger;

@CommandAlias("baltop")
public class BaltopCommand extends BaseCommand {

    @Default
    public void root(Player player) {
        Chat.getInstance().send(player, LangConfig.getInstance().ECO_BALTOP_TOP);
        AtomicInteger count = new AtomicInteger(1);
        CacheStorage.getInstance().getTopTen().forEach(entry -> Chat.getInstance().send(
                player,
                LangConfig.getInstance().ECO_BALTOP_ENTRY,
                "{ranking}", count.getAndIncrement(),
                "{playerName}", entry.getKey(),
                "{amount}", entry.getValue(),
                "{symbol}", GeneralConfig.getInstance().currencySymbol
        ));
    }

}
