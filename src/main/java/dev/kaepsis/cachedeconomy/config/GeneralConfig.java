package dev.kaepsis.cachedeconomy.config;

import net.pino.simpleconfig.BaseConfig;
import net.pino.simpleconfig.annotations.Config;
import net.pino.simpleconfig.annotations.ConfigFile;
import net.pino.simpleconfig.annotations.inside.Path;

@Config
@ConfigFile("config.yml")
public class GeneralConfig extends BaseConfig {

    private static GeneralConfig instance = null;
    @Path("storage.username")
    public String username;
    @Path("storage.password")
    public String password;
    @Path("storage.host")
    public String host;
    @Path("storage.port")
    public int port;
    @Path("storage.database")
    public String database;
    @Path("starting-balance")
    public double startingBalance = 0;
    @Path("currency-symbol")
    public String currencySymbol = "$";

    private GeneralConfig() {
    }

    public static GeneralConfig getInstance() {
        if (instance == null) {
            instance = new GeneralConfig();
        }
        return instance;
    }

}
