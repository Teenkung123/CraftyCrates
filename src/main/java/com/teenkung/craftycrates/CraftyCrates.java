package com.teenkung.craftycrates;

import com.iridium.iridiumcolorapi.IridiumColorAPI;
import com.teenkung.craftycrates.GUIs.InfoGUI;
import com.teenkung.craftycrates.commands.CommandTabComplete;
import com.teenkung.craftycrates.events.InfoEvent;
import com.teenkung.craftycrates.MySQL.PlayerDataManager;
import com.teenkung.craftycrates.commands.CommandHandler;
import com.teenkung.craftycrates.events.*;
import com.teenkung.craftycrates.MySQL.MySQL;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class CraftyCrates extends JavaPlugin {

    private static CraftyCrates Instance;
    private static MySQL database;
    private static Connection connection;
    @Override
    public void onEnable() {
        Instance = this;
        ConfigLoader.loadConfig();
        Objects.requireNonNull(getCommand("craftycrates")).setExecutor(new CommandHandler());
        Objects.requireNonNull(getCommand("craftycrates")).setTabCompleter(new CommandTabComplete());

        Bukkit.getScheduler().runTaskAsynchronously(CraftyCrates.getInstance(), () -> {
            System.out.println(colorize(getConfig().getString("Languages.MySQL.connecting")));
            database = new MySQL();
            try {
                database.Connect();
                connection = database.getConnection();
                System.out.println(colorize(getConfig().getString("Languages.MySQL.connected")));

                Bukkit.getScheduler().runTaskLaterAsynchronously(this, () -> {
                    database.createTable();
                    database.startSendDummyData();
                    Bukkit.getScheduler().runTask(this, () -> {
                        for (Player player : Bukkit.getOnlinePlayers()) {
                            if (!JoinEvent.getDataManager().containsKey(player)) {
                                JoinEvent.getDataManager().put(player, new PlayerDataManager(player));
                            }
                        }
                    });
                }, 20);
            } catch (SQLException e) {
                System.out.println(colorize(getConfig().getString("Languages.MySQL.unable")));
                e.printStackTrace();
                Bukkit.getPluginManager().disablePlugin(this);
            }
        });

        InfoGUI.loadGUIs();

        System.out.println(colorize(getConfig().getString("Languages.others.register-event-handler")));
        Bukkit.getPluginManager().registerEvents(new JoinEvent(), this);
        Bukkit.getPluginManager().registerEvents(new QuitEvent(), this);
        Bukkit.getPluginManager().registerEvents(new NEIEvent(), this);
        Bukkit.getPluginManager().registerEvents(new InfoEvent(), this);
        Bukkit.getPluginManager().registerEvents(new LogsEvent(), this);
        System.out.println(colorize(getConfig().getString("Languages.others.success-event-handler")));
    }

    @Override
    public void onDisable() {
        database.Disconnect();
    }

    public static CraftyCrates getInstance() {
        return Instance;
    }
    public static Connection getConnection() { return connection; }

    public static String colorize(String string) {
        return IridiumColorAPI.process(string);
    }

    public static ArrayList<String> colorizeList(List<String> list) {
        ArrayList<String> result = new ArrayList<>();
        for (String s : list) {
            result.add(colorize(s));
        }
        return result;
    }
}
