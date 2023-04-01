package me.vineer.clansapi.config.main;

import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;


@NoArgsConstructor
@ToString
public class MainConfigData {
    public int clanPrice;
    public long clanwarFirstDelay;
    public long clanwarSecondDelay;
    public long clanwarThirdDelay;
    public long clanwarGameTime;
    public ClanwarRewards clanwarRewards;
    public boolean clanFight;
}


@NoArgsConstructor
@ToString
class ClanwarRewards {
    public Lvl1 lvl1;
    public Lvl2 lvl2;
    public Lvl3 lvl3;
    public Lvl4 lvl4;
    public Lvl5 lvl5;
}


@NoArgsConstructor
@ToString
class Lvl5 {
    public Win win;
    public Lose lose;
}


@NoArgsConstructor
@ToString
class Lvl4 {
    public Win win;
    public Lose lose;
}


@NoArgsConstructor
@ToString
class Lvl3 {
    public Win win;
    public Lose lose;
}


@NoArgsConstructor
@ToString
class Lvl2 {
    public Win win;
    public Lose lose;
}


@NoArgsConstructor
@ToString
class Lvl1 {
    public Win win;
    public Lose lose;
}


@NoArgsConstructor
@ToString
class Lose {
    public int money;
    public int points;
}


@NoArgsConstructor
@ToString
class Win {
    public int money;
    public int points;
}