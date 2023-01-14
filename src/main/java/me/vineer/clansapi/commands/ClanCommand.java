package me.vineer.clansapi.commands;

import me.vineer.clansapi.ClansAPI;
import me.vineer.clansapi.Experience;
import me.vineer.clansapi.PlayerMenuUtility;
import me.vineer.clansapi.clans.Clan;
import me.vineer.clansapi.clans.player.ClanPlayer;
import me.vineer.clansapi.clans.ranks.ClanRank;
import me.vineer.clansapi.database.ClansController;
import me.vineer.clansapi.heads.NickController;
import me.vineer.clansapi.menu.custom.ClanMenu;
import me.vineer.clansapi.menu.custom.players.ClanPlayersMenu;
import me.vineer.clansapi.menu.custom.requests.ClanRequestsMenu;
import me.vineer.economyapi.money.Balance;
import net.kyori.adventure.audience.Audience;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ClanCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length == 3) {
            if(args[0].equals("create")) {
                if(ClansController.isPlayer(sender.getName())) {
                    sender.sendMessage(ChatColor.YELLOW + "[CA] " + ChatColor.RED + "Вы не можете создать клан т.к. вы находитесь в клане или у вас уже есть клан.");
                } else {
                    Clan clan = ClansController.createClan(args[1], args[2], sender.getName());
                    sender.sendMessage(ChatColor.YELLOW + "[CA] " + ChatColor.GREEN + "Клан " + ChatColor.GOLD + args[1] + ChatColor.GREEN + " был успешно создан!");
                    NickController.changeClan(sender.getName(), args[2]);
                }
            }
        } else if (args.length == 2) {
            if (args[0].equals("request")) {
                if(ClansController.isPlayer(sender.getName())) {
                    sender.sendMessage(ChatColor.YELLOW + "[CA] " + ChatColor.RED + "Вы не можете подать запрос в клан т.к. вы находитесь в клане. Для того чтобы подять заявку в клан, сначала выйдите из клана, в котором вы состоите используя команду " + ChatColor.GOLD + "/clan leave");
                    return true;
                } else if (ClansController.isRequest(sender.getName())) {
                    sender.sendMessage(ChatColor.YELLOW + "[CA] " + ChatColor.RED + "Вы не можете подать запрос в клан т.к. вы уже подали запрос в клан. Для того чтобы отменить завпрос в клан пропишите команду " + ChatColor.GOLD + "/clan cancel-request");
                    return true;
                } else {
                    ClansController.addRequestToClan(sender.getName(), args[1]);
                }
            } else if (args[0].equals("bank")) {
                if(args[1].equals("see")) {
                    if(!ClansController.isPlayer(sender.getName())) {
                        sender.sendMessage(ChatColor.YELLOW + "[CA] " + ChatColor.RED + "Вы не состоите в клане.");
                        return true;
                    }
                    Clan clan = ClansController.getClan(ClansController.getClanOfPlayer(sender.getName()));
                    if(clan == null) {
                        sender.sendMessage(ChatColor.YELLOW + "[CA] " + ChatColor.RED + "такого клана не существует.");
                        return true;
                    }
                    sender.sendMessage(ChatColor.YELLOW + "[CA] " + ChatColor.GOLD + "money: " + clan.getClanBalance());
                    sender.sendMessage(ChatColor.YELLOW + "[CA] " + ChatColor.GOLD + "XP: " + clan.getClanXP());
                }
            }
        } else if (args.length == 1) {
            if(args[0].equals("leave")) {
                if(ClansController.isPlayer(sender.getName()) && ClansController.getPlayer(sender.getName()).getRank() == ClanRank.PRESIDENT) sender.sendMessage(ChatColor.YELLOW + "[CA] " + ChatColor.RED + "Вы не можете выйти из клана т.к. вы глава.");
                else if(!ClansController.isPlayer(sender.getName())) sender.sendMessage(ChatColor.YELLOW + "[CA] " + ChatColor.RED + "Вы не можете выйти из клана т.к. вы не состоите ни в одном клане.");
                else {
                    ClansController.removePlayerFromClan(sender.getName());
                    NickController.leaveFromClan(sender.getName());
                }
            } else if (args[0].equals("cancel-request")) {
                if(!ClansController.isRequest(sender.getName())) sender.sendMessage(ChatColor.YELLOW + "[CA] " + ChatColor.RED + "Вы не можете отменить запрос в клан т.к. вы не подавали запросов в клан");
                else ClansController.removeRequestFromClan(sender.getName());
            } else if (args[0].equals("requests")) {
                if(ClansController.isPlayer(sender.getName()) && (ClansController.getPlayer(sender.getName()).getRank() == ClanRank.PRESIDENT || ClansController.getPlayer(sender.getName()).getRank() == ClanRank.VICE_PRESIDENT)) {
                    PlayerMenuUtility playerMenuUtility = ClansAPI.getPlayerMenuUtility((Player) sender);
                    new ClanRequestsMenu(playerMenuUtility, ClansController.getClanOfPlayer(sender.getName())).open();
                } else {
                    sender.sendMessage(ChatColor.YELLOW + "[CA] " + ChatColor.RED + "У вас недостаточно прав, либо вы не состоите в клане.");
                }
            } else if (args[0].equals("players")) {
                if(ClansController.isPlayer(sender.getName())) {
                    PlayerMenuUtility playerMenuUtility = ClansAPI.getPlayerMenuUtility((Player) sender);
                    new ClanPlayersMenu(playerMenuUtility, ClansController.getClanOfPlayer(sender.getName())).open();
                } else {
                    sender.sendMessage(ChatColor.YELLOW + "[CA] " + ChatColor.RED + "Вы не состоите в клане.");
                }
            } else if (args[0].equals("remove")) {
                if(ClansController.isPresident(sender.getName())) {
                    ClansController.removeClan(ClansController.getClanOfPlayer(sender.getName()));
                } else {
                    sender.sendMessage(ChatColor.YELLOW + "[CA] " + ChatColor.RED + "У вас нет клана!");
                }
            } else if (args[0].equals("home")) {
                String name = ClansController.getClanOfPlayer(sender.getName());
                Location location = ClansController.getClanHome(name);
                Player player = ((Player) sender);
                if(ClansController.getClanOfPlayer(player.getName()) == null) {
                    player.sendMessage(ChatColor.YELLOW + "[CA] " + ChatColor.RED + "Вы не состоите в клане!");
                    return true;
                }
                if(location == null) {
                    player.sendMessage(ChatColor.YELLOW + "[CA] " + ChatColor.RED + "У клана не установлена точка дома!");
                    return true;
                }
                Audience audience = ClansAPI.getPlugin().adventure().sender(sender);
                ClansAPI.teleport(audience, player, location);
            } else if (args[0].equals("sethome")) {
                ClanPlayer p = ClansController.getPlayer(sender.getName());
                Player player = ((Player) sender);
                if(p.getRank() == ClanRank.ELDER || p.getRank() == ClanRank.VICE_PRESIDENT || p.getRank() == ClanRank.PRESIDENT) {
                    String name = ClansController.getClanOfPlayer(sender.getName());
                    ClansController.setClanHome(name, new Location(player.getWorld(), player.getLocation().getBlockX(), player.getLocation().getY(), player.getLocation().getBlockZ()));
                    player.sendMessage(ChatColor.YELLOW + "[CA] " + ChatColor.GREEN + "Точка дома клана успешно установлена!");
                } else {
                    player.sendMessage(ChatColor.YELLOW + "[CA] " + ChatColor.RED + "У вас недостаточно прав для установки клановой точки дома!");
                }

            }
        } else if (args.length == 4) {
            if(args[0].equals("bank")) {
                if(!ClansController.isPlayer(sender.getName())) {
                    sender.sendMessage(ChatColor.YELLOW + "[CA] " + ChatColor.RED + "Вы не состоите в клане.");
                    return true;
                }
                Clan clan = ClansController.getClan(ClansController.getClanOfPlayer(sender.getName()));
                if(clan == null) {
                    sender.sendMessage(ChatColor.YELLOW + "[CA] " + ChatColor.RED + "такого клана не существует.");
                    return true;
                }
                int amount = Integer.parseInt(args[3]);
                if(args[1].equals("xp")) {
                    if(args[2].equals("add")) {
                        if(Experience.getExpFromLevel(amount) > Experience.getExp(((Player) sender))) {
                            sender.sendMessage(ChatColor.YELLOW + "[CA] " + ChatColor.RED + "У вас недостаточно опыта.");
                            return true;
                        }
                        Experience.changeExp(((Player) sender), -Experience.getExpFromLevel(amount));
                        clan.addXP(Experience.getExpFromLevel(amount));
                        sender.sendMessage(ChatColor.YELLOW + "[CA] " + ChatColor.GREEN + "Вы положили в клан " + ChatColor.GOLD + amount + " уровней" + ChatColor.GREEN + " (" + ChatColor.GOLD + Experience.getExpFromLevel(amount) + ChatColor.GREEN + " единиц опыта)");
                    } else if (args[2].equals("withdraw")) {
                        if(Experience.getLevelFromExp(clan.getClanXP()) < amount) {
                            sender.sendMessage(ChatColor.YELLOW + "[CA] " + ChatColor.RED + "В клане недостаточно опыта.");
                            return true;
                        }
                        clan.removeXP(Experience.getExpFromLevel(amount));
                        ((Player) sender).giveExp(Experience.getExpFromLevel(amount));
                        sender.sendMessage(ChatColor.YELLOW + "[CA] " + ChatColor.GREEN + "Вы достали из клана " + ChatColor.GOLD + amount + " уровней" + ChatColor.GREEN + " (" + ChatColor.GOLD + Experience.getExpFromLevel(amount) + ChatColor.GREEN + " единиц опыта)");
                    }
                } else if (args[1].equals("$")) {
                    if(args[2].equals("add")) {
                        Balance balance = Balance.getPlayerBalance(sender.getName());
                        if(balance.getMoney() < amount) {
                            sender.sendMessage(ChatColor.YELLOW + "[CA] " + ChatColor.RED + "У вас недостаточно денег.");
                            return true;
                        }
                        clan.addMoney(amount);
                        Balance.changePlayerBalance(sender.getName(), -amount,0);
                    } else if (args[2].equals("withdraw")) {
                        if(clan.getClanBalance() < amount) {
                            sender.sendMessage(ChatColor.YELLOW + "[CA] " + ChatColor.RED + "В клане недостаточно денег.");
                            return true;
                        }
                        clan.removeMoney(amount);
                        Balance.changePlayerBalance(sender.getName(), amount,0);
                    }
                }
            }
        } else if (args.length == 0) {
            if(!ClansController.isPlayer(sender.getName())) {
                sender.sendMessage(ChatColor.YELLOW + "[CA] " + ChatColor.RED + "Вы не состоите в клане!");
                return true;
            }
            new ClanMenu(ClansAPI.getPlayerMenuUtility((Player) sender)).open();
        }
        return true;
    }
}
