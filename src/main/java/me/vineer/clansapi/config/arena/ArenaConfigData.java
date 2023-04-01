package me.vineer.clansapi.config.arena;

import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
@NoArgsConstructor
@ToString
public class ArenaConfigData {

    public String world;
    public Arena TEST;

    public Arena getFromName(String name) {
        if(name.equals("TEST")) return TEST;
        else return null;
    }

    @NoArgsConstructor
    @ToString
    public static class Arena {
        public String schematic;
        public String name;
        public List<String> canBreak;
        public Spawn firstSpawn;
        public Spawn secondSpawn;
    }
    @NoArgsConstructor
    @ToString
    public static class Spawn {
        public int x;
        public int y;
        public int z;
    }
}

