package com.teenkung.craftycrates.events;

import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import static com.teenkung.craftycrates.CraftyCrates.colorize;

public class NEIEvent implements Listener {

    @EventHandler
    public void onInteract(InventoryClickEvent event) {
        if (event.getView().getTitle().equals(colorize("&4Not Enough Inventory!"))) {
            ItemStack stack = event.getCurrentItem();
            if (stack != null) {
                NBTItem nbt = new NBTItem(stack);
                if (nbt.getBoolean("Background")) {
                    event.setCancelled(true);
                }
            }
        }
    }
}
