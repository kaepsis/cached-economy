package dev.kaepsis.cachedeconomy.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import dev.kaepsis.cachedeconomy.config.GeneralConfig;
import dev.kaepsis.cachedeconomy.config.LangConfig;

@CommandAlias("ace")
@CommandPermission("cachedeconomy.admin")
@SuppressWarnings("unused")
public class AdminCommand extends BaseCommand {

    @Subcommand("reload")
    public void reload() {
        GeneralConfig.getInstance().reload();
        LangConfig.getInstance().reload();
    }

}
