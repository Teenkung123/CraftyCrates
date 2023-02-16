package com.teenkung.craftycrates;

import com.iridium.iridiumcolorapi.IridiumColorAPI;
import com.teenkung.craftycrates.commands.CommandHandler;
import com.teenkung.craftycrates.utils.MySQL;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.util.Objects;

public final class CraftyCrates extends JavaPlugin {

    private static CraftyCrates Instance;
    private static MySQL database;
    @Override
    public void onEnable() {
        Instance = this;
        ConfigLoader.loadConfig();
        Objects.requireNonNull(getCommand("craftycrates")).setExecutor(new CommandHandler());

        database = new MySQL();
        try {
            database.Connect();
        } catch (SQLException e) {
            System.out.println(colorize("&4Could not connect to MySQL server. Pleasse check your configuration settings.\nError: "+e));
        }

    }

    @Override
    public void onDisable() {

    }

    public static CraftyCrates getInstance() {
        return Instance;
    }
    public static MySQL getConnection() { return database; }

    public static String colorize(String string) {
        return IridiumColorAPI.process(string);
    }
}
