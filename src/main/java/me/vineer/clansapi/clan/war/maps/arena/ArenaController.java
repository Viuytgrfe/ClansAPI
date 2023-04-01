package me.vineer.clansapi.clan.war.maps.arena;

import me.vineer.clansapi.clan.war.War;
import me.vineer.clansapi.clan.war.exceptions.NotEnougthTeamsException;
import me.vineer.clansapi.clan.war.fight.Fight;
import me.vineer.clansapi.config.arena.ArenaConfig;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ArenaController {
    private List<Arena> arenasToGenerate;

    private List<Fight> generatedArenas;

    public static Location freeLocation;

    public ArenaController() throws NotEnougthTeamsException {
        arenasToGenerate = War.getArenasToGenerate();
        generatedArenas = new ArrayList<>();
        freeLocation = new Location(Bukkit.getWorld(ArenaConfig.data.world), 0, 0, 0);

    }

    public void generateArena(Arena arena) {
        arena.generateMap(freeLocation.clone());
        generatedArenas.add(new Fight(arena));
        try {
            freeLocation = freeLocation.clone().add(arena.getMap().getSchematic().getWidth()+1, 0, 0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void generateArenas() {
        arenasToGenerate.forEach(this::generateArena);
    }

    public void teleportPlayersToArenas() {
        generatedArenas.forEach(fight -> {
            fight.arena.teleportTeams();
        });
    }
}