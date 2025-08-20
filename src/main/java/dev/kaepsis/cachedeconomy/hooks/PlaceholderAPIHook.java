package dev.kaepsis.cachedeconomy.hooks;

import dev.kaepsis.cachedeconomy.storage.BalanceUtils;
import dev.kaepsis.cachedeconomy.storage.impl.CacheStorage;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.regex.Pattern;

public class PlaceholderAPIHook extends PlaceholderExpansion {

    private static final Pattern BALTOP_PATTERN = Pattern.compile("baltop_(\\d+)_(name|value)");
    private static final Pattern BALTOP_FORMATTED_PATTERN = Pattern.compile("baltop_(\\d+)_value_formatted");

    @Override
    public @NotNull String getIdentifier() {
        return "cachedeconomy";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Kaepsis";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0";
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String identifier) {
        if (player == null) return null;

        switch (identifier) {
            case "balance":
                return String.valueOf(CacheStorage.getInstance().getCachedBalance(player.getName()));
            case "balance_formatted":
                return CacheStorage.getInstance().getBalanceFormatted(player.getName());
        }

        if (BALTOP_PATTERN.matcher(identifier).matches()) {
            return handleBaltopRequest(identifier, false);
        }

        if (BALTOP_FORMATTED_PATTERN.matcher(identifier).matches()) {
            return handleBaltopRequest(identifier, true);
        }

        return null;
    }

    private String handleBaltopRequest(String identifier, boolean formatted) {
        String[] parts = identifier.split("_");
        try {
            int index = Integer.parseInt(parts[1]) - 1;
            boolean isName = parts[2].equalsIgnoreCase("name");

            Map.Entry<String, Double> entry = CacheStorage.getInstance().getTopTenAt(index);
            if (entry == null) {
                return isName ? "N/A" : "0";
            }

            if (isName) {
                return entry.getKey();
            }

            double value = entry.getValue();
            return formatted ?
                    BalanceUtils.getInstance().formatBalance(value) :
                    String.valueOf(value);
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            return "N/A";
        }
    }
}