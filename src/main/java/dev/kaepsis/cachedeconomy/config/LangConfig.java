package dev.kaepsis.cachedeconomy.config;

import net.pino.simpleconfig.BaseConfig;
import net.pino.simpleconfig.annotations.Config;
import net.pino.simpleconfig.annotations.ConfigFile;
import net.pino.simpleconfig.annotations.inside.Path;

@Config
@ConfigFile("lang.yml")
public class LangConfig extends BaseConfig {

    private static LangConfig instance = null;
    @Path("general.insufficient-balance")
    public String INSUFFICIENT_BALANCE = "&6&lECONOMY &8| &cYou don't have enough money";
    @Path("general.invalid-amount")
    public String INVALID_AMOUNT = "&6&lECONOMY &8| &cYou can't operate with that amount ({amount})";
    @Path("general.config-reloaded")
    public String CONFIG_RELOADED = "&6&lECONOMY &8| &fConfig has been reloaded &esuccessfully";
    @Path("pay.cant-send-to-yourself")
    public String CANT_SEND_TO_YOURSELF = "&6&lECONOMY &8| &cYou can't send money to yourself";
    @Path("pay.money-sent")
    public String MONEY_SENT = "&6&lECONOMY &8| &fYou sent &e{amount}{symbol} to {target}";
    @Path("pay.money-received")
    public String MONEY_RECEIVED = "&6&lECONOMY &8| &fYou received &e{amount}{symbol} &ffrom &e{sender}";
    @Path("database-error.player-not-found")
    public String PLAYER_NOT_FOUND = "&6&lECONOMY &8| &c{target} was not found as a player in the database";
    @Path("eco.give.sender")
    public String ECO_GIVE_SENDER = "&6&lECONOMY &8| &fYou gave &e{amount}{symbol} &fto &e{target}";
    @Path("eco.give.receiver")
    public String ECO_GIVE_RECEIVER = "&6&lECONOMY &8| &fYou received &e{amount}{symbol}";
    @Path("eco.set.sender")
    public String ECO_SET_SENDER = "&6&lECONOMY &8| &fYou set &e{target}&f's balance to &e{amount}{symbol}";
    @Path("eco.set.receiver")
    public String ECO_SET_RECEIVER = "&6&lECONOMY &8| &fYour balance has been set to &e{amount}{symbol}";
    @Path("eco.reset.sender")
    public String ECO_RESET_SENDER = "&6&lECONOMY &8| &fYou reset &e{target}&f's balance to &e{amount}{symbol}";
    @Path("eco.reset.receiver")
    public String ECO_RESET_RECEIVER = "&6&lECONOMY &8| &fYour balance has been reset to &e{amount}{symbol}";
    @Path("eco.reset.all")
    public String ECO_RESET_ALL = "&6&lECONOMY &8| &fYou reset all players' balances to &e{amount}{symbol}";
    @Path("eco.balance.yours")
    public String ECO_BALANCE_YOURS = "&6&lECONOMY &8| &fYou have &e{amount}{symbol}";
    @Path("eco.balance.target")
    public String ECO_BALANCE_TARGET = "&6&lECONOMY &8| &e{target} &fhas &e{amount}{symbol}";

    private LangConfig() {
    }

    public static LangConfig getInstance() {
        if (instance == null) {
            instance = new LangConfig();
        }
        return instance;
    }

}
