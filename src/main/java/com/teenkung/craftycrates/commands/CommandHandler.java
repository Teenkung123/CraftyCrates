package com.teenkung.craftycrates.commands;

import com.teenkung.craftycrates.ConfigLoader;
import com.teenkung.craftycrates.utils.WeightedRandomSelector;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static com.teenkung.craftycrates.CraftyCrates.colorize;

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
        } else {
            for (int i = 0; i < 10; i++) {
                WeightedRandomSelector<String> selector = new WeightedRandomSelector<>(ConfigLoader.getRarities("TEST"));
                String selectedID = selector.select();
                System.out.println("Randomized: " + selectedID);
            }
        }
        return false;
    }
}
