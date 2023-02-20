package com.teenkung.craftycrates.MySQL;

import com.teenkung.craftycrates.ConfigLoader;
import com.teenkung.craftycrates.CraftyCrates;
import com.teenkung.craftycrates.utils.selector.ChanceRandomSelector;
import com.teenkung.craftycrates.utils.selector.WeightedRandomSelector;
import com.teenkung.craftycrates.utils.storage.ItemPair;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import static com.teenkung.craftycrates.CraftyCrates.colorize;

public class PlayerDataManager {

    private final HashMap<String, Integer> totalRolls = new HashMap<>();
    private final HashMap<String, Integer> currentRolls = new HashMap<>();

    public PlayerDataManager(Player player) {
        UUID uuid = player.getUniqueId();
        // Run the code asynchronously to avoid blocking the main thread
        Bukkit.getScheduler().runTaskAsynchronously(CraftyCrates.getInstance(), () -> {
            // Query to check if a banner for a player is present in the database
            String query = "SELECT COUNT(UUID), TotalRolls, CurrentRolls FROM craftycrates_playerbannerdata WHERE UUID = ? AND BannerID = ?;";
            // Loop through each banner ID
            for (String banner : ConfigLoader.getBannerIdsList()) {
                try (PreparedStatement statement = CraftyCrates.getConnection().prepareStatement(query)) {
                    // Set the placeholders for the player UUID and banner ID in the query
                    statement.setString(1, uuid.toString());
                    statement.setString(2, banner);
                    try (ResultSet rs = statement.executeQuery()) {
                        // If the banner is not present in the database, insert a new entry
                        if (rs.next() && rs.getInt(1) == 0) {
                            String query2 = "INSERT INTO CraftyCrates_PlayerBannerData (UUID, BannerID, TotalRolls, CurrentRolls) VALUES (?, ?, 0, 0)";
                            try (PreparedStatement ps = CraftyCrates.getConnection().prepareStatement(query2)) {
                                // Set the placeholders for the player UUID and banner ID in the insert query
                                ps.setString(1, uuid.toString());
                                ps.setString(2, banner);
                                ps.executeUpdate();
                            }
                        } else {
                            // If the banner is present in the database, load the player data for that banner
                            totalRolls.put(banner, rs.getInt("TotalRolls"));
                            currentRolls.put(banner, rs.getInt("CurrentRolls"));
                        }
                    }
                } catch (SQLException e) {
                    // Handle any errors that occur during the database operations
                    System.out.println(colorize("&4Cannot load Data from Player " + player.getName()));
                    synchronized (CraftyCrates.getInstance()) {
                        player.kickPlayer(colorize("&4Something went wrong! Please reconnect in a few seconds"));
                    }
                }
            }
        });
    }

    public int getCurrentRoll(String banner) {
        return currentRolls.getOrDefault(banner, 0);
    }

    public int getTotalRoll(String banner) {
        return totalRolls.getOrDefault(banner, 0);
    }

    public float getAdditionalRate(String banner) {
        if (ConfigLoader.getBannerIdsList().contains(banner)) {
            if (ConfigLoader.getUprateEnabled(banner)) {
                if (currentRolls.getOrDefault(banner, 0) <= ConfigLoader.getUprateMinimumRoll(banner)) {
                    float increase = ConfigLoader.getUprateAdditionalRate(banner) * ((currentRolls.getOrDefault(banner, 0) - ConfigLoader.getUprateMinimumRoll(banner)));
                    return Math.max(increase, 0);
                } else {
                    return 0;
                }
            } else {
                return 0;
            }
        } else {
            return 0;
        }
    }

    public ArrayList<ItemPair> requestPull(String banner, Integer amount) {
        ArrayList<ItemPair> result = new ArrayList<>();
        for (int i = 0 ; i < amount; i++) {
            String selectedID = ChanceRandomSelector.selectByChance(ConfigLoader.getBannerChance(banner));
            String selectedItem = WeightedRandomSelector.select(ConfigLoader.getPullsWeight(selectedID));
        }

        return result;
    }

}
