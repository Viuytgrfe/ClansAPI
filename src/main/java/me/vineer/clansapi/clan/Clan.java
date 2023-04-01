package me.vineer.clansapi.clan;

import me.vineer.clansapi.clan.player.ClanPlayer;
import me.vineer.clansapi.clan.ranks.ClanRank;
import me.vineer.clansapi.database.ClansController;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

public class Clan {
    private final String clanName;
    private final String clanInitial;
    private int clanLevel = 1;
    private Location clanHome = null;
    private int clanXP = 0;
    private int clanBalance = 0;
    private ClanPlayer clanOwner = null;
    private List<ClanPlayer> players = new ArrayList<>();
    private List<String> requests = new ArrayList<>();

    public String getClanName() {
        return clanName;
    }

    public String getClanInitial() {
        return clanInitial;
    }

    public int getClanLevel() {
        return clanLevel;
    }

    public Location getClanHome() {
        return clanHome;
    }

    public int getClanXP() {
        return clanXP;
    }

    public int getClanBalance() {
        return clanBalance;
    }

    public List<ClanPlayer> getPlayers() {
        return players;
    }

    public List<String> getRequests() {
        return requests;
    }

    public Clan(String clanName, String clanInitial, String owner, int clanBalance, int clanXP) {
        this.clanName = clanName;
        this.clanInitial = clanInitial;
        this.clanOwner = new ClanPlayer(owner, ClanRank.PRESIDENT);
        ClansController.addPlayerToClan(owner, clanName, ClanRank.PRESIDENT);
        this.clanBalance = clanBalance;
        this.clanXP = clanXP;
    }

    public void addXP(int amount) {
        clanXP += amount;
        ClansController.addClanXP(amount, clanName);
    }

    public void removeXP(int amount) {
        clanXP -= amount;
        ClansController.addClanXP(-amount, clanName);
    }

    public void setClanHome(Location clanHome) {
        this.clanHome = clanHome;
        ClansController.setClanHome(clanName, clanHome);
    }

    public void setClanLevel(int level) {
        this.clanLevel = level;
        ClansController.setClanLevel(level, clanName);
    }

    public void addMoney(int amount) {
        clanBalance += amount;
        ClansController.addClanMoney(amount, clanName);
    }

    public String getClanOwner() {
        return clanOwner.getPlayerName();
    }

    public void removeMoney(int amount) {
        clanBalance -= amount;
        ClansController.addClanMoney(-amount, clanName);
    }

    public void addPlayerToClan(String name) {
        for (ClanPlayer player:players) if(player.getPlayerName().equals(name)) return;
        players.add(new ClanPlayer(name, ClanRank.BEGINNER));
        ClansController.addPlayerToClan(name, clanName);
    }

    public void addPlayerToClan(ClanPlayer player) {
        if(!players.contains(player))players.add(player);
        ClansController.addPlayerToClan(player.getPlayerName(), clanName, player.getRank());
    }

    public void addRequestToClan(String name) {
        if(!requests.contains(name)) requests.add(name);
        ClansController.addRequestToClan(name, clanName);
    }

    public void removePlayerFromClan(String name) {
        players.removeIf(player -> player.getPlayerName().equals(name));
        ClansController.removePlayerFromClan(name);
        if(players.size() == 0) ClansController.deleteClan(name);
    }

    public void removeRequestFromClan(String name) {
        requests.removeIf(player -> player.equals(name));
        ClansController.removeRequestFromClan(name);
    }
}
