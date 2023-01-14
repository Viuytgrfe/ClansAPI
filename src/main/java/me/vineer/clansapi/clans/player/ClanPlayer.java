package me.vineer.clansapi.clans.player;

import me.vineer.clansapi.clans.ranks.ClanRank;
import me.vineer.clansapi.database.ClansController;

public class ClanPlayer {
    private final String playerName;
    private ClanRank rank;

    public ClanPlayer(String playerName, ClanRank rank) {
        this.playerName = playerName;
        this.rank = rank;
    }

    public String getPlayerName() {
        return playerName;
    }

    public ClanRank getRank() {
        return rank;
    }

    public void updateRank() {
        if(rank == ClanRank.BEGINNER)rank = ClanRank.PLAYER;
        else if(rank == ClanRank.PLAYER)rank = ClanRank.ELDER;
        else if(rank == ClanRank.ELDER)rank = ClanRank.VICE_PRESIDENT;
        else if(rank == ClanRank.VICE_PRESIDENT)rank = ClanRank.PRESIDENT;
        ClansController.setPlayerRank(rank, playerName);
    }

    public void decreaseRank() {
        if(rank == ClanRank.PLAYER)rank = ClanRank.BEGINNER;
        else if(rank == ClanRank.ELDER)rank = ClanRank.PLAYER;
        else if (rank == ClanRank.VICE_PRESIDENT) rank = ClanRank.ELDER;
        ClansController.setPlayerRank(rank, playerName);
    }
}
