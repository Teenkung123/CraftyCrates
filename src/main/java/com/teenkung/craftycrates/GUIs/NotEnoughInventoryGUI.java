package com.teenkung.craftycrates.GUIs;

import com.teenkung.craftycrates.utils.Functions;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import static com.teenkung.craftycrates.CraftyCrates.colorize;

public class NotEnoughInventoryGUI {

    private final Inventory inv;
    private final Player player;

    public NotEnoughInventoryGUI(Player player) {
        this.player = player;
        inv = Bukkit.createInventory(player, 54, colorize("&4Not Enough Inventory!"));
        for (int i = 0 ; i < 54 ; i++) {
            if (i<= 8 || i>= 45) {
                inv.setItem(i, Functions.getInventoryItem(Material.BLACK_STAINED_GLASS_PANE, 1, "&f", null));
            }
        }
    }



    public void addItem(ItemStack stack) {
        inv.addItem(stack);
    }

    public void open() {
        Inventory newinv = inv;
        for (int i = 9 ; i < 45 ; i++) {
            if (newinv.getItem(i) == null) {
                newinv.setItem(i, Functions.getInventoryItem(Material.WHITE_STAINED_GLASS_PANE, 1, "&f", null));
            }
        }
        player.openInventory(newinv);
    }

}
