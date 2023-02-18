package com.teenkung.craftycrates.MySQL;

import com.teenkung.craftycrates.CraftyCrates;
import org.bukkit.Bukkit;

import java.sql.*;

import static com.teenkung.craftycrates.CraftyCrates.colorize;

public class MySQL {

    private Connection connection;

    public void Connect() throws SQLException {
        connection = DriverManager.getConnection(
                "jdbc:mysql://" + CraftyCrates.getInstance().getConfig().getString("MySQL.Host") + ":" + CraftyCrates.getInstance().getConfig().getString("MySQL.Port")
                        + "/" + CraftyCrates.getInstance().getConfig().getString("MySQL.Database") + "?useSSL=false",
                CraftyCrates.getInstance().getConfig().getString("MySQL.User"),
                CraftyCrates.getInstance().getConfig().getString("MySQL.Password")
        );
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
                        + "`UUID` VARCHAR(50) NOT NULL DEFAULT '', "
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
                    "`ItemNBT` LONGTEXT NOT NULL DEFAULT '' COLLATE 'utf8_general_ci', " +
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

}