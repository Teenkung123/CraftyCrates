package com.teenkung.craftycrates.MySQL;

import com.teenkung.craftycrates.ConfigLoader;
import com.teenkung.craftycrates.CraftyCrates;
import com.teenkung.craftycrates.GUIs.LogsGUI;
import com.teenkung.craftycrates.utils.ItemSerialization;
import com.teenkung.craftycrates.utils.record.ItemPair;
import com.teenkung.craftycrates.utils.selector.ChanceRandomSelector;
import com.teenkung.craftycrates.utils.selector.WeightedRandomSelector;
import dev.lone.itemsadder.api.CustomStack;
import net.Indyuce.mmoitems.MMOItems;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import static com.teenkung.craftycrates.CraftyCrates.colorize;

public class PlayerDataManager {

    private final ConcurrentHashMap<String, Integer> totalRolls = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Integer> currentRolls = new ConcurrentHashMap<>();

    private final LogsGUI gui;

    private final Player player;

    public PlayerDataManager(Player player) {
        this.player = player;
        gui = new LogsGUI(player);
        gui.setPage(1);
        UUID uuid = player.getUniqueId();
            String selectQuery = "SELECT BannerID, TotalRolls, CurrentRolls FROM craftycrates_playerbannerdata WHERE UUID = ?";
            try {
                Connection conn = CraftyCrates.getConnection();
                PreparedStatement selectStatement = conn.prepareStatement(selectQuery) ;
                // Set the placeholder for the player UUID in the select query
                selectStatement.setString(1, uuid.toString());
                try (ResultSet rs = selectStatement.executeQuery()) {
                    // Load the player data for each banner
                    while (rs.next()) {
                        String banner = rs.getString("BannerID");
                        totalRolls.put(banner, rs.getInt("TotalRolls"));
                        currentRolls.put(banner, rs.getInt("CurrentRolls"));
                    }
                }
                selectStatement.close();
            } catch (SQLException e) {
                // Handle any errors that occur during the database operations
                System.out.println(colorize("&4Cannot load Data from Player " + player.getName()));
               player.kickPlayer(colorize("&4Something went wrong! Please reconnect in a few seconds"));
                }
            // Loop through each banner ID
            for (String banner : ConfigLoader.getBannerIdsList()) {
                // Check if the banner data has been loaded from the database
                if (!totalRolls.containsKey(banner) || !currentRolls.containsKey(banner)) {
                    // If the banner data is missing, insert a new entry in the database
                    String insertQuery = "INSERT INTO CraftyCrates_PlayerBannerData (UUID, BannerID, TotalRolls, CurrentRolls) VALUES (?, ?, 0, 0)";
                    try {
                        Connection conn = CraftyCrates.getConnection();
                        PreparedStatement insertStatement = conn.prepareStatement(insertQuery);
                        // Set the placeholders for the player UUID and banner ID in the insert query
                        insertStatement.setString(1, uuid.toString());
                        insertStatement.setString(2, banner);
                        insertStatement.executeUpdate();
                        insertStatement.close();
                    } catch (SQLException e) {
                        // Handle any errors that occur during the database operations
                        System.out.println(colorize("&4Cannot load Data from Player " + player.getName() + ":&c"+e));
                        e.printStackTrace();
                        player.kickPlayer(colorize("&4Something went wrong! Please reconnect in a few seconds"));
                        return;
                    }
                }
            }
    }

    public int getTotalRoll(String banner) {
        return totalRolls.getOrDefault(banner, 0);
    }
    public int getCurrentRoll(String banner) {return currentRolls.getOrDefault(banner, 0);}

    public void setCurrentRoll(String banner, int currentRoll) {
        currentRolls.put(banner, currentRoll);
    }

