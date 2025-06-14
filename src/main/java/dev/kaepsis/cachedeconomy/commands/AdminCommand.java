package dev.kaepsis.cachedeconomy.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import dev.kaepsis.cachedeconomy.config.GeneralConfig;
import dev.kaepsis.cachedeconomy.config.LangConfig;
import dev.kaepsis.kmanagers.Chat;
import org.bukkit.command.CommandSender;

@CommandAlias("ace")
@CommandPermission("cachedeconomy.admin")
@SuppressWarnings("unused")
public class AdminCommand extends BaseCommand {

    @Subcommand("reload")
    public void reload(CommandSender sender) {
        GeneralConfig.getInstance().reload();
        LangConfig.getInstance().reload();
        Chat.getInstance().send(sender, LangConfig.getInstance().CONFIG_RELOADED);
    }

}
