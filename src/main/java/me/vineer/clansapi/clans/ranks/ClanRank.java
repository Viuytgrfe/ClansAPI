package me.vineer.clansapi.clans.ranks;

import me.vineer.economyapi.money.MoneyType;

public enum ClanRank {
    BEGINNER("beginner"),
    PLAYER("player"),
    ELDER("elder"),
    VICE_PRESIDENT("vice_president"),
    PRESIDENT("president");

    private final String name;

    ClanRank(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public String translateToRus() {
        if(name.equals("president")) {
            return "Глава";
        } else if(name.equals("vice_president")) {
            return "Зам. Глава";
        } else if(name.equals("elder")) {
            return "Старейшина";
        } else if (name.equals("player")) {
            return "Участник";
        } else if (name.equals("beginner")) {
            return "Новичёк";
        }
        return name;
    }

    public static ClanRank getEnum(String value) {
        ClanRank[] var1 = values();
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            ClanRank v = var1[var3];
            if (v.getName().equalsIgnoreCase(value)) {
                return v;
            }
        }

        throw new IllegalArgumentException();
    }
    public ClanRank next() {
        return values()[(this.ordinal() + 1) % values().length];
    }

    public ClanRank previous() {
        return values()[(this.ordinal() - 1 + values().length) % values().length];
    }
}