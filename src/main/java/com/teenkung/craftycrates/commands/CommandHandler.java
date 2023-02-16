package com.teenkung.craftycrates.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class CommandHandler implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player player) {
            if (args.length == 0 || args[0].equalsIgnoreCase("help")) {

            } else if (args[0].equalsIgnoreCase("open")) {

            } else if (args[0].equalsIgnoreCase("open-10")) {

            } else if (args[0].equalsIgnoreCase("info")) {

            } else if (args[0].equalsIgnoreCase("log")) {
                
            }
        }
        return false;
    }
}
