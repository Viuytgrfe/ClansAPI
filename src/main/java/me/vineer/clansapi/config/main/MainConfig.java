package me.vineer.clansapi.config.main;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.CustomClassLoaderConstructor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class MainConfig {
    public static MainConfigData data;
    public static void loadConfig() throws FileNotFoundException {
        data = new Yaml(new CustomClassLoaderConstructor(MainConfigData.class.getClassLoader())).loadAs(new FileInputStream("plugins/ClansAPI/config.yml"), MainConfigData.class);
    }
}