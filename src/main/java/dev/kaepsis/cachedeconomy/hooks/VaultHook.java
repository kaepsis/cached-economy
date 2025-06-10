package dev.kaepsis.cachedeconomy.hooks;

import dev.kaepsis.cachedeconomy.Main;
import dev.kaepsis.cachedeconomy.storage.impl.PlayerStorage;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicePriority;

import java.util.List;

public class VaultHook implements Economy {

    private static Economy economy;

    public VaultHook() {
        if (Bukkit.getPluginManager().getPlugin("Vault") != null) {
            setupEconomy();
        }
    }

    public static boolean hasEconomy() {
        return economy != null;
    }

    private void setupEconomy() {
        Bukkit.getServicesManager().register(Economy.class, this, Main.instance, ServicePriority.Normal);
        RegisteredServiceProvider<Economy> rsp = Bukkit.getServicesManager().getRegistration(Economy.class);
        if (rsp != null)
            economy = rsp.getProvider();
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getName() {
        return "CachedEconomy";
    }

    @Override
    public boolean hasBankSupport() {
        return false;
    }

    @Override
    public int fractionalDigits() {
        return -1;
    }

    @Override
    public String format(double v) {
        return "";
    }

    @Override
    public String currencyNamePlural() {
        return "";
    }

    @Override
    public String currencyNameSingular() {
        return "";
    }

    @Override
    public boolean hasAccount(OfflinePlayer offlinePlayer) {
        return false;
    }

    @Override
    public boolean hasAccount(OfflinePlayer offlinePlayer, String s) {
        return false;
    }

    @Override
    public double getBalance(OfflinePlayer offlinePlayer) {
        return PlayerStorage.getInstance().getBalance(offlinePlayer.getName());
    }

    @Override
    public double getBalance(OfflinePlayer offlinePlayer, String s) {
        return getBalance(offlinePlayer);
    }

    @Override
    public boolean has(OfflinePlayer offlinePlayer, double amount) {
        return (getBalance(offlinePlayer) >= amount);
    }

    @Override
    public boolean has(OfflinePlayer offlinePlayer, String s, double amount) {
        return (getBalance(offlinePlayer) >= amount);
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer offlinePlayer, double amount) {
        double currentAmount = getBalance(offlinePlayer);
        PlayerStorage.getInstance().setBalance(offlinePlayer.getName(), currentAmount - amount);
        return new EconomyResponse(amount, 0, EconomyResponse.ResponseType.SUCCESS, "Success");
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer offlinePlayer, String s, double amount) {
        return withdrawPlayer(offlinePlayer, amount);
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer offlinePlayer, double amount) {
        double currentAmount = getBalance(offlinePlayer);
        PlayerStorage.getInstance().setBalance(offlinePlayer.getName(), currentAmount + amount);
        return new EconomyResponse(amount, 0, EconomyResponse.ResponseType.SUCCESS, "Success");
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer offlinePlayer, String s, double amount) {
        return depositPlayer(offlinePlayer, amount);
    }

    @Override
    public EconomyResponse createBank(String s, OfflinePlayer offlinePlayer) {
        return null;
    }

    @Override
    public EconomyResponse deleteBank(String s) {
        return null;
    }

    @Override
    public EconomyResponse bankBalance(String s) {
        return null;
    }

    @Override
    public EconomyResponse bankHas(String s, double v) {
        return null;
    }

    @Override
    public EconomyResponse bankWithdraw(String s, double v) {
        return null;
    }

    @Override
    public EconomyResponse bankDeposit(String s, double v) {
        return null;
    }

    @Override
    public EconomyResponse isBankOwner(String s, OfflinePlayer offlinePlayer) {
        return null;
    }

    @Override
    public EconomyResponse isBankMember(String s, OfflinePlayer offlinePlayer) {
        return null;
    }

    @Override
    public List<String> getBanks() {
        return List.of();
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer offlinePlayer) {
        return false;
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer offlinePlayer, String s) {
        return false;
    }

}
