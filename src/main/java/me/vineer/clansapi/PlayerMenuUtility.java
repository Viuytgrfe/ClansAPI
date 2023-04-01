package me.vineer.clansapi;

import org.bukkit.entity.Player;
public class PlayerMenuUtility {
    private Player owner;
    private String playerFromRequest;

    public String getClanName() {
        return clanName;
    }

    public void setClanName(String clanName) {
        this.clanName = clanName;
    }

    private String clanName;
    public PlayerMenuUtility(Player owner) {
        this.owner = owner;
    }

    public String getPlayerFromRequest() {
        return playerFromRequest;
    }

    public void setPlayerFromRequest(String playerFromRequest) {
        this.playerFromRequest = playerFromRequest;
    }

    public void setOwner(Player owner) {
        this.owner = owner;
    }

    public Player getOwner() {
        return owner;
    }
}
