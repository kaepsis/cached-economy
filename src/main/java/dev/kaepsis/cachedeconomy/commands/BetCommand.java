package dev.kaepsis.cachedeconomy.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Syntax;
import com.github.kaepsis.Chat;
import dev.kaepsis.cachedeconomy.config.GeneralConfig;
import dev.kaepsis.cachedeconomy.config.LangConfig;
import dev.kaepsis.cachedeconomy.storage.BalanceUtils;
import dev.kaepsis.cachedeconomy.storage.impl.CacheStorage;
import org.bukkit.entity.Player;

import java.util.concurrent.ThreadLocalRandom;

@CommandAlias("bet")
public class BetCommand extends BaseCommand {
    private static final int WIN_THRESHOLD = 51;
    private static final int MAX_RANDOM = 101;
    private static final double WINNING_MULTIPLIER = 2.0;

    private final Chat chat;
    private final LangConfig langConfig;
    private final CacheStorage cacheStorage;
    private final BalanceUtils balanceUtils;
    private final String currencySymbol;
    private final ThreadLocalRandom random;

    public BetCommand() {
        this.chat = Chat.getInstance();
        this.langConfig = LangConfig.getInstance();
        this.cacheStorage = CacheStorage.getInstance();
        this.balanceUtils = BalanceUtils.getInstance();
        this.currencySymbol = GeneralConfig.getInstance().currencySymbol;
        this.random = ThreadLocalRandom.current();
    }

    @Default
    public void root(Player player) {
        chat.send(player, langConfig.betUsage);
    }

    @Default
    @Syntax("<amount | *>")
    public void execute(Player player, String amount) {
        double playerBalance = cacheStorage.getCachedBalance(player.getName());

        if (amount.equals("*")) {
            handleBet(player, playerBalance, playerBalance);
            return;
        }

        if (!isValidBet(player, amount, playerBalance)) {
            return;
        }

        handleBet(player, playerBalance, Double.parseDouble(amount));
    }

    private boolean isValidBet(Player player, String amount, double playerBalance) {
        if (balanceUtils.isNotValidAmount(amount)) {
            chat.send(player, langConfig.gnInvalidAmount,
                    "{amount}", amount,
                    "{symbol}", currencySymbol
            );
            return false;
        }

        double betAmount = Double.parseDouble(amount);
        if (playerBalance < betAmount) {
            chat.send(player, langConfig.gnInsufficientBalance);
            return false;
        }

        return true;
    }

    private void handleBet(Player player, double currentBalance, double betAmount) {
        boolean isWin = random.nextInt(MAX_RANDOM) >= WIN_THRESHOLD;
        double newBalance = isWin ?
                currentBalance + (betAmount * WINNING_MULTIPLIER) :
                currentBalance - betAmount;

        cacheStorage.setBalance(player.getName(), newBalance);

        chat.send(
                player,
                isWin ?
                        (betAmount == currentBalance ? langConfig.betAllWin : langConfig.betWin) :
                        (betAmount == currentBalance ? langConfig.betAllLost : langConfig.betLost),
                "{amount}", newBalance,
                "{symbol}", currencySymbol
        );
    }
}