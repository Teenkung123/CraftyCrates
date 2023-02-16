package com.teenkung.craftycrates;

import com.teenkung.craftycrates.utils.PullStorage;
import com.teenkung.craftycrates.utils.RarityStorage;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
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
    private static final HashMap<String, Double> uprateAddtionalRate = new HashMap();
    private static final HashMap<String, String> upratePool = new HashMap<>();
    private static final HashMap<String, ArrayList> rarities = new HashMap<>();
    private static final HashMap<String, HashMap<String, RarityStorage>> rarityStorage = new HashMap<>();

    //Pull configuration

    private static final ArrayList<String> PullsIdList = new ArrayList<>();
    private static final HashMap<String, String> pullUprateItem = new HashMap<>();
    private static final HashMap<String, Integer> pullGuarantee = new HashMap<>();
    private static final HashMap<String, ArrayList<String>> pullsItems = new HashMap<>();
    private static final HashMap<String, HashMap<String, PullStorage>> pullStorage = new HashMap<>();



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
                    PullsIdList.add(config.getString("pool_id"));
                    pullUprateItem.put(filename, config.getString("uprate.uprate_item", ""));
                    pullGuarantee.put(filename, config.getInt("uprate.guarantee", 1000000));
                    pullsItems.put(filename, new ArrayList<>(section.getKeys(false)));
                    for (String key : section.getKeys(false)) {
                        HashMap<String, PullStorage> l = new HashMap<>();
                        l.put(key, new PullStorage(
                                config.getString("list."+key+".Category", ""),
                                config.getString("list."+key+".ID"),
                                config.getInt("list."+key+".Amount"),
                                new ArrayList<>(config.getStringList("list."+key+".Command")),
                                config.getInt("list."+key+".Weight")
                        ));
                        pullStorage.put(filename, l);
                    }
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

                    BannerIdsList.add(config.getString("banner_id"));
                    bannerIDs.put(config.getString("banner_id"), filename);
                    uprateEnable.put(filename, config.getBoolean("uprate.enabled", false));
                    uprateMinRolls.put(filename, config.getInt("uprate.minimum_roll", 90));
                    uprateAddtionalRate.put(filename, config.getDouble("uprate.additional_rate", 2.0));
                    upratePool.put(filename, config.getString("uprate.uprate_pool", ""));

                    ArrayList<String> l = new ArrayList<>();
                    for (String key : section.getKeys(false)) {
                        if (PoolList.contains(config.getString("rarities."+key+".pool", "").replaceAll(".yml", ""))) {
                            l.add(key);
                            HashMap<String, RarityStorage> storage = new HashMap<>();
                            storage.put(key, new RarityStorage(
                                    config.getString("rarities."+key+".display", ""),
                                    config.getDouble("rarities."+key+".chance", 0.00),
                                    config.getString("rarities."+key+".pool", "").replaceAll(".yml", "")
                            ));
                            rarityStorage.put(filename, storage);
                        } else {
                            System.out.println(colorize("&6Could not find pool named " + config.getString("rarities."+key+".pool")));
                        }
                    }
                    rarities.put(filename, l);

                } else {
                    System.out.println(colorize("&cUnable to load Banner File: " + file.getName() + "&c(Invalid rarities node)"));
                }

            } else {
                System.out.println(colorize("&cUnable to load Banner File: " + file.getName() + " &c(File not found)"));
            }
        }

    }

}
