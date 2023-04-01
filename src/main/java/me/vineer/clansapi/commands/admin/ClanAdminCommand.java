package me.vineer.clansapi.commands.admin;

import me.vineer.clansapi.clan.war.War;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class ClanAdminCommand {
    public static boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args[1].equals("cwon")) {
            War.enabled = true;
        } else if (args[1].equals("cwoff")) {
            War.enabled = false;
        }
        return true;
    }
}
