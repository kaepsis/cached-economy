package dev.kaepsis.cachedeconomy;

import co.aikar.commands.PaperCommandManager;
import dev.kaepsis.cachedeconomy.commands.*;
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

import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public class Main extends JavaPlugin {

    public static Main instance;
    public static Map<String, Double> savedPlayers;
    public static NavigableMap<Double, String> suffixes;
    public static Logger logger = Logger.getLogger("Minecraft");

    @Override
    public void onEnable() {
        instance = this;
        savedPlayers = new ConcurrentHashMap<>();
        config();
        initializeSuffixes();
        initializeDatabase();
        setupHooks();
        commands();
        events();
        CacheStorage.getInstance().loadAllPlayers();
    }

    @Override
    public void onDisable() {
        DatabaseService.getInstance().shutdown();
        instance = null;
    }

    private void initializeSuffixes() {
        suffixes = new TreeMap<>() {{
            put(1_000D, GeneralConfig.getInstance().thousands);
            put(1_000_000D, GeneralConfig.getInstance().millions);
            put(1_000_000_000D, GeneralConfig.getInstance().billions);
            put(1_000_000_000_000D, GeneralConfig.getInstance().trillions);
        }};
    }

    private void initializeDatabase() {
        DatabaseService.getInstance();
        DatabaseService.getInstance().cachePlayers();
    }

    private void setupHooks() {
        new VaultHook();
        new PlaceholderAPIHook().register();
    }

    void config() {
        GeneralConfig.getInstance().registerConfig(this);
        LangConfig.getInstance().registerConfig(this);
    }

    void commands() {
        PaperCommandManager manager = new PaperCommandManager(this);
        completions(manager);
        manager.registerCommand(new PayCommand());
        manager.registerCommand(new EcoCommand());
        manager.registerCommand(new AdminCommand());
        manager.registerCommand(new BalanceCommand());
        manager.registerCommand(new BaltopCommand());
        manager.registerCommand(new BetCommand());
    }

    void events() {
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new PlayerListener(), this);
    }

    void completions(PaperCommandManager manager) {
        manager.getCommandCompletions().registerAsyncCompletion(
                "registeredPlayers",
                c -> PlayerStorage.getInstance().getRegisteredPlayers()
        );
        manager.getCommandCompletions().registerAsyncCompletion(
                "cachedPlayers",
                c -> CacheStorage.getInstance().getRegisteredPlayers()
        );
    }
}