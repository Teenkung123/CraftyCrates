package com.teenkung.craftycrates;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import static com.teenkung.craftycrates.CraftyCrates.colorize;

public class ConfigLoader {

    private static final ArrayList<String> BannerList = new ArrayList<>();
    private static final ArrayList<String> PoolList = new ArrayList<>();
    private static final HashMap<String, ArrayList> rarities = new HashMap<>();
    private static final HashMap<String, Boolean> uprateEnable = new HashMap<>();
    private static final HashMap<String, Integer> uprateMinRolls = new HashMap<>();
    private static final HashMap<String, Double> uprateAddtionalRate = new HashMap();
    private static final HashMap<String, String> upratePool = new HashMap<>();
    private static final HashMap<String, YamlConfiguration> Banners = new HashMap<>();
    private static final HashMap<String, YamlConfiguration> Pools = new HashMap<>();

    public static void loadConfig() {
        CraftyCrates Instance = CraftyCrates.getInstance();
        Instance.getConfig().options().copyDefaults(true);
        Instance.saveDefaultConfig();
        Instance.reloadConfig();

        System.out.println(colorize("&eLoading Pool Files. . ."));
        for (String Pool : Instance.getConfig().getStringList("Pools")) {
            File file = new File(Instance.getDataFolder(), "Pools/"+Pool);
            if (file.exists()) {
                PoolList.add(Pool.replaceAll(".yml", ""));
                Pools.put(Pool.replaceAll(".yml", ""), YamlConfiguration.loadConfiguration(file));
                System.out.println(colorize("&aLoaded Pool File: " + file.getName()));
            } else {
                System.out.println(colorize("&cUnable to load Pool File: " + file.getName() + " &c(File not found)"));
            }
        }

        System.out.println(colorize("&eLoading Banner Files. . ."));
        for (String Banner : Instance.getConfig().getStringList("Banners")) {
            File file = new File(Instance.getDataFolder(), "Banners/"+Banner);
            if (file.exists()) {
                BannerList.add(Banner.replaceAll(".yml", ""));
                Banners.put(Banner.replaceAll(".yml", ""), YamlConfiguration.loadConfiguration(file));
                System.out.println(colorize("&aLoaded Banner File: " + file.getName()));
            } else {
                System.out.println(colorize("&cUnable to load Banner File: " + file.getName() + " &c(File not found)"));
            }
        }

    }

}
