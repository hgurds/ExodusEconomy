package org.exodusEconomy.Commands.Balance;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class BalanceCompleter implements TabCompleter {

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        List<String> suggestions = new ArrayList<>();

        if (args.length == 1) {
            suggestions.add("get");
            suggestions.add("set");
        } else if (args.length == 2) {
            if ("get".equalsIgnoreCase(args[0])) {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    suggestions.add(player.getName());
                }
            }
            else if ("set".equalsIgnoreCase(args[0])) {
                suggestions.add("public");
                suggestions.add("private");
            }
        }

        return suggestions;
    }
}
