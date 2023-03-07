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
import static com.teenkung.craftycrates.CraftyCrates.colorizeList;

public class CommandHandler implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
            if (args.length > 0) {
                if (args[0].equalsIgnoreCase("open")) {
                    if (!sender.hasPermission("craftycrates.open")) {
                        sender.sendMessage(colorize(CraftyCrates.getInstance().getConfig().getString("Languages.commands.no-permission")));
                        return false;
                    }
                    if (sender instanceof Player player) {
                        if (args.length >= 3) {
                            if (ConfigLoader.getBannerIdsList().contains(args[1])) {
                                Bukkit.getScheduler().runTaskAsynchronously(CraftyCrates.getInstance(), () -> JoinEvent.getDataManager().get(player).requestPool(args[1], Integer.valueOf(args[2])).completeOnTimeout(new ArrayList<>(), 10, TimeUnit.SECONDS).thenAccept(result -> Bukkit.getScheduler().runTask(CraftyCrates.getInstance(), () -> {
                                    Functions.giveItem(result, player);
                                    Functions.dispatchCommand(result, player);
                                })));
                            } else {
                                player.sendMessage(colorize(CraftyCrates.getInstance().getConfig().getString("Languages.commands.open.unknown-id")));
                            }
                        } else {
                            player.sendMessage(colorize(CraftyCrates.getInstance().getConfig().getString("Languages.commands.open.invalid-argument")));
                        }
                    }
                } else if (args[0].equalsIgnoreCase("info")) {
                    if (!sender.hasPermission("craftycrates.info")) {
                        sender.sendMessage(colorize(CraftyCrates.getInstance().getConfig().getString("Languages.commands.no-permission")));
                        return false;
                    }
                    if (sender instanceof Player player) {
                        if (args.length >= 2) {
                            if (ConfigLoader.getBannerIdsList().contains(args[1])) {
                                InfoGUI.openTo(player, args[1]);
                            } else {
                                player.sendMessage(colorize(CraftyCrates.getInstance().getConfig().getString("Languages.commands.info.unknown-id")));
                            }
                        } else {
                            player.sendMessage(colorize(CraftyCrates.getInstance().getConfig().getString("Languages.commands.info.invalid-argument")));
                        }
                    }
                } else if (args[0].equalsIgnoreCase("logs")) {
                    if (!sender.hasPermission("craftycrates.logs")) {
                        sender.sendMessage(colorize(CraftyCrates.getInstance().getConfig().getString("Languages.commands.no-permission")));
                        return false;
                    }
                    if (sender instanceof Player player) {
                        if (args.length <= 2) {
                            if (ConfigLoader.getBannerIdsList().contains(args[1])) {
                                LogsGUI gui = JoinEvent.getDataManager().get(player).getLogsGUI();
                                gui.setBannerID(args[1]);

                                gui.openInventory();
                            } else {
                                player.sendMessage(colorize(CraftyCrates.getInstance().getConfig().getString("Languages.commands.logs.unknown-id")));
                            }
                        } else {
                            player.sendMessage(colorize(CraftyCrates.getInstance().getConfig().getString("Languages.commands.logs.invalid-argument")));
                        }
                    }
                } else if (args[0].equalsIgnoreCase("reload")) {
                    if (!sender.hasPermission("craftycrates.reload")) {
                        sender.sendMessage(colorize(CraftyCrates.getInstance().getConfig().getString("Languages.commands.no-permission")));
                        return false;
                    }
                    sender.sendMessage(colorize(CraftyCrates.getInstance().getConfig().getString("Languages.commands.reload.reloading")));
                    long time = System.currentTimeMillis();
                    ConfigLoader.reloadConfig();
                    sender.sendMessage(colorize(CraftyCrates.getInstance().getConfig().getString("Languages.commands.reload.reloaded", "").replaceAll("<time>", String.valueOf(time))));
                } else {
                    if (!sender.hasPermission("craftycrates.help")) {
                        sender.sendMessage(colorize(CraftyCrates.getInstance().getConfig().getString("Languages.commands.no-permission")));
                        return false;
                    }
                    ArrayList<String> help = colorizeList(CraftyCrates.getInstance().getConfig().getStringList("Languages.commands.help"));
                    for (String h : help) {
                        sender.sendMessage(h);
                    }
                }
            } else {
                if (!sender.hasPermission("craftycrates.help")) {
                    sender.sendMessage(colorize(CraftyCrates.getInstance().getConfig().getString("Languages.commands.no-permission")));
                    return false;
                }
                ArrayList<String> help = colorizeList(CraftyCrates.getInstance().getConfig().getStringList("Languages.commands.help"));
                for (String h : help) {
                    sender.sendMessage(h);
                }
            }
        return false;
    }
}
