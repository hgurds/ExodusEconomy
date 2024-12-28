package org.exodusEconomy;

import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import org.exodusEconomy.Commands.Balance.*;
import org.exodusEconomy.Events.PlayerJoinListener;
import org.exodusEconomy.SQL.DatabaseManager;

import java.util.Objects;

public final class ExodusEconomy extends JavaPlugin {
    private DatabaseManager db;

    @Override
    public void onEnable() {
        getServer().getConsoleSender().sendMessage("[" + this.getClass().getSimpleName() + "] " + ChatColor.GREEN + this.getClass().getSimpleName() + " has been enabled !");
        this.db = new DatabaseManager(getDataFolder(), getLogger());
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(db), this);
        Objects.requireNonNull(getCommand("balance")).setExecutor(new BalanceCommand(db));
        Objects.requireNonNull(getCommand("balance")).setTabCompleter(new BalanceCompleter());
    }

    @Override
    public void onDisable() {
        db.closeDatabase(getLogger());
        getServer().getConsoleSender().sendMessage("[" + this.getClass().getSimpleName() + "] " + ChatColor.RED + this.getClass().getSimpleName() + " has been disabled !");
    }

}
