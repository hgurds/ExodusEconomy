package org.exodusEconomy.Commands.Balance;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.exodusEconomy.SQL.DatabaseManager;
import org.jetbrains.annotations.NotNull;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BalanceCommand implements CommandExecutor {

    private DatabaseManager db;

    public BalanceCommand(DatabaseManager db) {
        this.db = db;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, @NotNull String s, String[] strings) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Only players can execute this command.");
            return true;
        }
        if (strings.length > 0) {
            if (strings.length <= 2) {
                String arg = null;
                if (strings.length == 2) arg = strings[1];
                switch (strings[0]) {
                    case "get": getBalance((Player) sender, arg); break;
                    case "set": setPrivacy((Player) sender, arg); break;
                    default : sender.sendMessage("§c[Bank] Incorrect usage. Use " + command.getUsage() + "."); break;
                }
            } else sender.sendMessage("§c[Bank] Incorrect usage. Use " + command.getUsage() + ".");
        } else getBalance((Player) sender, ((Player) sender).getName());

        return true;
    }

    private void getBalance(Player sender, String playerName) {
        if(playerName == null) {
            sender.sendMessage("§c[Bank] Incorrect usage. Use /balance get <player>.");
            return;
        }
        Player player = Bukkit.getPlayer(playerName);
        if(player == null) {
            sender.sendMessage("§c[Bank] Player \"" + playerName + "\" doesn't exist.");
            return;
        }
        String uuid = player.getUniqueId().toString();
        boolean isPublic = true;
        double balance = 0.0;
        String query;
        if(sender != player && !sender.isOp()) {
            query = "SELECT public FROM player_economy WHERE uuid = ?;";
            try (PreparedStatement statement = db.getConnection().prepareStatement(query)) {
                statement.setString(1, uuid);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        isPublic = resultSet.getBoolean("public");
                    }
                }
            } catch (SQLException e) {
                Bukkit.getLogger().severe("Error retrieving public for UUID : " + uuid + ": " + e.getMessage());
            }
        }
        if(!isPublic) {
            sender.sendMessage("§c[Bank] \"" + playerName + "\"'s balance is set to private." );
            return;
        }
        query = "SELECT balance FROM player_economy WHERE uuid = ?;";
        try (PreparedStatement statement = db.getConnection().prepareStatement(query)) {
            statement.setString(1, uuid);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    balance = resultSet.getDouble("balance");
                }
            }
        } catch (SQLException e) {
            Bukkit.getLogger().severe("Error retrieving funds for UUID : " + uuid + ": " + e.getMessage());
        }
        if(sender == player) sender.sendMessage("[Bank] You have : §e" + balance + "$§f.");
        else sender.sendMessage("[Bank] \"" + player.getName() + "\" has : §e" + balance + "$§f.");
    }

    public void setPrivacy(Player sender, String privacy) {
        if(privacy == null || (!privacy.equals("public") && !privacy.equals("private"))) {
            sender.sendMessage("§c[Bank] Incorrect usage. Use /balance set <public|private>.");
            return;
        }
        boolean isPublic = privacy.equalsIgnoreCase("public");

        String query = "UPDATE player_economy SET public = ? WHERE uuid = ?;";
        try (PreparedStatement statement = db.getConnection().prepareStatement(query)) {
            statement.setBoolean(1, isPublic);
            statement.setString(2, sender.getUniqueId().toString());
            int rowsAffected = statement.executeUpdate();
            sender.sendMessage("§a[Bank] Your balance's privacy setting has been updated to : §b" + privacy + "§a.");
        } catch (SQLException e) {
            Bukkit.getLogger().severe("Error updating privacy setting for UUID " + sender.getUniqueId() + ": " + e.getMessage());
            sender.sendMessage("§c[Bank] An error occurred while updating your privacy setting.");
        }
    }

}
