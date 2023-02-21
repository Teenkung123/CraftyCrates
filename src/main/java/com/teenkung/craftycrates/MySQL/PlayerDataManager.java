package com.teenkung.craftycrates.MySQL;

import com.teenkung.craftycrates.ConfigLoader;
import com.teenkung.craftycrates.CraftyCrates;
import com.teenkung.craftycrates.utils.record.ItemPair;
import com.teenkung.craftycrates.utils.selector.ChanceRandomSelector;
import com.teenkung.craftycrates.utils.selector.WeightedRandomSelector;
import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import net.Indyuce.mmoitems.MMOItems;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

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

    private final Player player;

    public PlayerDataManager(Player player) {
        this.player = player;
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

    public void setCurrentRoll(String banner, int currentRoll) {
        currentRolls.put(banner, currentRoll);
        Bukkit.getScheduler().runTaskAsynchronously(CraftyCrates.getInstance(), () -> {
            try {
                PreparedStatement ps = CraftyCrates.getConnection().prepareStatement("UPDATE `craftycrates_playerbannerdata` SET `CurrentRolls` = ? WHERE `UUID` = ? AND `BannerID` = ?;");
                ps.setInt(1, currentRoll);
                ps.setString(2, player.getUniqueId().toString());
                ps.setString(3, banner);
                ps.executeUpdate();
                ps.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void setTotalRoll(String banner, int TotalRoll) {
        totalRolls.put(banner, TotalRoll);
        Bukkit.getScheduler().runTaskAsynchronously(CraftyCrates.getInstance(), () -> {
            try {
                PreparedStatement ps = CraftyCrates.getConnection().prepareStatement("UPDATE `craftycrates_playerbannerdata` SET `TotalRolls` = ? WHERE `UUID` = ? AND `BannerID` = ?;");
                ps.setInt(1, TotalRoll);
                ps.setString(2, player.getUniqueId().toString());
                ps.setString(3, banner);
                ps.executeUpdate();
                ps.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void applyLogs(String banner, ItemStack stack) {
        Bukkit.getScheduler().runTaskAsynchronously(CraftyCrates.getInstance(), () -> {
            try {
                ReadWriteNBT nbt = NBT.itemStackToNBT(stack);
                PreparedStatement ps = CraftyCrates.getConnection().prepareStatement("INSERT INTO craftycrates_logs (UUID, BannerID, ItemNBT, Date) VALUES (?, ?, ?, ?)");
                ps.setString(1, this.player.getUniqueId().toString());
                ps.setString(2, banner);
                ps.setString(3, nbt.toString());
                ps.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
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
            HashMap<String, Float> rate = ConfigLoader.getBannerChance(banner);
            if (getAdditionalRate(banner) > 0) {
                rate.replace(banner, rate.get(banner)+getAdditionalRate(banner));
            }
            System.out.println(colorize("&dRate: &b" + rate.get(banner) + " &c| &dRolls: &b" + currentRolls));
            String selectedID = ChanceRandomSelector.selectByChance(rate);
            String selectedPoolID = WeightedRandomSelector.select(ConfigLoader.getPullsWeight(ConfigLoader.getPullIDByFileName(ConfigLoader.getRarityStorage(banner, selectedID).pool())));
            String ItemCategory = ConfigLoader.getPoolStorage(selectedID, selectedPoolID).category();
            String ItemID = ConfigLoader.getPoolStorage(selectedID, selectedPoolID).id();
            ItemStack stack = MMOItems.plugin.getItem(ItemCategory, ItemID);
            ItemPair pair = new ItemPair(stack , ConfigLoader.getPoolStorage(selectedID, selectedPoolID).commands());
            result.add(pair);

            //MySQL things
            setCurrentRoll(banner, currentRolls.get(banner)+1);
            setTotalRoll(banner, totalRolls.get(banner)+1);
            applyLogs(banner, stack);

        }

        return result;
    }

}
