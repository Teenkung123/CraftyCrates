package com.teenkung.craftycrates.events;

import com.teenkung.craftycrates.CraftyCrates;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.UUID;

import static com.teenkung.craftycrates.CraftyCrates.colorize;

public class LogsEvent implements Listener {

    private final ArrayList<UUID> cooldowns = new ArrayList<>();
    @EventHandler
    public void onInteract(InventoryClickEvent event) {
        if (event.getView().getTitle().equals(colorize("&5Pull Logs"))) {
            event.setCancelled(true);
            ItemStack currentItem = event.getCurrentItem();
            if (currentItem != null) {
                Player player = (Player) event.getWhoClicked();
                if (cooldowns.contains(player.getUniqueId())) {
                    return;
                }
                if (event.getSlot() == 8) {
                    JoinEvent.getDataManager().get(player).getLogsGUI().goNextPage();
                    cooldowns.add(player.getUniqueId());
                } else if (event.getSlot() == 0) {
                    JoinEvent.getDataManager().get(player).getLogsGUI().goPreviousPage();
                    cooldowns.add(player.getUniqueId());
                }
                Bukkit.getScheduler().runTaskLater(CraftyCrates.getInstance(), () -> cooldowns.remove(player.getUniqueId()), 10);
            }
        }
    }

}
