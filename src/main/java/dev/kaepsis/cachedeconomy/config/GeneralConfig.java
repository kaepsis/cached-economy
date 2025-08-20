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
    public String username = "root";
    @Path("storage.password")
    public String password = "root";
    @Path("storage.host")
    public String host = "localhost";
    @Path("storage.port")
    public int port = 3306;
    @Path("storage.database")
    public String database = "cachedeconomy";
    @Path("starting-balance")
    public double startingBalance = 0;
    @Path("currency-symbol")
    public String currencySymbol = "$";
    @Path("formattings.thousands")
    public String thousands = "k";
    @Path("formattings.millions")
    public String millions = "M";
    @Path("formattings.billions")
    public String billions = "B";
    @Path("formattings.trillions")
    public String trillions = "T";

    private GeneralConfig() {
    }

    public static GeneralConfig getInstance() {
        if (instance == null) {
            instance = new GeneralConfig();
        }
        return instance;
    }

}
