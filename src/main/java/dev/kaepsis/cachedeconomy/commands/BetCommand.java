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

    private final Chat chat = Chat.getInstance();
    private final LangConfig langConfig = LangConfig.getInstance();
    private final CacheStorage cacheStorage = CacheStorage.getInstance();
    private final BalanceUtils balanceUtils = BalanceUtils.getInstance();
    private final String currencySymbol = GeneralConfig.getInstance().currencySymbol;

    @Default
    public void root(Player player) {
        chat.send(player, langConfig.betUsage);
    }

    @Default
    @Syntax("<amount | *>")
    public void execute(Player player, String amount) {
        double playerBalance = cacheStorage.getCachedBalance(player.getName());

        if (amount.equals("*")) {
            handleAllInBet(player, playerBalance);
            return;
        }

        if (!isValidBet(player, amount, playerBalance)) {
            return;
        }

        double parsedAmount = Double.parseDouble(amount);
        handleRegularBet(player, playerBalance, parsedAmount);
    }

    private void handleAllInBet(Player player, double playerBalance) {
        boolean isWin = isWinningBet();
        double newBalance = isWin ? playerBalance * WINNING_MULTIPLIER : 0;

        cacheStorage.setBalance(player.getName(), newBalance);

        chat.send(
                player,
                isWin ? langConfig.betAllWin : langConfig.betAllLost,
                "{amount}", newBalance,
                "{symbol}", currencySymbol
        );
    }

    private boolean isValidBet(Player player, String amount, double playerBalance) {
        if (balanceUtils.isNotValidAmount(amount)) {
            chat.send(player, langConfig.gnInvalidAmount, "{amount}", amount);
            return false;
        }

        double parsedAmount = Double.parseDouble(amount);
        if (playerBalance < parsedAmount) {
            chat.send(player, langConfig.gnInsufficientBalance);
            return false;
        }

        return true;
    }

    private void handleRegularBet(Player player, double playerBalance, double betAmount) {
        boolean isWin = isWinningBet();
        double newBalance = calculateNewBalance(playerBalance, betAmount, isWin);

        cacheStorage.setBalance(player.getName(), newBalance);

        chat.send(
                player,
                isWin ? langConfig.betWin : langConfig.betLost,
                "{amount}", newBalance,
                "{symbol}", currencySymbol
        );
    }

    private boolean isWinningBet() {
        return ThreadLocalRandom.current().nextInt(0, MAX_RANDOM) >= WIN_THRESHOLD;
    }

    private double calculateNewBalance(double currentBalance, double betAmount, boolean isWin) {
        return isWin ?
                currentBalance + (betAmount * WINNING_MULTIPLIER) :
                currentBalance - betAmount;
    }
}