package com.teenkung.craftycrates.commands;

import com.teenkung.craftycrates.ConfigLoader;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class CommandTabComplete implements TabCompleter {


    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        ArrayList<String> ret = new ArrayList<>();
        if (args.length == 1) {
            if (sender.hasPermission("craftycrates.help")) {
                ret.add("help");
            } else if (sender.hasPermission("craftycrates.reload")) {
                ret.add("reload");
            } else if (sender.hasPermission("craftycrates.open")) {
                ret.add("open");
            } else if (sender.hasPermission("craftycrates.info")) {
                ret.add("info");
            } else if (sender.hasPermission("craftycrates.logs")) {
                ret.add("logs");
            }
        } else if (args.length == 2) {
            if (sender.hasPermission("craftycrates.open") || sender.hasPermission("craftycrates.info") || sender.hasPermission("craftycrates.logs")) {
                if (args[0].equals("open") || args[0].equals("info") || args[0].equals("logs")) {
                    ret.addAll(ConfigLoader.getBannerIdsList());
                }
            }
        } else if (args.length == 3) {
            if (args[0].equals("open") && sender.hasPermission("craftycrates.open")) {
                for (int i = 1 ; i < 21 ; i++) {
                    ret.add(String.valueOf(i));
                }
            }
        }

        return ret;
    }
}
