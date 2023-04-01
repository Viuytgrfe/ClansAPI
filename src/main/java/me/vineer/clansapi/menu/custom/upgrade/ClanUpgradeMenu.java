package me.vineer.clansapi.menu.custom.upgrade;

import me.vineer.clansapi.PlayerMenuUtility;
import me.vineer.clansapi.clan.Clan;
import me.vineer.clansapi.clan.player.ClanPlayer;
import me.vineer.clansapi.database.ClansController;
import me.vineer.clansapi.menu.Menu;
import me.vineer.clansapi.menu.items.GuiInventoryCreator;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;

public class ClanUpgradeMenu extends Menu {

    Clan clan;
    ClanPlayer MenuPlayer;

    public ClanUpgradeMenu(PlayerMenuUtility playerMenuUtility, String clanName) {
        super(playerMenuUtility);
        clan = ClansController.getClan(clanName);
        MenuPlayer = ClansController.getPlayer(playerMenuUtility.getOwner().getName());
    }

    @Override
    public String getMenuName() {
        return "прокачка клана " + clan.getClanName();
    }

    @Override
    public int getSlots() {
        return 9;
    }

    @Override
    public void handleMenu(InventoryClickEvent event) {

    }

    @Override
    public void setMenuItems() {
        inventory.setItem(0, GuiInventoryCreator.createGuiItem(Material.GRAY_STAINED_GLASS_PANE, "§f"));
        inventory.setItem(2, GuiInventoryCreator.createGuiItem(Material.EXPERIENCE_BOTTLE, "§fУровень клана 1", "§7Для повышения уровня нужно:", "§7▌ §a$§f100000", "§7▌ §aXP§f1000", "§a§lПодтвердите повышение уровня"));
        inventory.setItem(4, GuiInventoryCreator.createGuiItem(Material.EMERALD, "§fБанк клана", "§a$§f" + clan.getClanBalance(), "§aXP§f" + clan.getClanXP()));
        inventory.setItem(6, GuiInventoryCreator.createGuiItem(Material.RED_STAINED_GLASS_PANE, "§cОтменить"));
        inventory.setItem(8, GuiInventoryCreator.createGuiItem(Material.GRAY_STAINED_GLASS_PANE, "§f"));
    }
}
