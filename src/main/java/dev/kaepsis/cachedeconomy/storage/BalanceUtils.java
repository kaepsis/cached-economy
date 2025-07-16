package dev.kaepsis.cachedeconomy.storage;

import dev.kaepsis.cachedeconomy.Main;

import java.util.Map;

public class BalanceUtils {

    private static BalanceUtils instance = null;

    private BalanceUtils() {
    }

    public static BalanceUtils getInstance() {
        if (instance == null) {
            instance = new BalanceUtils();
        }
        return instance;
    }

    public String formatBalance(double balance) {
        if (balance < 1000) {
            return formatDecimal(balance);
        }
        Map.Entry<Double, String> entry = Main.suffixes.floorEntry(balance);
        if (entry == null) return formatDecimal(balance);
        double divisor = entry.getKey();
        String suffix = entry.getValue();
        double shortNumber = balance / divisor;
        return formatDecimal(shortNumber) + suffix;
    }

    private String formatDecimal(double value) {
        return (value % 1 == 0) ? String.format("%.0f", value) : String.format("%.1f", value);
    }

}
