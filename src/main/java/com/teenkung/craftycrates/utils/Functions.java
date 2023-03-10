package com.teenkung.craftycrates.utils;

import com.google.inject.internal.Nullable;
import com.teenkung.craftycrates.GUIs.NotEnoughInventoryGUI;
import com.teenkung.craftycrates.utils.record.ItemPair;
import de.tr7zw.nbtapi.NBTItem;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

import static com.teenkung.craftycrates.CraftyCrates.colorize;

public class Functions {

    public static void giveItem(ArrayList<ItemPair> ret, Player player) {
        ArrayList<ItemStack> exceeded = new ArrayList<>();
        for (ItemPair pair : ret) {
            if (player.getInventory().firstEmpty() == -1) {
                exceeded.add(pair.stack());
            } else {
                player.getInventory().addItem(pair.stack());
            }
        }
        if (exceeded.size() > 0) {
            NotEnoughInventoryGUI gui = new NotEnoughInventoryGUI(player);
            for (ItemStack stack : exceeded) {
                gui.addItem(stack);
            }
            gui.open();
        }
    }

    public static ItemStack getInventoryItem(Material material, int amount, @Nullable String name, @Nullable ArrayList<String> lore) {
        ItemStack stack = new ItemStack(material, amount);
        ItemMeta meta = stack.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(colorize(name));
            meta.setLore(lore);
            stack.setItemMeta(meta);
        }
        NBTItem nbt = new NBTItem(stack);
        nbt.setBoolean("Background", true);
        nbt.applyNBT(stack);
        return stack;

    }
    public static void dispatchCommand(ArrayList<ItemPair> ret, Player player) {
        for (ItemPair pair : ret) {
            ItemMeta meta = pair.stack().getItemMeta();
            boolean useMeta = meta != null;
            for (String cmd : pair.command()) {
                if (cmd.startsWith("[op]") || cmd.startsWith("[OP]") || cmd.startsWith("[op] ") || cmd.startsWith("[OP] ")) {
                    boolean isOp = player.isOp();
                    player.setOp(true);
                    if (useMeta) {
                        Bukkit.dispatchCommand(player, PlaceholderAPI.setPlaceholders(player, cmd
                                .replaceAll("<player>", player.getName())
                                .replaceAll("<item>", meta.getDisplayName())
                        ));
                    } else {
                        Bukkit.dispatchCommand(player, PlaceholderAPI.setPlaceholders(player, cmd
                                .replaceAll("<player>", player.getName())
                        ));
                    }
                    player.setOp(isOp);
                } else if (cmd.startsWith("[Player] ") || cmd.startsWith("[player] ") || cmd.startsWith("[player]") || cmd.startsWith("[Player]")) {
                    if (useMeta) {
                        Bukkit.dispatchCommand(player, PlaceholderAPI.setPlaceholders(player, cmd
                                .replaceAll("<player>", player.getName())
                                .replaceAll("<item>", meta.getDisplayName())
                        ));
                    } else {
                        Bukkit.dispatchCommand(player, PlaceholderAPI.setPlaceholders(player, cmd
                                .replaceAll("<player>", player.getName())
                        ));
                    }
                } else {
                    if (useMeta) {
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), PlaceholderAPI.setPlaceholders(player, cmd
                                .replaceAll("<player>", player.getName())
                                .replaceAll("<item>", meta.getDisplayName())
                        ));
                    } else {
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), PlaceholderAPI.setPlaceholders(player, cmd
                                .replaceAll("<player>", player.getName())
                        ));
                    }
                }
            }
        }
    }
}
