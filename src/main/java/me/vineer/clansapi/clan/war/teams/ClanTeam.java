package me.vineer.clansapi.clan.war.teams;

import lombok.ToString;
import me.vineer.clansapi.clan.player.ClanPlayer;
import me.vineer.clansapi.database.ClansController;

import java.util.ArrayList;
import java.util.List;
@ToString
public class ClanTeam {
    private String clanName;
    private List<ClanPlayer> team = new ArrayList<>();
    private List<ClanPlayer> requests = new ArrayList<>();

    public ClanTeam(String clanName) {
        this.clanName = clanName;
        team.add(ClansController.getClanPresident(clanName));
    }

    public List<ClanPlayer> getTeam() {
        return team;
    }

    public List<ClanPlayer> getRequests() {
        return requests;
    }

    public String getClanName() {
        return clanName;
    }

    public void addPlayer(ClanPlayer player) {
        if(requests.remove(player)) team.add(player);
    }
    public void removePlayer(ClanPlayer player) {
        team.remove(player);
        requests.add(player);
    }

    public void addRequest(ClanPlayer player) {
        requests.add(player);
    }

    public boolean isFull() {
        return team.size() >= 1; // TODO изменить назад на 3 игрока
    }

    public boolean canBeFull() {
        return team.size()+requests.size() >= 3;
    }
}
