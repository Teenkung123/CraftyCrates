package com.teenkung.craftycrates.events;

import com.teenkung.craftycrates.CraftyCrates;
import com.teenkung.craftycrates.utils.GUILoader;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.UUID;

public class LogsEvent implements Listener {

    private final ArrayList<UUID> cooldowns = new ArrayList<>();
    @EventHandler
    public void onInteract(InventoryClickEvent event) {
        Inventory clickedInventory = event.getClickedInventory();
        if (clickedInventory == null) {
            return;
        }
        ItemStack check = clickedInventory.getItem(GUILoader.getLogsNextPageSlot().get(0));
        if (check == null) {
            return;
        }
        NBTItem nbt = new NBTItem(check);
        if (nbt.getString("Inv").equals("LogsInfo")) {
            Player player = (Player) event.getWhoClicked();
            event.setCancelled(true);

            if (cooldowns.contains(player.getUniqueId())) {
                return;
            }

            if (GUILoader.getLogsPreviousPageSlot().contains(event.getSlot())) {
                JoinEvent.getDataManager().get(player).getLogsGUI().goPreviousPage();
                cooldowns.add(player.getUniqueId());
            } else if (GUILoader.getLogsNextPageSlot().contains(event.getSlot())) {
                JoinEvent.getDataManager().get(player).getLogsGUI().goNextPage();
                cooldowns.add(player.getUniqueId());
            }
            Bukkit.getScheduler().runTaskLater(CraftyCrates.getInstance(), () -> cooldowns.remove(player.getUniqueId()), 10);
        }
    }

}
