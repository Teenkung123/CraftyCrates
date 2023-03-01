package com.teenkung.craftycrates.GUIs;

import com.teenkung.craftycrates.ConfigLoader;
import com.teenkung.craftycrates.CraftyCrates;
import com.teenkung.craftycrates.events.JoinEvent;
import com.teenkung.craftycrates.utils.GUILoader;
import com.teenkung.craftycrates.utils.ItemSerialization;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Objects;

import static com.teenkung.craftycrates.CraftyCrates.colorize;

public class LogsGUI {

    private final Player player;
    private Inventory inv;
    private Integer page;
    private String bannerID;
    public LogsGUI(Player player) {
        this.player = player;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public void setBannerID(String bannerID) {
        this.bannerID = bannerID;
    }
    public void goNextPage() {
        Bukkit.getScheduler().runTaskAsynchronously(CraftyCrates.getInstance(), () -> {
            if (!checkIfExceed()) {
                setPage(page+1);
                loadData();
            }
        });
    }

    public void goPreviousPage() {
        if (page > 1) {
            setPage(page-1);
            loadData();
        } else {
            setPage(Double.valueOf(Math.ceil(JoinEvent.getDataManager().get(player).getTotalRoll(bannerID) / 45D)).intValue());
            loadData();
        }
    }

    private Boolean checkIfExceed() {
        try {
            PreparedStatement statement = CraftyCrates.getConnection().prepareStatement("SELECT COUNT(ID) FROM craftycrates_logs WHERE UUID = ? AND BannerID = ?");
            statement.setString(1, player.getUniqueId().toString());
            statement.setString(2, bannerID);
            ResultSet rs = statement.executeQuery();
            rs.next();
            int total = rs.getInt("COUNT(ID)");
            statement.close();
            return page > Math.ceil(total / 45D);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    private void loadData() {
        Bukkit.getScheduler().runTaskAsynchronously(CraftyCrates.getInstance(), () -> {
            if (checkIfExceed()) {
                page = 1;
            }
            resetGUI();
            int start = (page - 1) * 45;
            int end = page * 45 - 1;
            try {
                PreparedStatement statement = CraftyCrates.getConnection().prepareStatement("SELECT * FROM craftycrates_logs WHERE UUID = ? AND BannerID = ? ORDER BY Date DESC LIMIT ?, ?");
                statement.setString(1, player.getUniqueId().toString());
                statement.setString(2, bannerID);
                statement.setInt(3, start);
                statement.setInt(4, end+1);
                ResultSet rs = statement.executeQuery();
                ItemStack item;
                ItemMeta meta;
                ArrayList<String> lore;
                int count = JoinEvent.getDataManager().get(player).getTotalRoll(bannerID);
                while (rs.next()) {
                    item = ItemSerialization.deserializeItemStack(rs.getString("Data"));
                    meta = item.getItemMeta();
                    if (meta != null) {
                        meta.setDisplayName(colorize("&e#" + count + " " + meta.getDisplayName()));
                        lore = new ArrayList<>();
                        if (meta.hasLore()) {
                            lore.addAll(meta.getLore());
                        }
                        lore.add(colorize("&f "));
                        lore.add(colorize("&fOpened Time: &e" + rs.getString("Date")));
                        meta.setLore(lore);
                    }
                    item.setItemMeta(meta);

                    for (int e : GUILoader.getLogsSlot()) {
                        if (inv.getItem(e) == null) {
                            inv.setItem(e, item);
                            break;
                        } else if (Objects.requireNonNull(inv.getItem(e)).getType() == Material.AIR) {
                            inv.setItem(e, item);
                            break;
                        }

                    }
                    count--;
                }
                statement.close();
                Bukkit.getScheduler().runTask(CraftyCrates.getInstance(), () -> player.openInventory(inv));
            } catch (SQLException | IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void resetGUI() {
        inv = GUILoader.getLogsGUI(bannerID, player);
    }

    public void openInventory() {
        if (page == null) {
            page = 1;
        } if (bannerID == null) {
            bannerID = ConfigLoader.getBannerIdsList().get(1);
        }
        loadData();
    }

}
