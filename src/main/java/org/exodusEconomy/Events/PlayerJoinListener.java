package org.exodusEconomy.Events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.exodusEconomy.SQL.DatabaseManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PlayerJoinListener implements Listener {

    private final DatabaseManager db;

    public PlayerJoinListener(DatabaseManager db) {
        this.db = db;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        String uuid = event.getPlayer().getUniqueId().toString();

        try (Connection connection = db.getConnection()) {
            String query = "INSERT INTO player_economy (uuid, balance) VALUES (?, 0.0) ON CONFLICT(uuid) DO NOTHING;";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, uuid);
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            event.getPlayer().sendMessage("§4An error occurred while setting up your balance.");
        }
    }
}
