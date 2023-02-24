package com.teenkung.craftycrates.commands;

import com.teenkung.craftycrates.ConfigLoader;
import com.teenkung.craftycrates.CraftyCrates;
import com.teenkung.craftycrates.GUIs.LogsGUI;
import com.teenkung.craftycrates.events.JoinEvent;
import com.teenkung.craftycrates.utils.Functions;
import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import static com.teenkung.craftycrates.CraftyCrates.colorize;

public class CommandHandler implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player player) {
            if (args.length == 0 || args[0].equalsIgnoreCase("help")) {

            } else if (args[0].equalsIgnoreCase("open")) {
                if (args.length <= 3) {
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
            } else if (args[0].equalsIgnoreCase("info")) {

            } else if (args[0].equalsIgnoreCase("logs")) {
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
            } else if (args[0].equalsIgnoreCase("reload")) {

            } else {
                player.sendMessage(colorize("&cUnknown Argument!"));
            }
        } else {
            ItemStack stack = new ItemStack(Material.DIAMOND_AXE, 2);
            ReadWriteNBT nbt = NBT.itemStackToNBT(stack);
            System.out.println(nbt);

        }
        return false;
    }
}
