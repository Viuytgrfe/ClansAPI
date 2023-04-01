package me.vineer.clansapi.bossBar;

import com.sun.tools.javac.Main;
import me.vineer.clansapi.ClansAPI;
import me.vineer.clansapi.clan.war.War;
import me.vineer.clansapi.clan.war.WarState;
import me.vineer.clansapi.clan.war.teams.ClanTeam;
import me.vineer.clansapi.config.main.MainConfig;
import me.vineer.clansapi.database.ClansController;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class BossBarCreator {
    public List<HashMap<String, BukkitTask>> tasks = new ArrayList<>();
    public static void displayBossBar(Player player, long time, String text, Runnable after) {
        Audience audience = ClansAPI.getPlugin().adventure().player(player);
        BossBar bossBar = BossBar.bossBar(Component.text(text + " Осталось: " + ClansAPI.getTextFromTick(time)), 1.0f, BossBar.Color.RED, BossBar.Overlay.PROGRESS);
        new BukkitRunnable() {
            @Override
            public void run() {
                audience.showBossBar(bossBar);
                float tick = 0;
                while (tick < time) {
                    tick++;
                    bossBar.progress((time - tick) / time);
                    if(tick == (int)(time * 0.2)){ bossBar.color(BossBar.Color.RED);}
                    else if(tick == (int)(time * 0.4)){ bossBar.color(BossBar.Color.YELLOW);}
                    else if(tick == (int)(time * 0.6)){ bossBar.color(BossBar.Color.YELLOW);}
                    else if(tick == (int)(time * 0.8)){ bossBar.color(BossBar.Color.GREEN);}
                    if(tick % 20 == 0) {
                        bossBar.name(Component.text(text + " Осталось: " + ClansAPI.getTextFromTick(time - tick)));
                    }
                    try {
                        TimeUnit.MILLISECONDS.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                audience.hideBossBar(bossBar);
                after.run();
            }
        }.runTaskAsynchronously(ClansAPI.getPlugin());
    }

    public static void displayFirstBossBar(Player player, long time, String text, Runnable after) {
        Audience audience = ClansAPI.getPlugin().adventure().player(player);
        BossBar bossBar = BossBar.bossBar(Component.text(text + " Осталось: " + ClansAPI.getTextFromTick(time)), 1.0f, BossBar.Color.RED, BossBar.Overlay.PROGRESS);
        new BukkitRunnable() {
            @Override
            public void run() {
                audience.showBossBar(bossBar);
                float tick = 0;
                float lastTick;
                int endTickCounter = 0;
                while (tick < time) {
                    lastTick = tick;
                    tick = War.firstBossBarTick;
                    if(tick == lastTick) endTickCounter++;
                    if(endTickCounter == 3) cancel();
                    bossBar.progress((time - tick) / time);
                    if(tick >= (int)(time * 0.8)){ bossBar.color(BossBar.Color.GREEN);}
                    else if(tick >= (int)(time * 0.5)){ bossBar.color(BossBar.Color.YELLOW);}
                    else if(tick >= (int)(time * 0.2)){ bossBar.color(BossBar.Color.RED);}
                    if(tick % 20 == 0) {
                        bossBar.name(Component.text(text + " Осталось: " + ClansAPI.getTextFromTick(time - tick)));
                    }
                    try {
                        TimeUnit.MILLISECONDS.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                audience.hideBossBar(bossBar);
                after.run();
            }
        }.runTaskAsynchronously(ClansAPI.getPlugin());
    }

    public static void displayGameBossBar(Player player, long time, String text, Runnable after) {
        Audience audience = ClansAPI.getPlugin().adventure().player(player);
        BossBar bossBar = BossBar.bossBar(Component.text(text + " Осталось: " + ClansAPI.getTextFromTick(time)), 1.0f, BossBar.Color.RED, BossBar.Overlay.PROGRESS);
        new BukkitRunnable() {
            @Override
            public void run() {
                audience.showBossBar(bossBar);
                float tick = 0;
                float lastTick;
                int endTickCounter = 0;
                while (tick < time) {
                    lastTick = tick;
                    tick = War.gameBossBarTick;
                    if(tick == lastTick) endTickCounter++;
                    if(endTickCounter == 3) cancel();
                    bossBar.progress((time - tick) / time);
                    if(tick >= (int)(time * 0.8)){ bossBar.color(BossBar.Color.GREEN);}
                    else if(tick >= (int)(time * 0.5)){ bossBar.color(BossBar.Color.YELLOW);}
                    else if(tick >= (int)(time * 0.2)){ bossBar.color(BossBar.Color.RED);}
                    if(tick % 20 == 0) {
                        bossBar.name(Component.text(text + " Осталось: " + ClansAPI.getTextFromTick(time - tick)));
                    }
                    try {
                        TimeUnit.MILLISECONDS.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                audience.hideBossBar(bossBar);
                after.run();
            }
        }.runTaskAsynchronously(ClansAPI.getPlugin());
    }

    public static void displaySecondBossBar(Player player, long time, String text, Runnable after) {
        Audience audience = ClansAPI.getPlugin().adventure().player(player);
        BossBar bossBar = BossBar.bossBar(Component.text(text + " Осталось: " + ClansAPI.getTextFromTick(time)), 1.0f, BossBar.Color.RED, BossBar.Overlay.PROGRESS);
        new BukkitRunnable() {
            @Override
            public void run() {
                audience.showBossBar(bossBar);
                float tick = 0;
                float lastTick;
                int endTickCounter = 0;
                while (tick < time) {
                    lastTick = tick;
                    tick = War.secondBossBarTick;
                    if(tick == lastTick) endTickCounter++;
                    if(endTickCounter == 3) cancel();
                    bossBar.progress((time - tick) / time);
                    if(tick >= (int)(time * 0.8)){ bossBar.color(BossBar.Color.GREEN);}
                    else if(tick >= (int)(time * 0.5)){ bossBar.color(BossBar.Color.YELLOW);}
                    else if(tick >= (int)(time * 0.2)){ bossBar.color(BossBar.Color.RED);}
                    if(tick % 20 == 0) {
                        bossBar.name(Component.text(text + " Осталось: " + ClansAPI.getTextFromTick(time - tick)));
                    }
                    try {
                        TimeUnit.MILLISECONDS.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                audience.hideBossBar(bossBar);
                after.run();
            }
        }.runTaskAsynchronously(ClansAPI.getPlugin());
    }

    public static void displayThirdBossBar(Player player, long time, String text, Runnable after) {
        Audience audience = ClansAPI.getPlugin().adventure().player(player);
        BossBar bossBar = BossBar.bossBar(Component.text(text + " Осталось: " + ClansAPI.getTextFromTick(time)), 1.0f, BossBar.Color.RED, BossBar.Overlay.PROGRESS);
        new BukkitRunnable() {
            @Override
            public void run() {
                audience.showBossBar(bossBar);
                float tick = 0;
                float lastTick;
                int endTickCounter = 0;
                while (tick < time) {
                    if(War.state == WarState.THIRD_INIT && War.controller == null) {
                        break;
                    }
                    lastTick = tick;
                    tick = War.thirdBossBarTick;
                    if(tick == lastTick) endTickCounter++;
                    if(endTickCounter == 3) cancel();
                    bossBar.progress((time - tick) / time);
                    if(tick >= (int)(time * 0.8)){ bossBar.color(BossBar.Color.GREEN);}
                    else if(tick >= (int)(time * 0.5)){ bossBar.color(BossBar.Color.YELLOW);}
                    else if(tick >= (int)(time * 0.2)){ bossBar.color(BossBar.Color.RED);}

                    if(tick % 20 == 0) {
                        bossBar.name(Component.text(text + " Осталось: " + ClansAPI.getTextFromTick(time - tick)));
                    }
                    try {
                        TimeUnit.MILLISECONDS.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                audience.hideBossBar(bossBar);
                after.run();
            }
        }.runTaskAsynchronously(ClansAPI.getPlugin());
    }

    public static void generateWarBossBar(Player p) {
        BossBarCreator.displayFirstBossBar(p, MainConfig.data.clanwarFirstDelay, "Сбор групп для клановых битв.", () -> {
            ClanTeam team = War.getTeam(ClansController.getClanOfPlayer(p.getName()));
            if(team != null) {
                if(!team.isFull()) {
                    p.sendMessage(ChatColor.YELLOW + "[CA] " + ChatColor.WHITE + "К сожалению, в вашей команде недостаточно игроков для клановой битвы! Попробуйте собрать полную команду в следующий раз.");
                    // TODO возможно еще надо закинуть людей из запросов, если группа не полная
                } else {
                    BossBarCreator.displaySecondBossBar(p, MainConfig.data.clanwarSecondDelay, "Подготовка ресурсов к битве.", () -> {
                            BossBarCreator.displayThirdBossBar(p, MainConfig.data.clanwarThirdDelay, "Всё готово! Скоро начало битвы!", () -> {

                            });
                    });
                }
            }
        });
    }
}
