package me.vineer.clansapi.database;

import me.vineer.clansapi.ClansAPI;
import me.vineer.clansapi.clan.Clan;
import me.vineer.clansapi.clan.player.ClanPlayer;
import me.vineer.clansapi.clan.ranks.ClanRank;
import me.vineer.clansapi.heads.NickController;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ClansController {

    public static Clan createClan(String name, String initial, String owner) {
        if(isClan(name)) return getClan(name);
        try {
            PreparedStatement ps = Database.getConnection().prepareStatement("INSERT INTO Clans(Name, Initial, Level, XP, Balance, Owner) VALUES (?, ?, ?, ?, ?, ?)");
            ps.setString(1, name);
            ps.setString(2, initial);
            ps.setInt(3, 1);
            ps.setInt(4, 0);
            ps.setInt(5, 0);
            ps.setString(6, owner);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new Clan(name, initial, owner, 0, 0);
    }

    public static boolean isHome(String name) {
        if(!isClan(name)) return false;
        try {
            PreparedStatement ps = Database.getConnection().prepareStatement("SELECT * FROM ClanHomes WHERE ClanId = ?");
            ps.setInt(1, getClanId(name));
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static Location getClanHome(String name) {
        if(!isHome(name)) return null;
        try {
            PreparedStatement ps = Database.getConnection().prepareStatement("SELECT * FROM ClanHomes WHERE ClanId = ?");
            ps.setInt(1, getClanId(name));
            ResultSet rs = ps.executeQuery();
            return new Location(Bukkit.getWorld(rs.getString("WORLD")), rs.getInt("X") + 0.5, rs.getDouble("Y"), rs.getInt("Z") + 0.5);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void deleteClanHome(String name) {
        try {
            PreparedStatement ps = Database.getConnection().prepareStatement("DELETE FROM ClanHomes WHERE ClanId = ?");
            ps.setInt(1, getClanId(name));
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void removeClan(String clanName) {
        Bukkit.getScheduler().runTaskAsynchronously(ClansAPI.plugin, new Runnable() {
            @Override
            public void run() {
                if(!isClan(clanName)) return;
                Clan clan = getClan(clanName);
                for (ClanPlayer player : clan.getPlayers()) {
                    Player p = Bukkit.getPlayer(player.getPlayerName());
                    if(p != null && player.getRank() != ClanRank.PRESIDENT) {
                        NickController.leaveFromClan(player.getPlayerName());
                        p.sendMessage(ChatColor.YELLOW + "[CA] " + ChatColor.RED + "Клан в котором вы состояли был удалён!");
                    }
                    else if (player.getRank() == ClanRank.PRESIDENT) {
                        NickController.leaveFromClan(player.getPlayerName());
                        p.sendMessage(ChatColor.YELLOW + "[CA] " + ChatColor.GREEN + "Клан " + ChatColor.GOLD + clanName + ChatColor.GREEN + " был успешно удалён!");
                    }
                }
                for (String name : clan.getRequests()) {
                    Player player = Bukkit.getPlayer(name);
                    if(player != null) {
                        player.sendMessage(ChatColor.YELLOW + "[CA] " + ChatColor.RED + "Клан в который вы подавали запрос был удалён!");
                    }
                }
                try {
                    PreparedStatement ps = Database.getConnection().prepareStatement("DELETE FROM Clans WHERE Name = ?");
                    ps.setString(1, clanName);
                    ps.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });

    }


    public static Clan getClan(String name) {
        Clan clan = null;
        try {
            PreparedStatement ps = Database.getConnection().prepareStatement("SELECT * FROM Clans WHERE Name = ?");
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            if(!rs.next()) return null;
            clan = new Clan(name, rs.getString("Initial"), rs.getString("Owner"), rs.getInt("Balance"), rs.getInt("XP"));
            clan.setClanLevel(rs.getInt("Level"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if(clan == null) return null;
        try {
            PreparedStatement ps = Database.getConnection().prepareStatement("SELECT ClansPlayers.Name, ClansPlayers.Rank FROM ClansPlayers INNER JOIN Clans ON ClansPlayers.ClanId = Clans.ClanId WHERE Clans.Name = ?");
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                clan.addPlayerToClan(new ClanPlayer(rs.getString("Name"), ClanRank.getEnum(rs.getString("Rank"))));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            PreparedStatement ps = Database.getConnection().prepareStatement("SELECT ClansRequests.Name FROM ClansRequests INNER JOIN Clans ON ClansRequests.ClanId = Clans.ClanId WHERE Clans.Name = ?");
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                clan.addRequestToClan(rs.getString("Name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            PreparedStatement ps = Database.getConnection().prepareStatement("SELECT ClanHomes.* FROM ClanHomes INNER JOIN Clans ON ClanHomes.ClanId = Clans.ClanId WHERE Clans.Name = ?");
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                clan.setClanHome(new Location(Bukkit.getWorld(rs.getString("WORLD")), rs.getInt("X"), rs.getDouble("Y"), rs.getInt("Z")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return clan;
    }

    public static void addClanXP(int amount, String name) {
        if(!isClan(name)) return;
        try {
            PreparedStatement ps = Database.getConnection().prepareStatement("UPDATE Clans SET XP = XP + ? WHERE Name = ?");
            ps.setInt(1, amount);
            ps.setString(2, name);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean isClan(String name) {
        try {
            PreparedStatement ps = Database.getConnection().prepareStatement("SELECT * FROM Clans WHERE Name = ?");
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean isPlayer(String player) {
        try {
            PreparedStatement ps = Database.getConnection().prepareStatement("SELECT * FROM ClansPlayers WHERE Name = ?");
            ps.setString(1, player);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String getClanOfPlayer(String player) {
        if(!isPlayer(player)) return null;
        try {
            PreparedStatement ps = Database.getConnection().prepareStatement("SELECT * FROM ClansPlayers WHERE Name = ?");
            ps.setString(1, player);
            ResultSet rs = ps.executeQuery();
            return ClansController.getClanFromId(rs.getInt("ClanId"));
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean isPresident(String player) {
        if(!isPlayer(player)) return false;
        try {
            PreparedStatement ps = Database.getConnection().prepareStatement("SELECT * FROM Clans WHERE Owner = ?");
            ps.setString(1, player);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static List<String> getListClanName() {
        List<String> list = new ArrayList<>();
        try {
            PreparedStatement ps = Database.getConnection().prepareStatement("SELECT Name FROM Clans");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(rs.getString("Name"));
            }
            return list;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<Clan> getClanList() {
        List<Clan> clans = new ArrayList<>();
        if(getListClanName() == null) return clans;
        for (String name : getListClanName()) {
            clans.add(getClan(name));
        }
        return clans;
    }

    public static ClanPlayer getPlayer(String player) {
        if(!isPlayer(player)) return null;
        try {
            PreparedStatement ps = Database.getConnection().prepareStatement("SELECT * FROM ClansPlayers WHERE Name = ?");
            ps.setString(1, player);
            ResultSet rs = ps.executeQuery();
            return new ClanPlayer(rs.getString("Name"), ClanRank.getEnum(rs.getString("Rank")));
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static ClanPlayer getClanPresident(String clan) {
        if(!isClan(clan)) return null;
        try {
            PreparedStatement ps = Database.getConnection().prepareStatement("SELECT Name FROM ClansPlayers WHERE Rank = ? AND ClanId = ?");
            ps.setString(1, ClanRank.PRESIDENT.getName());
            ps.setInt(2, getClanId(clan));
            ResultSet rs = ps.executeQuery();
            return new ClanPlayer(rs.getString("Name"), ClanRank.PRESIDENT);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean isRequest(String name, String player) {
        try {
            PreparedStatement ps = Database.getConnection().prepareStatement("SELECT * FROM ClansRequests WHERE Name = ? AND ClanId = ?");
            ps.setString(1, player);
            ps.setInt(2, getClanId(name));
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean isRequest(String player) {
        try {
            PreparedStatement ps = Database.getConnection().prepareStatement("SELECT * FROM ClansRequests WHERE Name = ?");
            ps.setString(1, player);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void setClanHome(String name, Location location) {
        if(!isClan(name)) return;
        if(!isHome(name)) {
            try {
                PreparedStatement ps = Database.getConnection().prepareStatement("INSERT INTO ClanHomes(ClanId, X, Y, Z, WORLD) VALUES (?, ?, ?, ?, ?)");
                ps.setInt(1, getClanId(name));
                ps.setInt(2, location.getBlockX());
                ps.setDouble(3, location.getY());
                ps.setInt(4, location.getBlockZ());
                ps.setString(5, location.getWorld().getName());
                ps.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            try {
                PreparedStatement ps = Database.getConnection().prepareStatement("UPDATE ClanHomes SET X = ?, Y = ?, Z = ?, WORLD = ? WHERE ClanId = ?");
                ps.setInt(1, location.getBlockX());
                ps.setDouble(2, location.getY());
                ps.setInt(3, location.getBlockZ());
                ps.setString(4, location.getWorld().getName());
                ps.setInt(5, getClanId(name));
                ps.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void setClanLevel(int level, String name) {
        if(!isClan(name)) return;
        try {
            PreparedStatement ps = Database.getConnection().prepareStatement("UPDATE Clans SET Level = ? WHERE Name = ?");
            ps.setInt(1, level);
            ps.setString(2, name);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void setPlayerRank(ClanRank rank, String player) {
        if(!isPlayer(player)) return;
        try {
            PreparedStatement ps = Database.getConnection().prepareStatement("UPDATE ClansPlayers SET Rank = ? WHERE Name = ?");
            ps.setString(1, rank.getName());
            ps.setString(2, player);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void addClanMoney(int amount, String name) {
        if(!isClan(name)) return;
        try {
            PreparedStatement ps = Database.getConnection().prepareStatement("UPDATE Clans SET Balance = Balance + ? WHERE Name = ?");
            ps.setInt(1, amount);
            ps.setString(2, name);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static int getClanId(String name) {
        try {
            PreparedStatement ps = Database.getConnection().prepareStatement("SELECT ClanId FROM Clans WHERE Name = ?");
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            return rs.getInt("ClanId");
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static String getClanFromId(int id) {
        try {
            PreparedStatement ps = Database.getConnection().prepareStatement("SELECT Name FROM Clans WHERE ClanId = ?");
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            return rs.getString("Name");
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void addPlayerToClan(String player, String name) {
        if(!isClan(name)) return;
        if(isPlayer(player)) return;
        try {
            PreparedStatement ps = Database.getConnection().prepareStatement("INSERT INTO ClansPlayers(ClanId, Name, Rank) VALUES (?, ?, ?)");
            ps.setInt(1, getClanId(name));
            ps.setString(2, player);
            ps.setString(3, ClanRank.BEGINNER.getName());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void addPlayerToClan(String player, String name, ClanRank rank) {
        if(!isClan(name)) return;
        if(isPlayer(player)) return;
        try {
            PreparedStatement ps = Database.getConnection().prepareStatement("INSERT INTO ClansPlayers(ClanId, Name, Rank) VALUES (?, ?, ?)");
            ps.setInt(1, getClanId(name));
            ps.setString(2, player);
            ps.setString(3, rank.getName());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void addRequestToClan(String player, String name) {
        if(!isClan(name)) return;
        if(isRequest(name, player)) return;
        try {
            PreparedStatement ps = Database.getConnection().prepareStatement("INSERT INTO ClansRequests(ClanId, Name) VALUES (?, ?)");
            ps.setInt(1, getClanId(name));
            ps.setString(2, player);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void removePlayerFromClan(String name) {
        try {
            PreparedStatement ps = Database.getConnection().prepareStatement("DELETE FROM ClansPlayers WHERE Name = ?");
            ps.setString(1, name);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void removeRequestFromClan(String name) {
        try {
            PreparedStatement ps = Database.getConnection().prepareStatement("DELETE FROM ClansRequests WHERE Name = ?");
            ps.setString(1, name);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void deleteClan(String name) {
        try {
            PreparedStatement ps = Database.getConnection().prepareStatement("DELETE FROM Clans WHERE Name = ?");
            ps.setString(1, name);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
