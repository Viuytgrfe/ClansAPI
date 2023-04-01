package me.vineer.clansapi.menu.custom.clanHome;

import me.vineer.clansapi.ClansAPI;
import me.vineer.clansapi.PlayerMenuUtility;
import me.vineer.clansapi.clan.Clan;
import me.vineer.clansapi.clan.player.ClanPlayer;
import me.vineer.clansapi.database.ClansController;
import me.vineer.clansapi.menu.Menu;
import me.vineer.clansapi.menu.items.GuiInventoryCreator;
import net.kyori.adventure.audience.Audience;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class ClanHomeMenu extends Menu {

    Clan clan;
    ClanPlayer MenuPlayer;

    public ClanHomeMenu(PlayerMenuUtility playerMenuUtility, String clanName) {
        super(playerMenuUtility);
        clan = ClansController.getClan(clanName);
        MenuPlayer = ClansController.getPlayer(playerMenuUtility.getOwner().getName());
    }

    @Override
    public String getMenuName() {
        return "Точка дома клана " + clan.getClanName();
    }

    @Override
    public int getSlots() {
        return 9;
    }

    @Override
    public void handleMenu(InventoryClickEvent event) {
        if(event.getCurrentItem().getItemMeta().getDisplayName().equals(ChatColor.WHITE + "Точка дома клана")) {
            if(event.getCurrentItem().getItemMeta().getLore().get(1).equals(ChatColor.DARK_GRAY + "телепортироваться")) {
                event.getWhoClicked().closeInventory();
                Location loc = clan.getClanHome();
                loc.setX(loc.getBlockX() + 0.5);
                loc.setZ(loc.getBlockZ() + 0.5);
                ClansAPI.teleport(playerMenuUtility.getOwner(), loc);
            } else {
                Location loc = event.getWhoClicked().getLocation();
                clan.setClanHome(loc);
                event.getWhoClicked().sendMessage(ChatColor.YELLOW + "[CA] " + ChatColor.GREEN + "Точка дома установлена!");
                event.getWhoClicked().closeInventory();
            }

        } else if (event.getCurrentItem().getItemMeta().getDisplayName().equals(ChatColor.RED + "Удалить точку дома")) {
            ClansController.deleteClanHome(clan.getClanName());
            event.getWhoClicked().sendMessage(ChatColor.YELLOW + "[CA] " + ChatColor.GREEN + "Точка дома успешно удалена!");
            event.getWhoClicked().closeInventory();
        }
    }

    @Override
    public void setMenuItems() {
        if(ClansController.getClanHome(clan.getClanName()) != null)inventory.setItem(5, GuiInventoryCreator.modifyGuiItem(getDefaultHead(), ChatColor.WHITE + "Точка дома клана", ChatColor.DARK_GRAY + "Нажмите чтобы", ChatColor.DARK_GRAY + "телепортироваться"));
        else inventory.setItem(5, GuiInventoryCreator.modifyGuiItem(getDefaultHead(), ChatColor.WHITE + "Точка дома клана", ChatColor.DARK_GRAY + "Нажмите чтобы", ChatColor.DARK_GRAY + "Установить точку дома"));
        inventory.setItem(8, GuiInventoryCreator.createGuiItem(Material.RED_STAINED_GLASS_PANE, ChatColor.RED + "Удалить точку дома"));
    }

    private static ItemStack getDefaultHead() {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD, 1, (short) 3);
        Bukkit.getUnsafe().modifyItemStack(head, "{SkullOwner:{Id:[I;891982802,1654277952,-1266198704,-1544975137],Properties:{textures:[{Value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjNkMDJjZGMwNzViYjFjYzVmNmZlM2M3NzExYWU0OTc3ZTM4YjkxMGQ1MGVkNjAyM2RmNzM5MTNlNWU3ZmNmZiJ9fX0\"}]}}}");
        return head;
    }
}
