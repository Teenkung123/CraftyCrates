package com.teenkung.craftycrates.events;

import com.teenkung.craftycrates.MySQL.PlayerDataManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.HashMap;

public class JoinEvent implements Listener {

    private static final HashMap<Player, PlayerDataManager> dataManager = new HashMap<>();
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if (!dataManager.containsKey(event.getPlayer())) {
            dataManager.put(event.getPlayer(), new PlayerDataManager(event.getPlayer()));
        }
    }

    public static HashMap<Player, PlayerDataManager> getDataManager() { return dataManager; }

}
