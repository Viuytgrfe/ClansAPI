package me.vineer.clansapi.tabCompleters;

import me.vineer.clansapi.clan.player.ClanPlayer;
import me.vineer.clansapi.clan.ranks.ClanRank;
import me.vineer.clansapi.database.ClansController;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class ClanTabCompleter implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completer = new ArrayList<>();
        if(args.length == 1) {
            completer.add("create");
            completer.add("request");
            completer.add("cancel-request");
            if(sender.isOp()) completer.add("admin");
            ClanPlayer player = ClansController.getPlayer(sender.getName());
            if(player != null) {
                if(player.getRank() == ClanRank.PRESIDENT || player.getRank() == ClanRank.VICE_PRESIDENT) {
                    completer.add("requests");
                    completer.add("sethome");
                }
                if(player.getRank() == ClanRank.PRESIDENT) {
                    completer.add("remove");
                    completer.add("startwar");
                }
                completer.add("bank");
                completer.add("leave");
                completer.add("players");
                completer.add("home");
            }

        } else if (args.length == 2) {
            if(args[0].equals("create")) {
                completer.add("<clan name>");
            } else if (args[0].equals("request")) {
                completer.add("<clan name>");
            } else if (args[0].equals("bank")) {
                completer.add("$");
                completer.add("xp");
                completer.add("see");
            }
        } else if(args.length == 3) {
            if(args[0].equals("create"))completer.add("<initial>");
            else if (args[0].equals("bank") && !args[1].equals("see")) {
                completer.add("add");
                completer.add("withdraw");
            }
        } else if (args.length == 4) {
            if(args[0].equals("bank")) {
                completer.add("<amount>");
            }
        }
        return completer;
    }
}
