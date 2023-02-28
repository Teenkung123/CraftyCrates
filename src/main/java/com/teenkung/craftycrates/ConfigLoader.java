package com.teenkung.craftycrates;

import com.teenkung.craftycrates.utils.record.PoolStorage;
import com.teenkung.craftycrates.utils.record.RarityStorage;
import net.Indyuce.mmoitems.MMOItems;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import static com.teenkung.craftycrates.CraftyCrates.colorize;

public class ConfigLoader {

    //Overall Config Variable
    private static final ArrayList<String> PoolList = new ArrayList<>();
    //Banner Configuration
    private static final ArrayList<String> BannerIdsList = new ArrayList<>();
    private static final HashMap<String, Boolean> uprateEnable = new HashMap<>();
    private static final HashMap<String, Integer> uprateMinRolls = new HashMap<>();
    private static final HashMap<String, Float> uprateAddtionalRate = new HashMap<>();
    private static final HashMap<String, String> upratePool = new HashMap<>();
    private static final HashMap<String, ArrayList<String>> rarities = new HashMap<>();
    private static final HashMap<String, HashMap<String, RarityStorage>> rarityStorage = new HashMap<>();

    //Pool configuration

    private static final HashMap<String, String> poolsID = new HashMap<>();
    private static final HashMap<String, ArrayList<String>> poolsItems = new HashMap<>();
    private static final HashMap<String, HashMap<String, PoolStorage>> poolStorage = new HashMap<>();


    //================================================================================================================================

    public static void reloadConfig() {
        PoolList.clear();

        BannerIdsList.clear();
        uprateEnable.clear();
        uprateMinRolls.clear();
        uprateAddtionalRate.clear();
        upratePool.clear();
        rarities.clear();
        rarityStorage.clear();

        poolsID.clear();
        poolsItems.clear();
        poolStorage.clear();

        loadConfig();
    }

    public static HashMap<String, Float> getBannerChance(String ID) {
        HashMap<String, RarityStorage> storage = rarityStorage.getOrDefault(ID, new HashMap<>());
        HashMap<String, Float> result = new HashMap<>();
        for (String id : storage.keySet()) {
            result.put(id, storage.get(id).chance());
        }
        return result;
    }

    public static RarityStorage getRarityStorage(String BannerID, String PoolId) {
        return rarityStorage.getOrDefault(BannerID, new HashMap<>()).getOrDefault(PoolId, new RarityStorage("", 0F, ""));
    }

    public static PoolStorage getPoolStorage(String PoolID, String SubPool) {
        return poolStorage.getOrDefault(PoolID, new HashMap<>()).getOrDefault(SubPool, new PoolStorage("", "", 0, new ArrayList<>(), 0));
    }

    public static String getPoolIDByFileName(String FileName) {
        return poolsID.getOrDefault(FileName.replaceAll(".yml", ""), "");
    }

    public static HashMap<String, Integer> getPoolsWeight(String ID) {
        HashMap<String, PoolStorage> storage = poolStorage.getOrDefault(ID, new HashMap<>());
        HashMap<String, Integer> result = new HashMap<>();
        for (String id : storage.keySet()) {
            result.put(id, storage.get(id).weight());
        }
        return result;
    }

    public static ArrayList<String> getBannerIdsList() { return BannerIdsList; }
    public static Boolean getUprateEnabled(String banner) {
        return uprateEnable.getOrDefault(banner, false);
    }

    public static Float getUprateAdditionalRate(String banner) {
        return uprateAddtionalRate.getOrDefault(banner, 0F);
    }
    public static Integer getUprateMinimumRoll(String banner) {
        return uprateMinRolls.getOrDefault(banner, 1000000);
    }
    public static String getUpratePool(String banner) {
        return upratePool.getOrDefault(banner, "");
    }

    public static ArrayList<String> getRarities(String banner) {
        return rarities.getOrDefault(banner, new ArrayList<>());
    }

    public static ArrayList<String> getSubPoolsFromPoolIDs(String poolID) {
        return poolsItems.getOrDefault(poolID, new ArrayList<>());
    }

