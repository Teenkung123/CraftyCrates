package com.teenkung.craftycrates.events;

import com.teenkung.craftycrates.GUIs.InfoGUI;
import com.teenkung.craftycrates.utils.GUILoader;
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
        ItemStack check = clickedInventory.getItem(GUILoader.getInfoNextPageSlot().get(0));
        if (check == null) {
            return;
        }
        NBTItem nbt = new NBTItem(check);
        if (nbt.getString("Inv").equals("GachaInfo")) {
            event.setCancelled(true);
            if (GUILoader.getInfoPreviousPageSlot().contains(event.getSlot())) {
                InfoGUI.goPreviousPage((Player) event.getWhoClicked(), nbt.getString("bannerID"));
            } else if (GUILoader.getInfoNextPageSlot().contains(event.getSlot())) {
                InfoGUI.goNextPage((Player) event.getWhoClicked(), nbt.getString("bannerID"));
            }
        }
    }

}
