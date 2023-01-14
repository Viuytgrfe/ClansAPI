package me.vineer.clansapi.listeners;

import me.vineer.clansapi.ClansAPI;
import me.vineer.clansapi.clans.Clan;
import me.vineer.clansapi.clans.player.ClanPlayer;
import me.vineer.clansapi.database.ClansController;
import me.vineer.clansapi.heads.NickController;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.awt.*;

public class ChatListener implements Listener {
    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        if (event.getMessage().startsWith("#")) {
            event.setCancelled(true);
            Bukkit.getScheduler().runTaskAsynchronously(ClansAPI.getPlugin(), new Runnable() {
                @Override
                public void run() {
                    String clanName = ClansController.getClanOfPlayer(event.getPlayer().getName());
                    if(clanName == null) {
                        event.getPlayer().sendMessage(ChatColor.YELLOW + "[CA] " + ChatColor.RED + "Вы не состоите в клане!");
                        return;
                    }
                    Clan clan = ClansController.getClan(clanName);
                    for (ClanPlayer player: clan.getPlayers()) {
                        Player p = Bukkit.getPlayer(player.getPlayerName());
                        if(p == null) continue;
                        p.sendMessage(ChatColor.BLUE + "" + ChatColor.BOLD + "Клан " + ChatColor.WHITE + NickController.getTabNameFromNick(event.getPlayer().getName()) + net.md_5.bungee.api.ChatColor.of("#0088fd") +" ➤ " + ChatColor.GRAY + event.getMessage().substring(1));
                    }

                }
            });
        }
    }
}