    public void updateCurrentRoll(String banner) {
        Bukkit.getScheduler().runTaskAsynchronously(CraftyCrates.getInstance(), () -> {
            try (PreparedStatement ps = CraftyCrates.getConnection().prepareStatement("UPDATE `craftycrates_playerbannerdata` SET `CurrentRolls` = ? WHERE `UUID` = ? AND `BannerID` = ?;")) {
                ps.setInt(1, currentRolls.get(banner));
                ps.setString(2, player.getUniqueId().toString());
                ps.setString(3, banner);
                ps.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void setTotalRoll(String banner, int totalRoll) {
        totalRolls.put(banner, totalRoll);
        Bukkit.getScheduler().runTaskAsynchronously(CraftyCrates.getInstance(), () -> {
            try (PreparedStatement ps = CraftyCrates.getConnection().prepareStatement("UPDATE `craftycrates_playerbannerdata` SET `TotalRolls` = ? WHERE `UUID` = ? AND `BannerID` = ?;")) {
                ps.setInt(1, totalRoll);
                ps.setString(2, player.getUniqueId().toString());
                ps.setString(3, banner);
                ps.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }


    public LogsGUI getLogsGUI() {
        return gui;
    }

    /**
     * Adds the player's roll and the won item into the logs table asynchronously
     * @param banner The banner ID for which the logs are being applied
     * @param stack The won item stack
     */
    public void applyLogs(String banner, ItemStack stack) {
        Bukkit.getScheduler().runTaskAsynchronously(CraftyCrates.getInstance(), () -> {
            try {
                PreparedStatement ps = CraftyCrates.getConnection().prepareStatement("INSERT INTO craftycrates_logs (UUID, BannerID, Data, Date) VALUES (?, ?, ?, NOW())") ;
                ps.setString(1, player.getUniqueId().toString());
                ps.setString(2, banner);
                ps.setString(3, ItemSerialization.serializeItemStack(stack));
                ps.executeUpdate();
                ps.close();
            } catch (SQLException | IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * Returns the additional rate for the specified banner based on current rolls
     * @param banner The banner ID for which the additional rate is being calculated
     * @return The additional rate
     */
    public float getAdditionalRate(String banner) {
        if (ConfigLoader.getBannerIdsList().contains(banner) && ConfigLoader.getUprateEnabled(banner)) {
            int currentRoll = currentRolls.getOrDefault(banner, 0);
            int minRoll = ConfigLoader.getUprateMinimumRoll(banner);
            if (currentRoll >= minRoll) {
                float increase = ConfigLoader.getUprateAdditionalRate(banner) * (currentRoll - minRoll);
                return Math.max(increase, 0);
            }
        }
        return 0;
    }

    /**
     * Requests the specified amount of item pairs from the pool based on the specified banner
     * @param banner The banner ID for which the pool is being requested
     * @param amount The amount of item pairs being requested
     * @return An ArrayList of ItemPair objects
     */
    public CompletableFuture<ArrayList<ItemPair>> requestPool(String banner, Integer amount) {
        CompletableFuture<ArrayList<ItemPair>> future = new CompletableFuture<>();
        Bukkit.getScheduler().runTaskAsynchronously(CraftyCrates.getInstance(), () -> {
            ArrayList<ItemPair> result = new ArrayList<>();
            for (int i = 0; i < amount; i++) {
                HashMap<String, Float> rate = ConfigLoader.getBannerChance(banner);
                float additional = getAdditionalRate(banner);
                if (additional > 0) {
                    rate.put(ConfigLoader.getUpratePool(banner), rate.get(ConfigLoader.getUpratePool(banner)) + additional);
                }
                String selectedID = ChanceRandomSelector.selectByChance(rate);
                if (selectedID == null) {
                    continue;
                }
                String poolStorage = ConfigLoader.getRarityStorage(banner, selectedID).pool();
                String poolID = ConfigLoader.getPoolIDByFileName(poolStorage);
                HashMap<String, Integer> poolWeight = ConfigLoader.getPoolsWeight(poolID);
                String selectedPoolID = WeightedRandomSelector.select(poolWeight);
                String itemCategory = ConfigLoader.getPoolStorage(poolID, selectedPoolID).category();
                String itemID = ConfigLoader.getPoolStorage(poolID, selectedPoolID).id();

                Bukkit.getScheduler().runTask(CraftyCrates.getInstance(), () -> {
                    ItemStack stack;
                    if (itemCategory.equalsIgnoreCase("ItemsAdder")) {
                        stack = CustomStack.getInstance(itemID).getItemStack();
                    } else {
                        stack = MMOItems.plugin.getItem(itemCategory, itemID);
                    }
                    ItemPair pair = new ItemPair(stack, ConfigLoader.getPoolStorage(selectedID, selectedPoolID).commands());
                    result.add(pair);

                    //MySQL things
                    int currentRoll = currentRolls.getOrDefault(banner, 0) + 1;
                    if (selectedID.equals(ConfigLoader.getUpratePool(banner))) {
                        setCurrentRoll(banner, 0);
                    } else {
                        setCurrentRoll(banner, currentRoll);
                    }
                    applyLogs(banner, stack);
                });
            }
            setTotalRoll(banner, totalRolls.getOrDefault(banner, 0) + amount);
            updateCurrentRoll(banner);
            future.complete(result);
        });
        return future;
    }

}
