package dev.kaepsis.cachedeconomy.hooks;

import dev.kaepsis.cachedeconomy.storage.impl.CacheStorage;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String params) {
        if (params.equals("balance")) {
            double balance = CacheStorage.getInstance().getBalance(player.getName());
            return String.valueOf(balance);
        }
        return null;
    }

}
