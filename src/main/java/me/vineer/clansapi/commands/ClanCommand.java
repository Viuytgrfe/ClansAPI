package me.vineer.clansapi.commands;

import me.vineer.clansapi.ClansAPI;
import me.vineer.clansapi.Experience;
import me.vineer.clansapi.PlayerMenuUtility;
import me.vineer.clansapi.bossBar.BossBarCreator;
import me.vineer.clansapi.clan.Clan;
import me.vineer.clansapi.clan.player.ClanPlayer;
import me.vineer.clansapi.clan.ranks.ClanRank;
import me.vineer.clansapi.clan.war.War;
import me.vineer.clansapi.clan.war.WarState;
import me.vineer.clansapi.clan.war.teams.ClanTeam;
import me.vineer.clansapi.commands.admin.ClanAdminCommand;
import me.vineer.clansapi.config.main.MainConfig;
import me.vineer.clansapi.database.ClansController;
import me.vineer.clansapi.heads.NickController;
import me.vineer.clansapi.menu.custom.ClanMenu;
import me.vineer.clansapi.menu.custom.players.ClanPlayersMenu;
import me.vineer.clansapi.menu.custom.requests.ClanRequestsMenu;
import net.ess3.api.MaxMoneyException;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.math.BigDecimal;

public class ClanCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length == 3) {
            if(args[0].equals("create")) {
                if(ClansController.isPlayer(sender.getName())) {
                    sender.sendMessage(ChatColor.YELLOW + "[CA] " + ChatColor.RED + "Вы не можете создать клан т.к. вы находитесь в клане или у вас уже есть клан.");
                } else {
                    ClansController.createClan(args[1], args[2], sender.getName());
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
                ClansAPI.teleport(player, location);
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
            } else if (args[0].equals("startwar")) {
                if(!War.enabled) {
                    sender.sendMessage(ChatColor.YELLOW + "[CA] " + ChatColor.RED + "Сейчас клановые битвы не включены.");
                    return true;
                }
                ClanPlayer player = ClansController.getPlayer(sender.getName());
                if (player == null) {
                    sender.sendMessage(ChatColor.YELLOW + "[CA] " + ChatColor.RED + "Вы не состоите в клане!");
                    return true;
                }
                if(player.getRank() != ClanRank.PRESIDENT) {
                    sender.sendMessage(ChatColor.YELLOW + "[CA] " + ChatColor.RED + "Вы не можете начинать битву клана!");
                    return true;
                }
                if (War.getTeam(ClansController.getClanOfPlayer(sender.getName())) != null) {
                    sender.sendMessage(ChatColor.YELLOW + "[CA] " + ChatColor.RED + "Вы уже находитесь в подготовке к битве!");
                    return true;
                }
                if(War.state != null && War.state != WarState.FIRST_INIT) {
                    sender.sendMessage(ChatColor.YELLOW + "[CA] " + ChatColor.RED + "Набор на клановую битву закончился! Дождитесь следующей для участия.");
                    return true;
                }
                War.addTeam(new ClanTeam(ClansController.getClanOfPlayer(sender.getName())));
                Bukkit.getOnlinePlayers().forEach(p -> {
                    ClanPlayer pl = ClansController.getPlayer(p.getName());
                    if(pl != null) {
                        if(!War.started) {
                            if(pl.getRank() != ClanRank.PRESIDENT) {
                                p.sendMessage(ChatColor.YELLOW + "[CA] " + ChatColor.WHITE + "Клан " + ChatColor.GREEN + ClansController.getClanOfPlayer(sender.getName()) + ChatColor.WHITE + " начинает Клановые Войны! Если глава вашего клана подключится к битве, то у вас будет шанс поучавствовать в битве!");
                            }
                            else {
                                p.sendMessage(ChatColor.YELLOW + "[CA] " + ChatColor.WHITE + "Клан " + ChatColor.GREEN + ClansController.getClanOfPlayer(sender.getName()) + ChatColor.WHITE + " начинает Клановые Войны! Хочешь сразиться против кланов? Тогда собирай свою группу, прописав команду:" + ChatColor.GREEN + " /clan startwar" + ChatColor.WHITE + " и у вас будет время на подготовку в размере: " + ClansAPI.getTextFromTick(MainConfig.data.clanwarSecondDelay) + " для начала!");
                            }
                            if(!ClansController.getClanOfPlayer(pl.getPlayerName()).equals(ClansController.getClanOfPlayer(sender.getName()))) {
                                BossBarCreator.generateWarBossBar(p);
                            }
                        }
                        else {
                            p.sendMessage(ChatColor.YELLOW + "[CA] " + ChatColor.WHITE + "Клан " + ChatColor.GREEN + ClansController.getClanOfPlayer(sender.getName()) + ChatColor.WHITE + " подключается к клановым войнам!");
                        }
                    }
                });
                ClansController.getClan(ClansController.getClanOfPlayer(sender.getName())).getPlayers().forEach(clanPlayer -> {
                    Player clanplayer = Bukkit.getPlayer(clanPlayer.getPlayerName());
                    if(clanplayer != null && clanPlayer.getRank() != ClanRank.PRESIDENT) clanplayer.sendMessage(ChatColor.YELLOW + "[CA] " + ChatColor.WHITE + "Ваш клан начал сбор на битву кланов, если желаете, подготовтесь к битве и отправьте заявку на битву используя команду " + ChatColor.GREEN + "/waraccept");
                    if(clanplayer != null && !War.started) {
                        BossBarCreator.generateWarBossBar(clanplayer);
                    }
                });
                if(!War.started) {
                    War.started = true;
                    War.startMainTimer();
                }
            } else if (args[0].equals("waraccept")) {
                if(!War.enabled) {
                    sender.sendMessage(ChatColor.YELLOW + "[CA] " + ChatColor.RED + "Сейчас клановые битвы не включены.");
                    return true;
                }
                if(!War.started) {
                    sender.sendMessage(ChatColor.YELLOW + "[CA] " + ChatColor.RED + "Сейчас клановые битвы не проходят.");
                    return true;
                }
                ClanPlayer player = ClansController.getPlayer(sender.getName());
                if(player == null) {
                    sender.sendMessage(ChatColor.YELLOW + "[CA] " + ChatColor.RED + "Вы не состоите в клане.");
                    return true;
                }
                if(War.state.ordinal() > WarState.FIRST_INIT.ordinal()) {
                    sender.sendMessage(ChatColor.YELLOW + "[CA] " + ChatColor.RED + "Набор на клановую битву закончился! Дождитесь следующей битвы для участия.");
                    return true;
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
                        BigDecimal balance = ClansAPI.essentials.getUser(sender.getName()).getMoney();
                        if(balance.compareTo(BigDecimal.valueOf(amount)) < 0) {
                            sender.sendMessage(ChatColor.YELLOW + "[CA] " + ChatColor.RED + "У вас недостаточно денег.");
                            return true;
                        }
                        clan.addMoney(amount);
                        ClansAPI.essentials.getUser(sender.getName()).takeMoney(BigDecimal.valueOf(amount));
                    } else if (args[2].equals("withdraw")) {
                        if(clan.getClanBalance() < amount) {
                            sender.sendMessage(ChatColor.YELLOW + "[CA] " + ChatColor.RED + "В клане недостаточно денег.");
                            return true;
                        }
                        clan.removeMoney(amount);
                        try {
                            ClansAPI.essentials.getUser(sender.getName()).giveMoney(BigDecimal.valueOf(amount));
                        } catch (MaxMoneyException e) {
                            clan.addMoney(amount);
                            sender.sendMessage(ChatColor.YELLOW + "[CA] " + ChatColor.RED + "У вас лимит денег!");
                        }
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
        if(args.length > 0 && args[0].equals("admin") && sender.isOp()) return ClanAdminCommand.onCommand(sender, command, label, args);
        else return true;
    }
}
