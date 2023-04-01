package me.vineer.clansapi;

import com.earth2me.essentials.Essentials;
import me.vineer.clansapi.bossBar.BossBarCreator;
import me.vineer.clansapi.commands.ClanCommand;
import me.vineer.clansapi.config.arena.ArenaConfig;
import me.vineer.clansapi.config.main.MainConfig;
import me.vineer.clansapi.database.Database;
import me.vineer.clansapi.listeners.ChatListener;
import me.vineer.clansapi.listeners.MenuListener;
import me.vineer.clansapi.tabCompleters.ClanTabCompleter;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.UserManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.time.Duration;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public final class ClansAPI extends JavaPlugin {

    public static ClansAPI plugin;
    public static LuckPerms luckPerms;
    public static UserManager userManager;
    public static Essentials essentials;
    private BukkitAudiences adventure;


    private static final HashMap<Player, PlayerMenuUtility> playerMenuUtilityMap = new HashMap<>();
    @Override
    public void onEnable() {
        plugin = this;
        essentials = (Essentials) Bukkit.getPluginManager().getPlugin("Essentials");
        luckPerms = LuckPermsProvider.get();
        userManager = luckPerms.getUserManager();
        try {
            MainConfig.loadConfig();
            ArenaConfig.loadConfig();
        } catch (Exception e) {
            e.printStackTrace();
        }
        File dir = new File("plugins/ClansAPI/schematics");
        if(!dir.exists())dir.mkdirs();

        this.adventure = BukkitAudiences.create(this);
        Database.initDatabase();

        this.getCommand("clan").setExecutor(new ClanCommand());
        this.getCommand("clan").setTabCompleter(new ClanTabCompleter());
        this.getServer().getPluginManager().registerEvents(new MenuListener(), this);
        this.getServer().getPluginManager().registerEvents(new ChatListener(), this);
    }

    public static ClansAPI getPlugin() {
        return plugin;
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        Database.disconnect();
    }

    public static PlayerMenuUtility getPlayerMenuUtility(Player p) {
        PlayerMenuUtility playerMenuUtility;
        if (!(playerMenuUtilityMap.containsKey(p))) { //See if the player has a playermenuutility "saved" for them

            //This player doesn't. Make one for them add add it to the hashmap
            playerMenuUtility = new PlayerMenuUtility(p);
            playerMenuUtilityMap.put(p, playerMenuUtility);

            return playerMenuUtility;
        } else {
            return playerMenuUtilityMap.get(p); //Return the object by using the provided player
        }
    }

    public @NotNull BukkitAudiences adventure() {
        if(this.adventure == null) {
            throw new IllegalStateException("Tried to access Adventure when the plugin was disabled!");
        }
        return this.adventure;
    }

    public static void teleport(Player player, Location location) {
        Audience audience = ClansAPI.getPlugin().adventure().player(player);
        BossBar bossBar = BossBar.bossBar(Component.text("Телепортация:" + ChatColor.RED +" 5 секунд"), 0.0f, BossBar.Color.RED, BossBar.Overlay.PROGRESS);
        new BukkitRunnable() {
            @Override
            public void run() {
                audience.showBossBar(bossBar);
                float tick = 0;
                while (tick < 1000) {
                    tick++;
                    bossBar.progress(tick / 1000);
                    if(tick == 200){ bossBar.name(Component.text("Телепортация:" + ChatColor.RED +" 4 секунды")); bossBar.color(BossBar.Color.RED);}
                    else if(tick == 400){ bossBar.name(Component.text("Телепортация:" + ChatColor.YELLOW +" 3 секунды")); bossBar.color(BossBar.Color.YELLOW);}
                    else if(tick == 600){ bossBar.name(Component.text("Телепортация:" + ChatColor.YELLOW +" 2 секунды")); bossBar.color(BossBar.Color.YELLOW);}
                    else if(tick == 800){ bossBar.name(Component.text("Телепортация:" + ChatColor.GREEN +" 1 секунда")); bossBar.color(BossBar.Color.GREEN);}
                    try {
                        TimeUnit.MILLISECONDS.sleep(5);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                audience.hideBossBar(bossBar);
                location.setYaw(player.getLocation().getYaw());
                location.setPitch(player.getLocation().getPitch());
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        player.teleport(location);
                    }
                }.runTask(ClansAPI.getPlugin());
            }
        }.runTaskAsynchronously(ClansAPI.getPlugin());
    }

    public static void teleport(Player player, Location location, int time) {
        BossBarCreator.displayBossBar(player, time, ChatColor.GOLD + "Подготовка кланов к битве.", () -> {
            location.setYaw(player.getLocation().getYaw());
            location.setPitch(player.getLocation().getPitch());
            new BukkitRunnable() {
                @Override
                public void run() {
                    player.teleport(location);
                }
            }.runTask(ClansAPI.getPlugin());
        });
    }

    public static String getTextFromTick(float tick) {
        Duration duration = Duration.ofMillis((long) (tick * 50L));
        String minutes;
        String seconds;
        if (duration.toMinutes() > 4)
            minutes = duration.toMinutes() + " минут" + (duration.toSecondsPart() == 0 ? "" : " ");
        else if (duration.toMinutes() > 1)
            minutes = duration.toMinutes() + " минуты" + (duration.toSecondsPart() == 0 ? "" : " ");
        else if (duration.toMinutes() == 1) minutes = "1 минута" + (duration.toSecondsPart() == 0 ? "" : " ");
        else minutes = "";
        if (duration.toMinutes() == 0) {
            if (duration.toSecondsPart() % 10 > 4 || (duration.toSecondsPart() % 10 == 0 && duration.toSecondsPart() != 0)) seconds = duration.toSecondsPart() + " секунд";
            else if (duration.toSecondsPart() % 10 <= 4 && duration.toSecondsPart() % 10 > 1 && !(duration.toSecondsPart() < 15 && duration.toSecondsPart() > 10))
                seconds = duration.toSecondsPart() + " секунды";
            else if (duration.toSecondsPart() < 15 && duration.toSecondsPart() > 10)
                seconds = duration.toSecondsPart() + " секунд";
            else if (duration.toSecondsPart() == 1) seconds = "1 секунда";
            else seconds = "0 секунд";
        } else {
            if (duration.toSecondsPart() % 10 > 4 || (duration.toSecondsPart() % 10 == 0 && duration.toSecondsPart() != 0))
                seconds = duration.toSecondsPart() + " секунд";
            else if (duration.toSecondsPart() % 10 <= 4 && duration.toSecondsPart() % 10 > 1 && !(duration.toSecondsPart() < 15 && duration.toSecondsPart() > 10))
                seconds = duration.toSecondsPart() + " секунды";
            else if (duration.toSecondsPart() < 15 && duration.toSecondsPart() > 10)
                seconds = duration.toSecondsPart() + " секунд";
            else if (duration.toSecondsPart() % 10 == 1) seconds = duration.toSecondsPart() + " секунда";
            else seconds = "";
        }


        return minutes + seconds;
    }

}
