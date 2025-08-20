package dev.kaepsis.cachedeconomy.storage;

import dev.kaepsis.cachedeconomy.Main;

import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

public class BalanceUtils {
    private static final double LARGE_NUMBER_THRESHOLD = 1000.0;
    private static volatile BalanceUtils instance;
    private final NavigableMap<Double, String> suffixes;

    private final Map<Double, String> formattedBalanceCache;
    private final Map<String, Boolean> amountValidationCache;

    private BalanceUtils() {
        this.suffixes = new TreeMap<>(Main.suffixes);
        this.formattedBalanceCache = new ConcurrentHashMap<>(1000);
        this.amountValidationCache = new ConcurrentHashMap<>(100);
    }

    public static BalanceUtils getInstance() {
        BalanceUtils result = instance;
        if (result == null) {
            synchronized (BalanceUtils.class) {
                result = instance;
                if (result == null) {
                    instance = result = new BalanceUtils();
                }
            }
        }
        return result;
    }

    public String formatBalance(double balance) {
        return formattedBalanceCache.computeIfAbsent(balance, this::calculateFormattedBalance);
    }

    private String calculateFormattedBalance(double balance) {
        if (balance < LARGE_NUMBER_THRESHOLD) {
            return formatDecimal(balance);
        }

        Map.Entry<Double, String> suffixEntry = suffixes.floorEntry(balance);
        if (suffixEntry == null) {
            return formatDecimal(balance);
        }

        double divisor = suffixEntry.getKey();
        String suffix = suffixEntry.getValue();
        return formatDecimal(balance / divisor) + suffix;
    }

    private String formatDecimal(double value) {
        return Math.abs(value % 1) < Double.MIN_VALUE ?
                String.format("%.0f", value) :
                String.format("%.1f", value);
    }

    public boolean isNotValidAmount(String amount) {
        if (amount == null || amount.isEmpty()) {
            return true;
        }

        return amountValidationCache.computeIfAbsent(amount, key -> {
            try {
                double parsedAmount = Double.parseDouble(key);
                return isInvalidNumber(parsedAmount);
            } catch (NumberFormatException e) {
                return true;
            }
        });
    }

    private boolean isInvalidNumber(double amount) {
        return amount <= 0
                || amount > Double.MAX_VALUE
                || Double.isNaN(amount)
                || Double.isInfinite(amount);
    }
}