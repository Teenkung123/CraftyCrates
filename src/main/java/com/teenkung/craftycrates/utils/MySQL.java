package com.teenkung.craftycrates.utils;

import com.teenkung.craftycrates.CraftyCrates;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

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

}
