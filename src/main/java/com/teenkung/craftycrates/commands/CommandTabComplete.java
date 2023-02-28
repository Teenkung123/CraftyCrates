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
            ret.add("help");
            ret.add("reload");
            ret.add("open");
            ret.add("info");
            ret.add("logs");
        } else if (args.length == 2) {
            if (args[0].equals("open") || args[0].equals("info") || args[0].equals("logs")) {
                ret.addAll(ConfigLoader.getBannerIdsList());
            }
        } else if (args.length == 3) {
            if (args[0].equals("open")) {
                for (int i = 1 ; i < 21 ; i++) {
                    ret.add(String.valueOf(i));
                }
            }
        }

        return ret;
    }
}
