package me.vineer.clansapi.database;

import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;

public class Database {
    public static String url = "jdbc:sqlite:Clans.db";
    public static Connection con;
    static ConsoleCommandSender console = Bukkit.getConsoleSender();

    public Database() {
    }

    public static void connect() {
        if (!isConnected()) {
            try {
                Properties connectionProperties = new Properties();
                con = DriverManager.getConnection(url, connectionProperties);
            } catch (SQLException var1) {
                var1.printStackTrace();
            }
        }
    }

    public static void disconnect() {
        if (isConnected()) {
            try {
                con.close();
            } catch (SQLException var1) {
                var1.printStackTrace();
            }
        }

    }

    public static boolean isConnected() {
        return con != null;
    }

    public static Connection getConnection() {
        return con;
    }

    public static void initDatabase() {
        connect();
        try {
            PreparedStatement ps = getConnection().prepareStatement("PRAGMA foreign_keys = ON;");
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            PreparedStatement ps = getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS ClanHomes (\n" +
                    "\t\"HomeId\"\tINTEGER NOT NULL UNIQUE,\n" +
                    "\t\"ClanId\"\tINTEGER NOT NULL UNIQUE,\n" +
                    "\t\"X\"\tINTEGER NOT NULL,\n" +
                    "\t\"Y\"\tdouble NOT NULL,\n" +
                    "\t\"Z\"\tINTEGER NOT NULL,\n" +
                    "\t\"WORLD\"\tTEXT NOT NULL,\n" +
                    "\tPRIMARY KEY(HomeId AUTOINCREMENT),\n" +
                    "\tFOREIGN KEY(ClanId) REFERENCES Clans(ClanId) ON DELETE CASCADE\n" +
                    ");");
            ps.executeUpdate();
        } catch (SQLException var1) {
            var1.printStackTrace();
        }

        try {
            PreparedStatement ps = getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS Clans (\n" +
                    "\t\"ClanId\"\tINTEGER NOT NULL UNIQUE,\n" +
                    "\t\"Name\"\tTEXT NOT NULL UNIQUE,\n" +
                    "\t\"Initial\"\tTEXT NOT NULL UNIQUE,\n" +
                    "\t\"Level\"\tINTEGER NOT NULL,\n" +
                    "\t\"XP\"\tINTEGER NOT NULL,\n" +
                    "\t\"Balance\"\tINTEGER NOT NULL,\n" +
                    "\t\"Owner\"\tTEXT NOT NULL UNIQUE,\n" +
                    "\tPRIMARY KEY(ClanId)\n" +
                    ");");
            ps.executeUpdate();
        } catch (SQLException var1) {
            var1.printStackTrace();
        }

        try {
            PreparedStatement ps = getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS ClansPlayers (\n" +
                    "\t\"ClanId\"\tINTEGER NOT NULL,\n" +
                    "\t\"Name\"\tTEXT NOT NULL UNIQUE,\n" +
                    "\t\"Rank\"\tTEXT NOT NULL,\n" +
                    "\tFOREIGN KEY(ClanId) REFERENCES Clans(ClanId) ON DELETE CASCADE\n" +
                    ");");
            ps.executeUpdate();
        } catch (SQLException var1) {
            var1.printStackTrace();
        }

        try {
            PreparedStatement ps = getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS ClansRequests (\n" +
                    "\t\"ClanId\"\tINTEGER NOT NULL,\n" +
                    "\t\"Name\"\tTEXT NOT NULL,\n" +
                    "\tFOREIGN KEY(ClanId) REFERENCES Clans(ClanId) ON DELETE CASCADE\n" +
                    ");");
            ps.executeUpdate();
        } catch (SQLException var1) {
            var1.printStackTrace();
        }

    }
}
