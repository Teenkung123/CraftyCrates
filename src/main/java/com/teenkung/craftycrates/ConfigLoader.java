package com.teenkung.craftycrates;

import com.teenkung.craftycrates.utils.storage.PoolStorage;
import com.teenkung.craftycrates.utils.storage.RarityStorage;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import static com.teenkung.craftycrates.CraftyCrates.colorize;

public class ConfigLoader {

    //Overall Config Variable
    private static final ArrayList<String> BannerList = new ArrayList<>();
    private static final ArrayList<String> PoolList = new ArrayList<>();
    private static final HashMap<String, YamlConfiguration> Banners = new HashMap<>();
    private static final HashMap<String, YamlConfiguration> Pools = new HashMap<>();

    //Banner Configuration
    private static final ArrayList<String> BannerIdsList = new ArrayList<>();
    private static final HashMap<String, String> bannerIDs = new HashMap<>();
    private static final HashMap<String, Boolean> uprateEnable = new HashMap<>();
    private static final HashMap<String, Integer> uprateMinRolls = new HashMap<>();
    private static final HashMap<String, Float> uprateAddtionalRate = new HashMap<>();
    private static final HashMap<String, String> upratePool = new HashMap<>();
    private static final HashMap<String, ArrayList<String>> rarities = new HashMap<>();
    private static final HashMap<String, HashMap<String, RarityStorage>> rarityStorage = new HashMap<>();

    //Pull configuration

    private static final ArrayList<String> PullsIdList = new ArrayList<>();
    private static final HashMap<String, String> pullsID = new HashMap<>();
    private static final HashMap<String, ArrayList<String>> pullsItems = new HashMap<>();
    private static final HashMap<String, HashMap<String, PoolStorage>> pullStorage = new HashMap<>();


    //================================================================================================================================

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

    public static PoolStorage getPoolStorage(String BannerID, String Pool) {
        return pullStorage.getOrDefault(BannerID, new HashMap<>()).getOrDefault(Pool, new PoolStorage("", "", 0, new ArrayList<>(), 0));
    }

    public static String getPullIDByFileName(String FileName) {
        return pullsID.getOrDefault(FileName.replaceAll(".yml", ""), "");
    }

    public static HashMap<String, Integer> getPullsWeight(String ID) {
        HashMap<String, PoolStorage> storage = pullStorage.getOrDefault(ID, new HashMap<>());
        HashMap<String, Integer> result = new HashMap<>();
        for (String id : storage.keySet()) {
            result.put(id, storage.get(id).getWeight());
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
                    Pools.put(filename, config);
                    String id = config.getString("pool_id");
                    pullsID.put(filename, id);
                    PullsIdList.add(id);
                    pullsItems.put(id, new ArrayList<>(section.getKeys(false)));
                    HashMap<String, PoolStorage> storage = new HashMap<>();
                    for (String key : section.getKeys(false)) {
                        storage.put(key, new PoolStorage(
                                config.getString("list."+key+".Category", ""),
                                config.getString("list."+key+".ID"),
                                config.getInt("list."+key+".Amount"),
                                new ArrayList<>(config.getStringList("list."+key+".Command")),
                                config.getInt("list."+key+".Weight")
                        ));
                    }
                    pullStorage.put(id, storage);
                    System.out.println(colorize("&d"+pullStorage));
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
                String filename = Banner.replaceAll(".yml", "");
                YamlConfiguration config = YamlConfiguration.loadConfiguration(file);


                ConfigurationSection section = config.getConfigurationSection("rarities");
                if (section != null) {
                    BannerList.add(filename);
                    Banners.put(filename, config);

                    String id = config.getString("banner_id");
                    BannerIdsList.add(id);
                    bannerIDs.put(filename, id);
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
