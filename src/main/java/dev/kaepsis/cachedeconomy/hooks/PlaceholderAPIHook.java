package dev.kaepsis.cachedeconomy.hooks;

import dev.kaepsis.cachedeconomy.storage.impl.CacheStorage;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class PlaceholderAPIHook extends PlaceholderExpansion {

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
        if (identifier.equals("balance")) {
            double balance = CacheStorage.getInstance().getCachedBalance(player.getName());
            return String.valueOf(balance);
        }
        if (identifier.equalsIgnoreCase("balance_formatted")) {
            return CacheStorage.getInstance().getBalanceFormatted(player.getName());
        }
        if (identifier.matches("baltop_\\d+_(name|value)")) {
            String[] parts = identifier.split("_");
            try {
                int index = Integer.parseInt(parts[1]) - 1;
                boolean isName = parts[2].equalsIgnoreCase("name");
                Map.Entry<String, Double> entry = CacheStorage.getInstance().getTopTenAt(index);
                if (entry == null) return isName ? "N/A" : "0";
                return isName ? entry.getKey() : String.valueOf(entry.getValue());
            } catch (NumberFormatException e) {
                return "N/A";
            }
        }
        return null;
    }

}
