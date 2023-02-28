package com.teenkung.craftycrates.events;

import com.teenkung.craftycrates.GUIs.InfoGUI;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class InfoEvent implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        Inventory clickedInventory = event.getClickedInventory();
        if (clickedInventory == null) {
            return;
        }
        ItemStack check = clickedInventory.getItem(0);
        if (check == null) {
            return;
        }
        NBTItem nbt = new NBTItem(check);
        if (nbt.getString("Inv").equals("GachaInfo")) {
            event.setCancelled(true);
            if (event.getSlot() == 45) {
                InfoGUI.goPreviousPage((Player) event.getWhoClicked(), nbt.getString("bannerID"));
            } else if (event.getSlot() == 53) {
                InfoGUI.goNextPage((Player) event.getWhoClicked(), nbt.getString("bannerID"));
            }
        }
    }

}
