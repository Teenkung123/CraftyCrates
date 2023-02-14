package com.teenkung.craftycrates;

import com.iridium.iridiumcolorapi.IridiumColorAPI;
import org.bukkit.plugin.java.JavaPlugin;

public final class CraftyCrates extends JavaPlugin {

    private static CraftyCrates Instance;
    @Override
    public void onEnable() {
        Instance = this;
        ConfigLoader.loadConfig();
    }

    @Override
    public void onDisable() {

    }

    public static CraftyCrates getInstance() {
        return Instance;
    }

    public static String colorize(String string) {
        return IridiumColorAPI.process(string);
    }
}
