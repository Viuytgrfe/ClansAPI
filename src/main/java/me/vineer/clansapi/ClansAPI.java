package me.vineer.clansapi;

import me.vineer.clansapi.clans.Clan;
import me.vineer.clansapi.commands.ClanCommand;
import me.vineer.clansapi.database.ClansController;
import me.vineer.clansapi.database.Database;
import me.vineer.clansapi.listeners.ChatListener;
import me.vineer.clansapi.listeners.MenuListener;
import me.vineer.clansapi.tabComplaters.ClanTabCompleter;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.UserManager;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

public final class ClansAPI extends JavaPlugin {

    public static ClansAPI plugin;
    public static LuckPerms luckPerms;
    public static UserManager userManager;

    public static List<Clan> clans;

    private BukkitAudiences adventure;


    private static final HashMap<Player, PlayerMenuUtility> playerMenuUtilityMap = new HashMap<>();
    @Override
    public void onEnable() {
        plugin = this;
        luckPerms = LuckPermsProvider.get();
        userManager = luckPerms.getUserManager();
        this.adventure = BukkitAudiences.create(this);
        Database.initDatabase();
        clans = ClansController.getClanList();
        for (Clan clan : clans) {
            System.out.println(clan);
        }

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

    public static void teleport(Audience audience, Player player, Location location) {
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

}
