package dev.kaepsis.cachedeconomy;

import co.aikar.commands.PaperCommandManager;
import dev.kaepsis.cachedeconomy.commands.AdminCommand;
import dev.kaepsis.cachedeconomy.commands.BalanceCommand;
import dev.kaepsis.cachedeconomy.commands.EcoCommand;
import dev.kaepsis.cachedeconomy.commands.PayCommand;
import dev.kaepsis.cachedeconomy.config.GeneralConfig;
import dev.kaepsis.cachedeconomy.config.LangConfig;
import dev.kaepsis.cachedeconomy.hooks.PlaceholderAPIHook;
import dev.kaepsis.cachedeconomy.hooks.VaultHook;
import dev.kaepsis.cachedeconomy.listeners.PlayerListener;
import dev.kaepsis.cachedeconomy.services.DatabaseService;
import dev.kaepsis.cachedeconomy.storage.impl.CacheStorage;
import dev.kaepsis.cachedeconomy.storage.impl.PlayerStorage;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.logging.Logger;

public class Main extends JavaPlugin {

    public static Main instance;

    public static PaperCommandManager manager;

    public static HashMap<String, Double> savedPlayers;

    public static Logger logger = Logger.getLogger("Minecraft");

    @Override
    public void onEnable() {
        instance = this;
        savedPlayers = new HashMap<>();
        config();
        DatabaseService.getInstance().openConnection();
        new VaultHook();
        new PlaceholderAPIHook().register();
        DatabaseService.getInstance().cachePlayers();
        commands();
        events();
    }

    @Override
    public void onDisable() {
        instance = null;
        DatabaseService.getInstance().removeCachedPlayers();
    }

    void config() {
        GeneralConfig.getInstance().registerConfig(this);
        LangConfig.getInstance().registerConfig(this);
    }

    void commands() {
        manager = new PaperCommandManager(this);
        completions();
        manager.registerCommand(new PayCommand());
        manager.registerCommand(new EcoCommand());
        manager.registerCommand(new AdminCommand());
        manager.registerCommand(new BalanceCommand());
    }

    void events() {
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new PlayerListener(), this);
    }

    void completions() {
        manager.getCommandCompletions().registerAsyncCompletion("registeredPlayers", c -> PlayerStorage.getInstance().getRegisteredPlayers());
        manager.getCommandCompletions().registerAsyncCompletion("cachedPlayers", c -> CacheStorage.getInstance().getRegisteredPlayers());
    }

}
