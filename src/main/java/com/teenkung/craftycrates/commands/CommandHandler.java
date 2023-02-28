package com.teenkung.craftycrates.commands;

import com.teenkung.craftycrates.ConfigLoader;
import com.teenkung.craftycrates.CraftyCrates;
import com.teenkung.craftycrates.GUIs.InfoGUI;
import com.teenkung.craftycrates.GUIs.LogsGUI;
import com.teenkung.craftycrates.events.JoinEvent;
import com.teenkung.craftycrates.utils.Functions;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import static com.teenkung.craftycrates.CraftyCrates.colorize;

public class CommandHandler implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
            if (args.length > 0) {
                if (args[0].equalsIgnoreCase("open")) {
                    if (sender instanceof Player player) {
                        if (args.length >= 3) {
                            if (ConfigLoader.getBannerIdsList().contains(args[1])) {
                                Bukkit.getScheduler().runTaskAsynchronously(CraftyCrates.getInstance(), () -> JoinEvent.getDataManager().get(player).requestPool(args[1], Integer.valueOf(args[2])).completeOnTimeout(new ArrayList<>(), 10, TimeUnit.SECONDS).thenAccept(result -> Bukkit.getScheduler().runTask(CraftyCrates.getInstance(), () -> {
                                    Functions.giveItem(result, player);
                                    Functions.dispatchCommand(result, player);
                                })));
                            } else {
                                player.sendMessage(colorize("&cUnknown Banner ID, please check your arguments (case sensitive)"));
                            }
                        } else {
                            player.sendMessage(colorize("&cInvalid argument! /craftycrates open <bannerID> <amount>"));
                        }
                    }
                } else if (args[0].equalsIgnoreCase("info")) {
                    if (sender instanceof Player player) {
                        if (args.length >= 2) {
                            if (ConfigLoader.getBannerIdsList().contains(args[1])) {
                                InfoGUI.openTo(player, args[1]);
                            } else {
                                player.sendMessage(colorize("&cUnknown Banner ID, please check your arguments (case sensitive)"));
                            }
                        } else {
                            player.sendMessage(colorize("&cInvalid argument! /craftycrates info <bannerID>"));
                        }
                    }
                } else if (args[0].equalsIgnoreCase("logs")) {
                    if (sender instanceof Player player) {
                        if (args.length <= 2) {
                            if (ConfigLoader.getBannerIdsList().contains(args[1])) {
                                LogsGUI gui = JoinEvent.getDataManager().get(player).getLogsGUI();
                                gui.setBannerID(args[1]);

                                gui.openInventory();
                            } else {
                                player.sendMessage(colorize("&cUnknown Banner ID, please check your arguments (case sensitive)"));
                            }
                        } else {
                            player.sendMessage(colorize("&cInvalid argument! /craftycrates logs <bannerID>"));
                        }
                    }
                } else if (args[0].equalsIgnoreCase("reload")) {
                    sender.sendMessage(colorize("&6Reloading Configuration"));
                    long time = System.currentTimeMillis();
                    ConfigLoader.reloadConfig();
                    sender.sendMessage(colorize("&aConfiguration reloaded in " + (System.currentTimeMillis() - time) + "ms"));
                } else {
                    String help = colorize("""
                        &6CraftyCrates Help command:\s
                           &6/cc open <BannerID> <amount> &f- Open the Gacha with specific amount
                           &6/cc info <BannerID> &f- Get info of specific Banner
                           &6/cc logs <BannerID> &f- Get Your logs of specific Banner
                           &6/cc reload &f- Reload the configuration
                           &6/cc help &f- Open this menu""");
                    sender.sendMessage(help);
                }
            } else {
                String help = colorize("""
                        &6CraftyCrates Help command:\s
                           &6/cc open <BannerID> <amount> &f- Open the Gacha with specific amount
                           &6/cc info <BannerID> &f- Get info of specific Banner
                           &6/cc logs <BannerID> &f- Get Your logs of specific Banner
                           &6/cc reload &f- Reload the configuration
                           &6/cc help &f- Open this menu""");
                sender.sendMessage(help);
            }
        return false;
    }
}