    public static Integer getTotalPoolWeight(String poolID) {
        return getPoolsWeight(poolID).values().stream().mapToInt(Integer::intValue).sum();
    }


    public static void loadConfig() {
        CraftyCrates Instance = CraftyCrates.getInstance();
        Instance.getConfig().options().copyDefaults(true);
        Instance.saveDefaultConfig();
        Instance.reloadConfig();

        System.out.println(colorize("&eLoading Pool Files. . ."));
        for (String Pool : Instance.getConfig().getStringList("Pools")) {
            File file = new File(Instance.getDataFolder(), "Pools/"+Pool);
            if (file.exists()) {
                YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
                ConfigurationSection section = config.getConfigurationSection("list");
                if (section != null) {
                    String filename = Pool.replaceAll(".yml", "");
                    PoolList.add(filename);
                    String id = config.getString("pool_id");
                    poolsID.put(filename, id);
                    poolsItems.put(id, new ArrayList<>(section.getKeys(false)));
                    HashMap<String, PoolStorage> storage = new HashMap<>();
                    for (String key : section.getKeys(false)) {
                        if (MMOItems.plugin.getItem(config.getString("list."+key+".Category", ""), config.getString("list."+key+".ID")) != null) {
                            storage.put(key, new PoolStorage(
                                    config.getString("list."+key+".Category", ""),
                                    config.getString("list."+key+".ID"),
                                    config.getInt("list."+key+".Amount"),
                                    new ArrayList<>(config.getStringList("list."+key+".Command")),
                                    config.getInt("list."+key+".Weight")
                            ));
                        } else {
                            System.out.println(colorize("&cCould not find Item with Category "+config.getString("list."+key+".Category", "")+" And ID: "+config.getString("list."+key+".ID")));
                        }
                    }
                    poolStorage.put(id, storage);
                }

                System.out.println(colorize("&aLoaded Pool File: " + file.getName()));
            } else {
                System.out.println(colorize("&cUnable to load Pool File: " + file.getName() + " &c(File not found)"));
            }
        }

        System.out.println(colorize("&eLoading Banner Files. . ."));
        for (String Banner : Instance.getConfig().getStringList("Banners")) {
            File file = new File(Instance.getDataFolder(), "Banners/"+Banner);
            System.out.println(colorize("&aLoaded Banner File: " + file.getName()));
            if (file.exists()) {
                YamlConfiguration config = YamlConfiguration.loadConfiguration(file);


                ConfigurationSection section = config.getConfigurationSection("rarities");
                if (section != null) {
                    String id = config.getString("banner_id");
                    BannerIdsList.add(id);
                    uprateEnable.put(id, config.getBoolean("uprate.enabled", false));
                    uprateMinRolls.put(id, config.getInt("uprate.minimum_roll", 90));
                    uprateAddtionalRate.put(id,  Double.valueOf(config.getDouble("uprate.additional_rate", 2D)).floatValue());
                    upratePool.put(id, config.getString("uprate.uprate_pool", ""));

                    ArrayList<String> l = new ArrayList<>();
                    HashMap<String, RarityStorage> storage = new HashMap<>();
                    for (String key : section.getKeys(false)) {
                        if (PoolList.contains(config.getString("rarities."+key+".pool", "").replaceAll(".yml", ""))) {
                            l.add(key);
                            storage.put(key, new RarityStorage(
                                    config.getString("rarities."+key+".display", ""),
                                    Double.valueOf(config.getDouble("rarities."+key+".chance", 0)).floatValue(),
                                    config.getString("rarities."+key+".pool", "").replaceAll(".yml", "")
                            ));
                        } else {
                            System.out.println(colorize("&6Could not find pool named " + config.getString("rarities."+key+".pool")));
                        }
                    }
                    rarityStorage.put(id, storage);
                    rarities.put(id, l);

                } else {
                    System.out.println(colorize("&cUnable to load Banner File: " + file.getName() + "&c(Invalid rarities node)"));
                }

            } else {
                System.out.println(colorize("&cUnable to load Banner File: " + file.getName() + " &c(File not found)"));
            }
        }

    }

}
