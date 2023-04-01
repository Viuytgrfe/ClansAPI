package me.vineer.clansapi.config.arena;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.CustomClassLoaderConstructor;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class ArenaConfig {
    public static ArenaConfigData data;
    public static void loadConfig() throws FileNotFoundException {
        data = new Yaml(new CustomClassLoaderConstructor(ArenaConfigData.class.getClassLoader())).loadAs(new FileInputStream("plugins/ClansAPI/arenas.yml"), ArenaConfigData.class);
    }
}
