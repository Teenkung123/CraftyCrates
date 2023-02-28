package com.teenkung.craftycrates.GUIs;

import com.teenkung.craftycrates.ConfigLoader;
import com.teenkung.craftycrates.utils.Functions;
import de.tr7zw.nbtapi.NBTItem;
import net.Indyuce.mmoitems.MMOItems;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

import static com.teenkung.craftycrates.CraftyCrates.colorize;

public class InfoGUI {

    private static final HashMap<String, ArrayList<Inventory>> guisList = new HashMap<>();

    public static void loadGUIs() {
        for (String bannerID : ConfigLoader.getBannerIdsList()) {
            ArrayList<String> PoolList = ConfigLoader.getRarities(bannerID);
            ArrayList<Inventory> infoList = new ArrayList<>();
            int page = 0;
            for (String pool : PoolList) {
                Inventory inv = Bukkit.createInventory(null, 54, colorize("&dGacha info: "+ConfigLoader.getRarityStorage(bannerID, pool).display()));
                page++;
                for (int i = 0; i < 54 ; i++) {
                    if (i < 9 || i > 45) {
                        ItemStack bg = Functions.getInventoryItem(Material.BLACK_STAINED_GLASS_PANE, 1, "", null);
                        NBTItem nbt = new NBTItem(bg);
                        nbt.setInteger("page", page);
                        nbt.setString("bannerID", bannerID);
                        nbt.setString("Inv", "GachaInfo");
                        inv.setItem(i, nbt.getItem());
                    }
                }
                inv.setItem(53, Functions.getInventoryItem(Material.LIME_STAINED_GLASS_PANE, 1, "&aNext Page", null));
                inv.setItem(45, Functions.getInventoryItem(Material.RED_STAINED_GLASS_PANE, 1, "&cPrevious Page", null));

                String poolID = ConfigLoader.getPoolIDByFileName(ConfigLoader.getRarityStorage(bannerID, pool).pool());
                int totalWeight = ConfigLoader.getTotalPoolWeight(poolID);
                for (String subPool : ConfigLoader.getSubPoolsFromPoolIDs(poolID)) {
                    String category = ConfigLoader.getPoolStorage(poolID, subPool).category();
                    String id = ConfigLoader.getPoolStorage(poolID, subPool).id();
                    ItemStack stack = MMOItems.plugin.getItem(category, id);
                    if (stack != null) {
                        ItemMeta meta = stack.getItemMeta();
                        if (meta != null) {
                            ArrayList<String> lore;
                            if (meta.getLore() != null) {
                                lore = new ArrayList<>(meta.getLore());
                            } else {
                                lore = new ArrayList<>();
                            }
                            lore.add("");
                            float percent = (ConfigLoader.getPoolStorage(poolID, subPool).weight().floatValue() / totalWeight) * 100;
                            DecimalFormat decimalFormat = new DecimalFormat("#.00");
                            lore.add(colorize("&eChance: &b" + decimalFormat.format(percent) + "&e%"));
                            meta.setLore(lore);
                            stack.setItemMeta(meta);
                        }
                    }
                    inv.addItem(stack);
                }
                infoList.add(inv);
            }
            guisList.put(bannerID, infoList);
        }

    }

    public static void openTo(Player player, String bannerID) {
        player.openInventory(guisList.get(bannerID).get(0));
    }

    public static void goNextPage(Player player, String bannerID) {
        ItemStack check = player.getOpenInventory().getTopInventory().getItem(0);
        if (check != null) {
            NBTItem item = new NBTItem(check);
            if (item.getString("Inv").equals("GachaInfo")) {
                if (item.getInteger("page") < guisList.get(bannerID).size()) {
                    player.openInventory(guisList.get(bannerID).get(item.getInteger("page")));
                } else {
                    player.openInventory(guisList.get(bannerID).get(0));
                }
            }
        }
    }

    public static void goPreviousPage(Player player, String bannerID) {
        ItemStack check = player.getOpenInventory().getTopInventory().getItem(0);
        if (check != null) {
            NBTItem item = new NBTItem(check);
            if (item.getString("Inv").equals("GachaInfo")) {
                if (item.getInteger("page") <= 1) {
                    player.openInventory(guisList.get(bannerID).get(guisList.get(bannerID).size() - 1));
                } else {
                    player.openInventory(guisList.get(bannerID).get(item.getInteger("page") - 2));
                }
            }
        }
    }
}
