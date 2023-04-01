package me.vineer.clansapi.clan.war.maps.arena;

import com.fastasyncworldedit.core.FaweAPI;
import com.ibm.icu.impl.Pair;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.World;
import lombok.ToString;
import me.vineer.clansapi.ClansAPI;
import me.vineer.clansapi.clan.war.maps.MapType;
import me.vineer.clansapi.clan.war.maps.random.RandomEnumGenerator;
import me.vineer.clansapi.clan.war.teams.ClanTeam;
import me.vineer.clansapi.config.arena.ArenaConfig;
import me.vineer.clansapi.config.arena.ArenaConfigData;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.io.Console;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
@ToString
public class Arena {
    private final MapType map;
    private ClanTeam firstTeam;
    private ClanTeam secondTeam;

    private Location arenaLocation;

    private World world = FaweAPI.getWorld(ArenaConfig.data.world);
    private EditSession session;

    public Arena() {
        map = new RandomEnumGenerator<>(MapType.class).randomEnum();
    }

    public Arena(ClanTeam firstTeam, ClanTeam secondTeam) {
        map = new RandomEnumGenerator<>(MapType.class).randomEnum();
        this.firstTeam = firstTeam;
        this.secondTeam = secondTeam;
    }

    public Location getArenaLocation() {
        return arenaLocation;
    }

    public ClanTeam getFirstTeam() {
        return firstTeam;
    }

    public void setFirstTeam(ClanTeam firstTeam) {
        this.firstTeam = firstTeam;
    }

    public ClanTeam getSecondTeam() {
        return secondTeam;
    }

    public void setSecondTeam(ClanTeam secondTeam) {
        this.secondTeam = secondTeam;
    }

    public MapType getMap() {
        return map;
    }

    public void generateMap(Location location) {
        this.arenaLocation = location;
        /*Bukkit.getScheduler().runTaskAsynchronously(ClansAPI.getPlugin(), new Runnable() {
            @Override
            public void run() {*/
                Clipboard clipboard = null;
                try {
                    clipboard = FaweAPI.load(map.getFile());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Clipboard finalClipboard = clipboard;
                arenaLocation.setY(257 - finalClipboard.getHeight());
                session = clipboard.paste(world, new BlockVector3() {
                    @Override
                    public int getX() {
                        return arenaLocation.getBlockX();
                    }
                    @Override
                    public int getY() {
                        return arenaLocation.getBlockY();
                    }
                    @Override
                    public int getZ() {
                        return arenaLocation.getBlockZ();
                    }
                }, true, false, null);
                System.out.println("generatedMap at: " + arenaLocation);
            /*}
        });*/
        /*Bukkit.getScheduler().runTaskLater(ClansAPI.getPlugin(), () -> {
            try (EditSession Session = WorldEdit.getInstance().newEditSession(world)) {
                session.undo(Session);
            }
        }, 20 * 30);*/
    }

    public void teleportTeams() {
        ArenaConfigData.Spawn firstSpawn = ArenaConfig.data.getFromName(map.name()).firstSpawn;
        ArenaConfigData.Spawn secondSpawn = ArenaConfig.data.getFromName(map.name()).secondSpawn;
        Location firstLocation = new Location(Bukkit.getWorld(ArenaConfig.data.world), firstSpawn.x + arenaLocation.getBlockX() + 0.5, firstSpawn.y + arenaLocation.getBlockY(), firstSpawn.z + arenaLocation.getBlockZ() + 0.5);
        Location secondLocation = new Location(Bukkit.getWorld(ArenaConfig.data.world), secondSpawn.x + arenaLocation.getBlockX() + 0.5, secondSpawn.y + arenaLocation.getBlockY(), secondSpawn.z + arenaLocation.getBlockZ() + 0.5);
        Location finalFirstLocation = firstLocation;
        firstTeam.getTeam().forEach(clanPlayer -> {
            Player player = Bukkit.getPlayer(clanPlayer.getPlayerName());
            if(player != null) {
                Bukkit.getScheduler().runTask(ClansAPI.getPlugin(), () -> {
                    player.teleport(finalFirstLocation);
                    System.out.println("teleported player " + player.getName() + " to: " + finalFirstLocation);
                });
            } else {
                System.out.println(clanPlayer.getPlayerName() + " is null!");
            }
        });

        Location finalSecondLocation = secondLocation;
        secondTeam.getTeam().forEach(clanPlayer -> {
            Player player = Bukkit.getPlayer(clanPlayer.getPlayerName());
            if(player != null) {
                Bukkit.getScheduler().runTask(ClansAPI.getPlugin(), () -> {
                    player.teleport(finalSecondLocation);
                    System.out.println("teleported player " + player.getName() + " to: " + finalSecondLocation);
                });
            } else {
                System.out.println(clanPlayer.getPlayerName() + " is null!");
            }
        });

    }

    public List<Player> getPlayers() {
        List<Player> players = new ArrayList<>();
        players.addAll(getFirstTeamPlayers());
        players.addAll(getSecondTeamPlayers());
        return players;
    }

    public List<Player> getFirstTeamPlayers() {
        List<Player> players = new ArrayList<>();
        firstTeam.getTeam().forEach(clanPlayer -> {
            Player player = Bukkit.getPlayer(clanPlayer.getPlayerName());
            if(player != null) players.add(player);
        });
        return players;
    }

    public List<Player> getSecondTeamPlayers() {
        List<Player> players = new ArrayList<>();
        if(secondTeam == null) return players;
        secondTeam.getTeam().forEach(clanPlayer -> {
            Player player = Bukkit.getPlayer(clanPlayer.getPlayerName());
            if(player != null) players.add(player);
        });
        return players;
    }
}
