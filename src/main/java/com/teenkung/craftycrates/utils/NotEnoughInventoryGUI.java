package com.teenkung.craftycrates.utils;

import com.google.inject.internal.Nullable;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class NotEnoughInventoryGUI {

    private final Inventory inv;
    private final Player player;

    public NotEnoughInventoryGUI(Player player) {
        this.player = player;
        inv = Bukkit.createInventory(player, 54, "Not Enough Inventory!");
        for (int i = 0 ; i < 54 ; i++) {
            if (i<= 8 || i>= 45) {
                inv.setItem(i, getInventoryItem(Material.BLACK_STAINED_GLASS_PANE, 1, "", null));
            } else {
                inv.setItem(i, getInventoryItem(Material.WHITE_STAINED_GLASS_PANE, 1, "", null));
            }
        }
    }

    private ItemStack getInventoryItem(Material material, int amount, @Nullable String name, @Nullable ArrayList<String> lore) {
        ItemStack stack = new ItemStack(material, amount);
        ItemMeta meta = stack.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            meta.setLore(lore);
            stack.setItemMeta(meta);
        }
        NBTItem nbt = new NBTItem(stack);
        nbt.setBoolean("Background", true);
        nbt.applyNBT(stack);
        return stack;

    }

    public void addItem(ItemStack stack) {
        for (int i = 9 ; i < 45 ; i++) {
            if (inv.getItem(i) == getInventoryItem(Material.WHITE_STAINED_GLASS_PANE, 1, "", null)) {
                inv.setItem(i, stack);
                break;
            }
        }
    }

    public void open() {
        player.openInventory(inv);
    }

}
