package dev.kaepsis.cachedeconomy.storage;

import dev.kaepsis.cachedeconomy.Main;

import java.util.Map;

public class BalanceUtils {

    private static final double LARGE_NUMBER_THRESHOLD = 1000.0;
    private static volatile BalanceUtils instance;

    private BalanceUtils() {
    }

    public static BalanceUtils getInstance() {
        if (instance == null) {
            synchronized (BalanceUtils.class) {
                if (instance == null) {
                    instance = new BalanceUtils();
                }
            }
        }
        return instance;
    }

    public String formatBalance(double balance) {
        if (balance < LARGE_NUMBER_THRESHOLD) {
            return formatDecimal(balance);
        }

        Map.Entry<Double, String> suffixEntry = Main.suffixes.floorEntry(balance);
        if (suffixEntry == null) {
            return formatDecimal(balance);
        }

        double divisor = suffixEntry.getKey();
        String suffix = suffixEntry.getValue();
        double shortNumber = balance / divisor;

        return formatDecimal(shortNumber) + suffix;
    }

    private String formatDecimal(double value) {
        return isWholeNumber(value) ?
                String.format("%.0f", value) :
                String.format("%.1f", value);
    }

    private boolean isWholeNumber(double value) {
        return value % 1 == 0;
    }

    public <T> boolean isNotValidAmount(T amount) {
        if (amount == null) {
            return true;
        }

        try {
            double parsedAmount = Double.parseDouble(amount.toString());
            return isInvalidNumber(parsedAmount);
        } catch (NumberFormatException e) {
            return true;
        }
    }

    private boolean isInvalidNumber(double amount) {
        return amount <= 0 ||
                amount > Double.MAX_VALUE ||
                Double.isNaN(amount) ||
                Double.isInfinite(amount);
    }
}