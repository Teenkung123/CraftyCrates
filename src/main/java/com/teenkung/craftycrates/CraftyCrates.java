package com.teenkung.craftycrates;

import com.iridium.iridiumcolorapi.IridiumColorAPI;
import com.teenkung.craftycrates.commands.CommandHandler;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class CraftyCrates extends JavaPlugin {

    private static CraftyCrates Instance;
    @Override
    public void onEnable() {
        Instance = this;
        ConfigLoader.loadConfig();
        Objects.requireNonNull(getCommand("craftycrates")).setExecutor(new CommandHandler());

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
