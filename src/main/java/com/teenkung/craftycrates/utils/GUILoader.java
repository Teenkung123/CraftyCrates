package com.teenkung.craftycrates.utils;

import com.teenkung.craftycrates.ConfigLoader;
import com.teenkung.craftycrates.CraftyCrates;
import com.teenkung.craftycrates.MySQL.PlayerDataManager;
import com.teenkung.craftycrates.events.JoinEvent;
import com.teenkung.craftycrates.utils.record.RarityStorage;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.teenkung.craftycrates.CraftyCrates.colorize;

public class GUILoader {

    public static List<Integer> getInfoNextPageSlot() {
        return CraftyCrates.getInstance().getConfig().getIntegerList("GUIs.Info.Next-Page-Slot");
    }
    public static List<Integer> getInfoPreviousPageSlot() {
        return CraftyCrates.getInstance().getConfig().getIntegerList("GUIs.Info.Previous-Page-Slot");
    }
    public static List<Integer> getInfoSlot() {
        return CraftyCrates.getInstance().getConfig().getIntegerList("GUIs.Info.Info-Slot");
    }
    public static Inventory getInfoGUI(String poolID, String bannerID, int page) {
        FileConfiguration config = CraftyCrates.getInstance().getConfig();
        HashMap<Character, ItemStack> itemMap = new HashMap<>();

        RarityStorage RStorage = ConfigLoader.getRarityStorage(bannerID, poolID);

        Inventory inv = Bukkit.createInventory(null,
                config.getStringList("GUIs.Info.Layout").size()*9,
                colorize(config.getString("GUIs.Info.Title", "")
                        .replaceAll("<display>", RStorage.display())
                        .replaceAll("<chance>", RStorage.chance().toString())
                        .replaceAll("<banner>", bannerID)));

        ConfigurationSection s = config.getConfigurationSection("GUIs.Info.Items");
        if (s != null) {
            for (String key : s.getKeys(false)) {
                char character = key.charAt(0);
                ConfigurationSection section = config.getConfigurationSection("GUIs.Info.Items." + key);
                if (section != null) {
                    Material material = Material.getMaterial(section.getString("Material", "STONE"));
                    if (material != null) {
                        int amount = section.getInt("Amount");
                        String displayName = colorize(section.getString("Display"));
                        List<String> lore = section.getStringList("Lore");
                        ArrayList<String> newLore = new ArrayList<>();
                        for (String l : lore) {
                            newLore.add(colorize(l
                                    .replaceAll("<banner>", bannerID)
                                    .replaceAll("<pool_chance>", RStorage.chance().toString())
                                    .replaceAll("<uprate_pool>", ConfigLoader.getUpratePool(bannerID))
                                    .replaceAll("<uprate_minimum>", ConfigLoader.getUprateMinimumRoll(bannerID).toString())
                                    .replaceAll("<uprate_enable>", ConfigLoader.getUprateEnabled(bannerID).toString())
                                    .replaceAll("<uprate_additional>", ConfigLoader.getUprateAdditionalRate(bannerID).toString())));
                        }

                        int modelData = section.getInt("ModelData");

                        ItemStack item = new ItemStack(material, amount);
                        ItemMeta meta = item.getItemMeta();
                        if (meta != null) {
                            meta.setDisplayName(displayName);
                            meta.setLore(newLore);
                            if (modelData != 0) {
                                meta.setCustomModelData(modelData);
                            }
                            item.setItemMeta(meta);
                        }

                        NBTItem nbt = new NBTItem(item);
                        nbt.setString("Inv", "GachaInfo");
                        nbt.setString("bannerID", bannerID);
                        nbt.setInteger("page", page);

                        itemMap.put(character, nbt.getItem());

                    }
                }
            }
        }

        String[] layout = config.getStringList("GUIs.Info.Layout").toArray(new String[0]);
        for (int i = 0; i < layout.length; i++) {
            String row = layout[i];
            for (int j = 0; j < row.length(); j++) {
                char character = row.charAt(j);
                ItemStack item = itemMap.getOrDefault(character, null);
                if (item!= null) {
                    inv.setItem(i*9+j, item);
                }
            }
        }
        return inv;
    }
    public static List<Integer> getLogsNextPageSlot() {
        return CraftyCrates.getInstance().getConfig().getIntegerList("GUIs.Logs.Next-Page-Slot");
    }
    public static List<Integer> getLogsPreviousPageSlot() {
        return CraftyCrates.getInstance().getConfig().getIntegerList("GUIs.Logs.Previous-Page-Slot");
    }
    public static List<Integer> getLogsSlot() {
        return CraftyCrates.getInstance().getConfig().getIntegerList("GUIs.Logs.Logs-Slot");
    }
    public static Inventory getLogsGUI(String bannerID, Player player) {
        FileConfiguration config = CraftyCrates.getInstance().getConfig();
        HashMap<Character, ItemStack> itemMap = new HashMap<>();

        Inventory inv = Bukkit.createInventory(null,
                config.getStringList("GUIs.Logs.Layout").size()*9,
                colorize(config.getString("GUIs.Logs.Title", "")
                        .replaceAll("<banner>", bannerID)));

        ConfigurationSection s = config.getConfigurationSection("GUIs.Logs.Items");
        PlayerDataManager manager = JoinEvent.getDataManager().get(player);
        if (s != null) {
            for (String key : s.getKeys(false)) {
                char character = key.charAt(0);
                ConfigurationSection section = config.getConfigurationSection("GUIs.Logs.Items." + key);
                if (section != null) {
                    Material material = Material.getMaterial(section.getString("Material", "STONE"));
                    if (material != null) {
                        int amount = section.getInt("Amount");
                        String displayName = colorize(section.getString("Display"));
                        List<String> lore = section.getStringList("Lore");
                        ArrayList<String> newLore = new ArrayList<>();
                        for (String l : lore) {
                            newLore.add(colorize(l
                                    .replaceAll("<banner>", bannerID)
                                    .replaceAll("<uprate_pool>", ConfigLoader.getUpratePool(bannerID))
                                    .replaceAll("<uprate_minimum>", ConfigLoader.getUprateMinimumRoll(bannerID).toString())
                                    .replaceAll("<uprate_enable>", ConfigLoader.getUprateEnabled(bannerID).toString())
                                    .replaceAll("<uprate_additional>", ConfigLoader.getUprateAdditionalRate(bannerID).toString())
                                    .replaceAll("<current_rate>", Float.valueOf(manager.getAdditionalRate(bannerID)).toString())
                                    .replaceAll("<current_rolls>", Integer.valueOf(manager.getCurrentRoll(bannerID)).toString())
                                    .replaceAll("<total_rolls>", Integer.valueOf(manager.getTotalRoll(bannerID)).toString())
                            ));
                        }

                        int modelData = section.getInt("ModelData");

                        ItemStack item = new ItemStack(material, amount);
                        ItemMeta meta = item.getItemMeta();
                        if (meta != null) {
                            meta.setDisplayName(displayName);
                            meta.setLore(newLore);
                            if (modelData != 0) {
                                meta.setCustomModelData(modelData);
                            }
                            item.setItemMeta(meta);
                        }

                        NBTItem nbt = new NBTItem(item);
                        nbt.setString("Inv", "LogsInfo");

                        itemMap.put(character, nbt.getItem());

                    }
                }
            }
        }

        String[] layout = config.getStringList("GUIs.Logs.Layout").toArray(new String[0]);
        for (int i = 0; i < layout.length; i++) {
            String row = layout[i];
            for (int j = 0; j < row.length(); j++) {
                char character = row.charAt(j);
                ItemStack item = itemMap.getOrDefault(character, null);
                if (item!= null) {
                    inv.setItem(i*9+j, item);
                }
            }
        }
        return inv;
    }


}
