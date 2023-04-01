package me.vineer.clansapi.menu.custom.players;

import me.vineer.clansapi.ClansAPI;
import me.vineer.clansapi.PlayerMenuUtility;
import me.vineer.clansapi.clan.player.ClanPlayer;
import me.vineer.clansapi.clan.ranks.ClanRank;
import me.vineer.clansapi.database.ClansController;
import me.vineer.clansapi.heads.NickController;
import me.vineer.clansapi.menu.Menu;
import me.vineer.clansapi.menu.custom.acceptMenu.AcceptMenu;
import me.vineer.clansapi.menu.custom.acceptMenu.ChoiceDecision;
import me.vineer.clansapi.menu.items.GuiInventoryCreator;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

public class ClanPlayerMenu extends Menu {

    private ClanPlayer clickedPlayer;
    private ClanPlayer MenuPlayer;
    public ClanPlayerMenu(PlayerMenuUtility playerMenuUtility, String clickedPlayer) {
        super(playerMenuUtility);
        this.clickedPlayer = ClansController.getPlayer(NickController.getNickFromTabName(clickedPlayer));
        this.MenuPlayer = ClansController.getPlayer(playerMenuUtility.getOwner().getName());
    }

    @Override
    public String getMenuName() {
        return clickedPlayer.getPlayerName();
    }

    @Override
    public int getSlots() {
        return 9;
    }

    @Override
    public void handleMenu(InventoryClickEvent event) {
        if(event.getCurrentItem().getType() == Material.LIME_STAINED_GLASS_PANE && event.getCurrentItem().getItemMeta().getDisplayName().equals("§aПовысить")) {
            clickedPlayer.updateRank();
            setMenuItems();
        } else if (event.getCurrentItem().getType() == Material.RED_STAINED_GLASS_PANE && event.getCurrentItem().getItemMeta().getDisplayName().equals("§cПонизить")) {
            clickedPlayer.decreaseRank();
            setMenuItems();
        } else if (event.getCurrentItem().getType() == Material.BARRIER) {
            new AcceptMenu(ClansAPI.getPlayerMenuUtility((Player) event.getWhoClicked()), new ChoiceDecision() {
                @Override
                public void caseNo() {
                    event.getWhoClicked().closeInventory();
                }

                @Override
                public void caseYes() {
                    event.getWhoClicked().closeInventory();
                    ClansController.removePlayerFromClan(clickedPlayer.getPlayerName());
                    event.getWhoClicked().sendMessage(ChatColor.YELLOW + "[CA] " + ChatColor.GREEN + "Игрок был выгнан из клана!");
                    Player player = Bukkit.getPlayer(clickedPlayer.getPlayerName());
                    if(player != null) player.sendMessage(ChatColor.YELLOW + "[CA] " + ChatColor.RED + "Вас выгнали из клана!");
                    NickController.leaveFromClan(clickedPlayer.getPlayerName());
                }
            }).openWithText("Выгнать ли этого игрока из клана?");
        }
    }

    @Override
    public void setMenuItems() {
        Bukkit.getScheduler().runTaskAsynchronously(ClansAPI.getPlugin(), new Runnable() {
            @Override
            public void run() {
                inventory.clear();
                if(MenuPlayer.getRank().ordinal() > clickedPlayer.getRank().ordinal()+1 && clickedPlayer.getRank() != ClanRank.VICE_PRESIDENT)inventory.setItem(1, GuiInventoryCreator.createGuiItem(Material.LIME_STAINED_GLASS_PANE, "§aПовысить", "§fДо §6" + clickedPlayer.getRank().next().translateToRus()));

                if(MenuPlayer.getRank().ordinal() > clickedPlayer.getRank().ordinal() && clickedPlayer.getRank() != ClanRank.BEGINNER)inventory.setItem(2, GuiInventoryCreator.createGuiItem(Material.RED_STAINED_GLASS_PANE, "§cПонизить", "§fДо §6" + clickedPlayer.getRank().previous().translateToRus()));

                inventory.setItem(4, GuiInventoryCreator.createGuiItem(Material.PLAYER_HEAD, NickController.getTabNameFromNick(clickedPlayer.getPlayerName()), "§6" + clickedPlayer.getRank().translateToRus()));
                if(MenuPlayer.getRank().ordinal() > ClanRank.PLAYER.ordinal() && clickedPlayer.getRank() != ClanRank.PRESIDENT && clickedPlayer.getRank().ordinal() < MenuPlayer.getRank().ordinal())inventory.setItem(7, GuiInventoryCreator.createGuiItem(Material.BARRIER, "§cВыгнать игрока из клана"));
            }
        });

    }
}
