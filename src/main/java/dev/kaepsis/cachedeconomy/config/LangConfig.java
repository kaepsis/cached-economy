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
    public String gnInsufficientBalance = "&6&lECONOMY &8| &cYou don't have enough money";
    @Path("general.invalid-amount")
    public String gnInvalidAmount = "&6&lECONOMY &8| &cYou can't operate with that amount ({amount})";
    @Path("general.config-reloaded")
    public String gnConfigReloaded = "&6&lECONOMY &8| &fConfig has been reloaded &esuccessfully";
    @Path("pay.cant-send-to-yourself")
    public String payCannotSendToYourself = "&6&lECONOMY &8| &cYou can't send money to yourself";
    @Path("pay.money-sent")
    public String payMoneySent = "&6&lECONOMY &8| &fYou sent &e{amount}{symbol} to {target}";
    @Path("pay.money-received")
    public String payMoneyReceived = "&6&lECONOMY &8| &fYou received &e{amount}{symbol} &ffrom &e{sender}";
    @Path("database-error.player-not-found")
    public String dbPlayerNotFound = "&6&lECONOMY &8| &c{target} was not found as a player in the database";
    @Path("general.player-not-found")
    public String gnPlayerNotFound = "&6&lECONOMY &8| &c{target} is not online or their name is wrong";
    @Path("eco.give.sender")
    public String ecoGiveSender = "&6&lECONOMY &8| &fYou gave &e{amount}{symbol} &fto &e{target}";
    @Path("eco.give.receiver")
    public String ecoGiveReceiver = "&6&lECONOMY &8| &fYou received &e{amount}{symbol}";
    @Path("eco.set.sender")
    public String ecoSetSender = "&6&lECONOMY &8| &fYou set &e{target}&f's balance to &e{amount}{symbol}";
    @Path("eco.set.receiver")
    public String ecoSetReceiver = "&6&lECONOMY &8| &fYour balance has been set to &e{amount}{symbol}";
    @Path("eco.reset.sender")
    public String ecoResetSender = "&6&lECONOMY &8| &fYou reset &e{target}&f's balance to &e{amount}{symbol}";
    @Path("eco.reset.receiver")
    public String ecoResetReceiver = "&6&lECONOMY &8| &fYour balance has been reset to &e{amount}{symbol}";
    @Path("eco.reset.all")
    public String ecoResetAll = "&6&lECONOMY &8| &fYou reset all players' balances to &e{amount}{symbol}";
    @Path("eco.balance.yours")
    public String ecoBalanceYours = "&6&lECONOMY &8| &fYou have &e{amount}{symbol}";
    @Path("eco.balance.target")
    public String ecoBalanceTarget = "&6&lECONOMY &8| &e{target} &fhas &e{amount}{symbol}";
    @Path("eco.baltop.top")
    public String ecoBaltopTop = "&e--== &6&lBALTOP &e==--";
    @Path("eco.baltop.entry")
    public String ecoBaltopEntry = "&6{ranking}. &f{playerName}&7: &e{amount}{symbol}";
    @Path("bet.usage")
    public String betUsage = "&6&lECONOMY &8| &cCorrect usage /bet <amount | *>";
    @Path("bet.all.win")
    public String betAllWin = "&6&lECONOMY &8| &fYou won the bet! Your balance has been updated to &e{amount}{symbol}";
    @Path("bet.all.lost")
    public String betAllLost = "&6&lECONOMY &8| &fYou lost the bet! Your balance has been updated to &e{amount}{symbol}";
    @Path("bet.win")
    public String betWin = "&6&lECONOMY &8| &fYou won the bet! Your balance has been updated to &e{amount}{symbol}";
    @Path("bet.lost")
    public String betLost = "&6&lECONOMY &8| &fYou lost the bet! Your balance has been updated to &e{amount}{symbol}";

    private LangConfig() {
    }

    public static LangConfig getInstance() {
        if (instance == null) {
            instance = new LangConfig();
        }
        return instance;
    }

}
