package me.vineer.clansapi.heads;

import me.vineer.clansapi.ClansAPI;
import me.vineer.clansapi.database.ClansController;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.NodeType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class NickController {
    public static String getTabNameFromNick(String nick) {
        User user = ClansAPI.userManager.getUser(nick);
        String prefix = user.getCachedData().getMetaData().getPrefix();

        if(prefix == null)prefix = "";
        String name =  prefix + nick;
        name = name.replaceAll("&", "§");
        return name;
    }

    public static String getNickFromTabName(String name) {
        String[] splitName = name.split(" ");
        if(splitName.length > 1) {
            return ChatColor.stripColor(splitName[1]);
        } else {
            return name;
        }
    }

    public static void changeClan(String nick ,String initial) {
        int clanLevel = ClansController.getClan(ClansController.getClanOfPlayer(nick)).getClanLevel();
        ClansAPI.userManager.modifyUser(Bukkit.getOfflinePlayer(nick).getUniqueId(), user -> {
            for (Node node : user.getNodes(NodeType.SUFFIX)) {
                user.data().remove(node);
            }
            user.data().add(Node.builder("suffix.100." + ChatColor.GRAY + " " + getColorFromClanLevel(clanLevel) + initial + ChatColor.GRAY).build());
        });
    }

    public static void leaveFromClan(String nick) {
        ClansAPI.userManager.modifyUser(Bukkit.getOfflinePlayer(nick).getUniqueId(), user -> {
            user.data().add(Node.builder("suffix.100.").build());
            for (Node node : user.getNodes(NodeType.SUFFIX)) {
                user.data().remove(node);
            }
        });
    }

    public static ChatColor getColorFromClanLevel(int level) {
        if(level == 1) {
            return ChatColor.DARK_GRAY;
        } else if (level == 2) {
            return ChatColor.WHITE;
        } else if (level == 3) {
            return ChatColor.DARK_GREEN;
        } else if (level == 4) {
            return ChatColor.GREEN;
        } else if (level == 5) {
            return ChatColor.YELLOW;
        } else {
            return ChatColor.RED;
        }
    }
}
