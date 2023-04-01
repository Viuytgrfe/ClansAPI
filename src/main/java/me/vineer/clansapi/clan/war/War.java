package me.vineer.clansapi.clan.war;

import me.vineer.clansapi.ClansAPI;
import me.vineer.clansapi.clan.player.ClanPlayer;
import me.vineer.clansapi.clan.war.exceptions.NotEnougthTeamsException;
import me.vineer.clansapi.clan.war.maps.arena.Arena;
import me.vineer.clansapi.clan.war.maps.arena.ArenaController;
import me.vineer.clansapi.clan.war.teams.ClanTeam;
import me.vineer.clansapi.config.main.MainConfig;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class War {
    public static boolean enabled = true;

    public static WarState state = null;

    public static boolean started = false;
    private static List<Arena> arenas = new ArrayList<>();
    private static List<ClanTeam> teams = new ArrayList<>();

    public static long firstBossBarTick = 0;
    public static long secondBossBarTick = 0;
    public static long thirdBossBarTick = 0;
    public static long gameBossBarTick = 0;

    public static ArenaController controller;

    public static final Runnable mainThread = () -> {
        while (firstBossBarTick < MainConfig.data.clanwarFirstDelay) {
            try {
                TimeUnit.MILLISECONDS.sleep(50);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            firstBossBarTick++;
        }
        prepareWar();
        state = WarState.SECOND_INIT;
        if(controller != null) {
            while (secondBossBarTick < MainConfig.data.clanwarSecondDelay) {
                try {
                    TimeUnit.MILLISECONDS.sleep(50);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                secondBossBarTick++;
            }
            controller.generateArenas();
            state = WarState.THIRD_INIT;
            while (thirdBossBarTick < MainConfig.data.clanwarThirdDelay) {
                try {
                    TimeUnit.MILLISECONDS.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                thirdBossBarTick++;
            }
            controller.teleportPlayersToArenas();
            state = WarState.GAME;
            while (gameBossBarTick < MainConfig.data.clanwarGameTime) {
                try {
                    TimeUnit.MILLISECONDS.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                gameBossBarTick++;
            }
        }
        War.endWar();
    };

    public static void startMainTimer() {
        firstBossBarTick = 0;
        secondBossBarTick = 0;
        thirdBossBarTick = 0;
        gameBossBarTick = 0;
        War.state = WarState.FIRST_INIT;
        Bukkit.getScheduler().runTaskAsynchronously(ClansAPI.getPlugin(), mainThread);
    }

    private static ClanTeam teamWithoutPair = null;

    public static List<ClanTeam> getFullTeams() {
        return teams.stream()
                .filter(ClanTeam::isFull)
                .collect(Collectors.toList());
    }

    public static void prepareWar() {
        try {
            controller = new ArenaController();
        } catch (NotEnougthTeamsException e) {
            arenas
                    .forEach(arena -> {
                        arena.getPlayers()
                                .forEach(player -> {
                                    player.sendMessage(ChatColor.YELLOW + "[CA] " + ChatColor.RED + "К сожалению, собралось недостаточно команд для начала игры.");
                                });
                        });
        }
    }

    public static ClanTeam getTeam(String name) {
        for(ClanTeam tm : teams) {
            if (tm.getClanName().equals(name)) {
                return tm;
            }
        }
        return null;
    }

    public static void addTeam(ClanTeam team) {
        teams.add(team);
    }

    public static void addPlayerToTeam(ClanPlayer player, String clanName) {
        teams.stream()
                .filter(team -> team.getClanName().equals(clanName))
                .toList().get(0).addPlayer(player);
    }

    public static void removePlayerFromTeam(ClanPlayer player, String clanName) {
        teams.stream()
                .filter(team -> team.getClanName().equals(clanName))
                .toList().get(0).removePlayer(player);
    }

    public static boolean removeTeam(ClanTeam team) {
        return teams.remove(team);
    }

    public static void prepareArenas() throws NotEnougthTeamsException {
        List<ClanTeam> fullTeams = getFullTeams();
        System.out.println("fullTeams: " + fullTeams.stream().map(Object::toString).collect(Collectors.joining(", ")));
        if(fullTeams.size() < 2) {
            arenas = pushTeamsToArenas(fullTeams);
            throw new NotEnougthTeamsException();
        }
        if(fullTeams.size() % 2 == 1) {
            teamWithoutPair = teams.get(teams.size()-1);
            fullTeams.remove(fullTeams.size()-1);
        }
        arenas = pushTeamsToArenas(fullTeams);
        System.out.println("arenasToGenerate: " + arenas.stream().map(Object::toString).collect(Collectors.joining(", ")));
    }

    private static List<Arena> pushTeamsToArenas(List<ClanTeam> teams) {
        List<Arena> arenaList = new ArrayList<>();
        if(teams.size() < 2) {
            arenaList.add(new Arena(teams.get(0), null));
            return arenaList;
        }
        for(int i = 0; i < teams.size(); i += 2)
            arenaList.add(new Arena(teams.get(i), teams.get(i+1)));

        return arenaList;
    }

    public static List<Arena> getArenasToGenerate() throws NotEnougthTeamsException {
        if(arenas.size() == 0) prepareArenas();
        return arenas;
    }

    public static void endWar() {
        started = false;
        state = null;
        teams = new ArrayList<>();
        arenas = new ArrayList<>();
    }
}
