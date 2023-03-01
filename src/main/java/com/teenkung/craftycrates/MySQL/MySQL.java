package com.teenkung.craftycrates.MySQL;

import com.teenkung.craftycrates.CraftyCrates;
import org.bukkit.Bukkit;

import java.sql.*;

import static com.teenkung.craftycrates.CraftyCrates.colorize;

public class MySQL {

    private Connection connection;

    public void Connect() throws SQLException {
        if (!isConnected()) {
            connection = DriverManager.getConnection(
                    "jdbc:mysql://" + CraftyCrates.getInstance().getConfig().getString("MySQL.Host") + ":" + CraftyCrates.getInstance().getConfig().getString("MySQL.Port")
                            + "/" + CraftyCrates.getInstance().getConfig().getString("MySQL.Database") + "?useSSL=false&autoReconnect=true",
                    CraftyCrates.getInstance().getConfig().getString("MySQL.User"),
                    CraftyCrates.getInstance().getConfig().getString("MySQL.Password")
            );
        }
    }

    public Connection getConnection() { return connection; }

    public void Disconnect() {
        if (isConnected()) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean isConnected() {
        return connection != null;
    }

    public void createTable() {
        System.out.println(colorize("&dChecking MySQL tables..."));
        createBannerPlayerDataTable();
        createLogsTable();
    }

    private void createBannerPlayerDataTable() {
        Bukkit.getScheduler().runTaskAsynchronously(CraftyCrates.getInstance(), () ->{
            try {
                Statement statement = connection.createStatement();
                statement.execute("CREATE TABLE IF NOT EXISTS `CraftyCrates_PlayerBannerData` ("
                        + "`ID` INT NOT NULL AUTO_INCREMENT, "
                        + "`UUID` VARCHAR(50 ) NOT NULL DEFAULT '', "
                        + "`BannerID` VARCHAR(50) NOT NULL DEFAULT '', "
                        + "`TotalRolls` INT NOT NULL DEFAULT '0', "
                        + "`CurrentRolls` INT NOT NULL DEFAULT '0', "
                        + "PRIMARY KEY (`ID`)"
                        + ") "
                        + "COLLATE='utf8_general_ci';");
                statement.close();
                System.out.println(colorize("&aSuccessfully Loaded PlayerBannerData Table!"));
            } catch (SQLException e) {
                System.out.println(colorize("&4Unable to create Table PlayerBannerData: " + e));
            }
        });
    }

    private void createLogsTable() {
        Bukkit.getScheduler().runTaskAsynchronously(CraftyCrates.getInstance(), () ->{
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS `craftycrates_logs` ( " +
                    "`ID` INT NOT NULL AUTO_INCREMENT, " +
                    "`UUID` VARCHAR(50) NOT NULL DEFAULT '' COLLATE 'utf8_general_ci', " +
                    "`BannerID` VARCHAR(50) NOT NULL DEFAULT '' COLLATE 'utf8_general_ci', " +
                    "`Data` LONGTEXT NOT NULL DEFAULT '' COLLATE 'utf8_general_ci', " +
                    "`Date` DATETIME NOT NULL DEFAULT '1000-01-01 00:00:00', " +
                    "PRIMARY KEY (`ID`) " +
                    ") " +
                    "COLLATE='utf8_general_ci';");
            statement.close();
            System.out.println(colorize("&aSuccessfully Loaded Logs Table!"));
        } catch (SQLException e) {
            System.out.println(colorize("&4Unable to create Table Logs: " + e));
        }
        });
    }

    public void startSendDummyData() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(CraftyCrates.getInstance(), () ->  {
            if (CraftyCrates.getInstance().getConfig().getBoolean("MySQL.DummyData.Display")) {
                System.out.println("Sending Dummy Data to prevent Database Timed out!");
            }
            try {
                PreparedStatement statement = connection.prepareStatement("REPLACE INTO craftycrates_playerbannerdata (ID, UUID, BannerID, TotalRolls, CurrentRolls) VALUES (1, 'Dummy_Data', RAND(), 0, 0);");
                statement.executeUpdate();
                statement.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }, 20, CraftyCrates.getInstance().getConfig().getInt("MySQL.DummyData.SendRate", 300)* 20L);
    }

}
