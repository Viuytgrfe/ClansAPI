package me.vineer.clansapi.menu.custom;

import me.vineer.clansapi.ClansAPI;
import me.vineer.clansapi.PlayerMenuUtility;
import me.vineer.clansapi.clan.Clan;
import me.vineer.clansapi.clan.player.ClanPlayer;
import me.vineer.clansapi.clan.ranks.ClanRank;
import me.vineer.clansapi.database.ClansController;
import me.vineer.clansapi.menu.Menu;
import me.vineer.clansapi.menu.custom.acceptMenu.AcceptMenu;
import me.vineer.clansapi.menu.custom.acceptMenu.ChoiceDecision;
import me.vineer.clansapi.menu.custom.clanHome.ClanHomeMenu;
import me.vineer.clansapi.menu.custom.players.ClanPlayersMenu;
import me.vineer.clansapi.menu.custom.requests.ClanRequestsMenu;
import me.vineer.clansapi.menu.custom.upgrade.ClanUpgradeMenu;
import me.vineer.clansapi.menu.items.GuiInventoryCreator;
import net.kyori.adventure.audience.Audience;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class ClanMenu extends Menu {

    String clanName;
    Clan clan;
    ClanPlayer player;

    public ClanMenu(PlayerMenuUtility playerMenuUtility) {
        super(playerMenuUtility);
        this.clanName = ClansController.getClanOfPlayer(playerMenuUtility.getOwner().getName());
        clan = ClansController.getClan(clanName);
        player = ClansController.getPlayer(playerMenuUtility.getOwner().getName());
    }

    @Override
    public String getMenuName() {
        return "Меню клана " + clanName;
    }

    @Override
    public int getSlots() {
        return 27;
    }

    @Override
    public void handleMenu(InventoryClickEvent event) {
        if(event.getCurrentItem().getType() == Material.PLAYER_HEAD && event.getCurrentItem().getItemMeta().getDisplayName().equals("§fСписок игроков")) {
            new ClanPlayersMenu(playerMenuUtility, clanName).open();
        } else if (event.getCurrentItem().getType() == Material.PLAYER_HEAD && event.getCurrentItem().getItemMeta().getDisplayName().equals("§fЗаявки")) {
            if(ClansController.getPlayer(event.getWhoClicked().getName()).getRank().ordinal() >= ClanRank.ELDER.ordinal() && ClansController.getClanOfPlayer(event.getWhoClicked().getName()).equals(clanName)) {
                new ClanRequestsMenu(playerMenuUtility, clanName).open();
            } else {
                event.getWhoClicked().closeInventory();
                event.getWhoClicked().sendMessage(ChatColor.YELLOW + "[CA] " + ChatColor.RED + "У вас нет доступа к этой функции!");
            }
        } else if (event.getCurrentItem().getType() == Material.BARRIER && event.getCurrentItem().getItemMeta().getDisplayName().equals("§cПокинуть клан")) {
            new AcceptMenu(playerMenuUtility, new ChoiceDecision() {
                @Override
                public void caseYes() {
                    Clan clan = ClansController.getClan(clanName);
                    clan.removePlayerFromClan(event.getWhoClicked().getName());
                    event.getWhoClicked().sendMessage(ChatColor.YELLOW + "[CA] " + ChatColor.GREEN + "Вы успешно покинули клан!");
                    for (ClanPlayer player : clan.getPlayers()) {
                        Player p = Bukkit.getPlayer(player.getPlayerName());
                        if(p == null) continue;
                        p.sendMessage(ChatColor.YELLOW + "[CA] " + ChatColor.GOLD + "Игрок " + ChatColor.GREEN + event.getWhoClicked().getName() + ChatColor.GOLD + " вышел из клана!");
                    }
                }

                @Override
                public void caseNo() {

                }
            }).openWithText("Вы хотите выйти из клана?");
        } else if (event.getCurrentItem().getType() == Material.BARRIER && event.getCurrentItem().getItemMeta().getDisplayName().equals("§cУдалить клан")) {
            new AcceptMenu(playerMenuUtility, new ChoiceDecision() {
                @Override
                public void caseYes() {
                    ClansController.removeClan(clanName);
                    event.getWhoClicked().sendMessage(ChatColor.YELLOW + "[CA] " + ChatColor.GREEN + "Вы успешно удалили клан!");
                    for (ClanPlayer player : clan.getPlayers()) {
                        Player p = Bukkit.getPlayer(player.getPlayerName());
                        if(p == null) continue;
                        if(p.getName().equals(clan.getClanOwner())) continue;
                        p.sendMessage(ChatColor.YELLOW + "[CA] " + ChatColor.GOLD + "Клан в котором вы состояли был удалён!");
                    }
                }

                @Override
                public void caseNo() {

                }
            }).openWithText("Вы хотите удалить клан?");
        } else if (event.getCurrentItem().getType() == Material.PLAYER_HEAD && event.getCurrentItem().getItemMeta().getDisplayName().equals("§fБаза клана") && player.getRank().ordinal() < ClanRank.ELDER.ordinal()) {
            if(ClansController.getClanHome(clanName) != null) {
                ClansAPI.teleport(playerMenuUtility.getOwner(), clan.getClanHome());
            } else {
                event.getWhoClicked().sendMessage(ChatColor.YELLOW + "[CA] " + ChatColor.RED + "Точка дома клана не установлена!");
            }

        } else if (event.getCurrentItem().getType() == Material.PLAYER_HEAD && event.getCurrentItem().getItemMeta().getDisplayName().equals("§fБаза клана") && player.getRank().ordinal() >= ClanRank.ELDER.ordinal()) {
            new ClanHomeMenu(playerMenuUtility, clanName).open();
        } else if (event.getCurrentItem().getType() == Material.EXPERIENCE_BOTTLE) {
            new ClanUpgradeMenu(playerMenuUtility, clanName).open();
        }
    }

    @Override
    public void setMenuItems() {
        inventory.setItem(0, GuiInventoryCreator.createGuiItem(Material.RED_STAINED_GLASS_PANE, "§cНедоступно", "§7Откроется на §f1 §7уровне"));
        inventory.setItem(1, GuiInventoryCreator.createGuiItem(Material.RED_STAINED_GLASS_PANE, "§cНедоступно", "§7Откроется на §f2 §7уровне"));
        inventory.setItem(2, GuiInventoryCreator.createGuiItem(Material.GRAY_STAINED_GLASS_PANE, "§f"));
        inventory.setItem(4, GuiInventoryCreator.createGuiItem(Material.WHITE_BANNER, "§fФлаг клана §bMEP §f| Белый флаг"));
        inventory.setItem(6, GuiInventoryCreator.createGuiItem(Material.GRAY_STAINED_GLASS_PANE, "§f"));
        if(player.getRank().ordinal() >= ClanRank.ELDER.ordinal())inventory.setItem(7, GuiInventoryCreator.createGuiItem(Material.PLAYER_HEAD, "§fЗаявки", "§7Нажмите чтобы просмотреть", "§7Заявки на вступление в ваш клан"));
        else new ItemStack(Material.AIR);
        inventory.setItem(8, GuiInventoryCreator.createGuiItem(Material.PLAYER_HEAD, "§fСписок игроков", "§7Нажмите чтобы просмотреть", "§7Список участников клана"));
        inventory.setItem(9, GuiInventoryCreator.createGuiItem(Material.RED_STAINED_GLASS_PANE, "§cНедоступно", "§7Откроется на §f3 §7уровне"));
        inventory.setItem(10, GuiInventoryCreator.createGuiItem(Material.RED_STAINED_GLASS_PANE, "§cНедоступно", "§7Откроется на §f4 §7уровне"));
        inventory.setItem(11, GuiInventoryCreator.createGuiItem(Material.GRAY_STAINED_GLASS_PANE, "§f"));
        if(player.getRank() == ClanRank.PRESIDENT)inventory.setItem(13, GuiInventoryCreator.createGuiItem(Material.EXPERIENCE_BOTTLE, "§fУровень клана 1", "§7Для повышения уровня нужно:", "§7▌ §a$§f100000", "§7▌ §aXP§f1000", "§8Нажмите чтобы повысить уровень"));
        else new ItemStack(Material.AIR);
        inventory.setItem(15, GuiInventoryCreator.createGuiItem(Material.GRAY_STAINED_GLASS_PANE, "§f"));
        inventory.setItem(18, GuiInventoryCreator.createGuiItem(Material.RED_STAINED_GLASS_PANE, "§cНедоступно", "§7Откроется на §f5 §7уровне"));
        inventory.setItem(19, GuiInventoryCreator.createGuiItem(Material.RED_STAINED_GLASS_PANE, "§cНедоступно", "§7Откроется на §f6 §7уровне"));
        inventory.setItem(20, GuiInventoryCreator.createGuiItem(Material.GRAY_STAINED_GLASS_PANE, "§f"));
        inventory.setItem(21, GuiInventoryCreator.createGuiItem(Material.EMERALD, "§fБанк клана", "§a$§f" + clan.getClanBalance(), "§aXP§f" + clan.getClanXP()));
        if(player.getRank().ordinal() >= ClanRank.ELDER.ordinal())inventory.setItem(23, GuiInventoryCreator.createGuiItem(Material.PLAYER_HEAD, "§fБаза клана", "§8§lНажмите чтобы открыть", "§8§lМеню взаимодейтсвия"));
        else inventory.setItem(23, GuiInventoryCreator.createGuiItem(Material.PLAYER_HEAD, "§fБаза клана", "§8§lНажмите чтобы", "§8§lТелепортироваться"));
        inventory.setItem(24, GuiInventoryCreator.createGuiItem(Material.GRAY_STAINED_GLASS_PANE, "§f"));
        if(player.getRank() == ClanRank.PRESIDENT) inventory.setItem(25, GuiInventoryCreator.createGuiItem(Material.BARRIER, "§cУдалить клан"));
        else inventory.setItem(26, GuiInventoryCreator.createGuiItem(Material.BARRIER, "§cПокинуть клан"));

    }
}
