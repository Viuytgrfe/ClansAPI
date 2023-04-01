package me.vineer.clansapi.clan.war.maps.random;

import me.vineer.clansapi.clan.war.teams.ClanTeam;

import java.util.Collections;
import java.util.List;

public class RandomTeamsDistributor {
    private List<ClanTeam> teams;
    public RandomTeamsDistributor(List<ClanTeam> teams) {
        this.teams = teams;
    }

    public List<ClanTeam> randomize() {
        Collections.shuffle(teams);
        return teams;
    }
}
