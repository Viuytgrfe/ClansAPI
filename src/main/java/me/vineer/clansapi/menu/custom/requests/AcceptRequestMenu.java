package me.vineer.clansapi.menu.custom.requests;

import me.vineer.clansapi.PlayerMenuUtility;
import me.vineer.clansapi.database.ClansController;
import me.vineer.clansapi.heads.NickController;
import me.vineer.clansapi.menu.Menu;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class AcceptRequestMenu extends Menu {
    public AcceptRequestMenu(PlayerMenuUtility playerMenuUtility) {
        super(playerMenuUtility);
    }

    @Override
    public String getMenuName() {
        return "Подтвердить?";
    }

    @Override
    public int getSlots() {
        return 9;
    }

    @Override
    public void handleMenu(InventoryClickEvent event) {
        if(event.getCurrentItem().getType() == Material.EMERALD) {
            event.setCancelled(true);
            String target = super.playerMenuUtility.getPlayerFromRequest();
            if(ClansController.isPlayer(super.playerMenuUtility.getPlayerFromRequest())) {
                event.getWhoClicked().sendMessage(ChatColor.YELLOW + "[CA] " + ChatColor.RED + "Произошла ошибка, этот игрок находится в клане.");
                return;
            }
            ClansController.addPlayerToClan(target, super.playerMenuUtility.getClanName());
            NickController.changeClan(target, ClansController.getClan(playerMenuUtility.getClanName()).getClanInitial());
            ClansController.removeRequestFromClan(target);
            event.getWhoClicked().sendMessage(ChatColor.YELLOW + "[CA] " + ChatColor.GREEN + "Игрок был успешно принят в клан!");
            event.getWhoClicked().closeInventory();
            Player player = Bukkit.getPlayer(super.playerMenuUtility.getPlayerFromRequest());
            if(player != null) {
                player.sendMessage(ChatColor.YELLOW + "[CA] " + ChatColor.GOLD + "Ваш запрос на вступление в клан " + super.playerMenuUtility.getClanName() + " был принят.");
            }
        } else if (event.getCurrentItem().getType() == Material.BARRIER) {
            event.setCancelled(true);
            ClansController.removeRequestFromClan(super.playerMenuUtility.getPlayerFromRequest());
            event.getWhoClicked().sendMessage(ChatColor.YELLOW + "[CA] " + ChatColor.GREEN + "Запрос был успешно отменён!");
            Player player = Bukkit.getPlayer(super.playerMenuUtility.getPlayerFromRequest());
            if(player != null) {
                player.sendMessage(ChatColor.YELLOW + "[CA] " + ChatColor.GOLD + "Ваш запрос на вступление в клан " + super.playerMenuUtility.getClanName() + " был отменён.");
            }
            event.getWhoClicked().closeInventory();
        }
    }

    @Override
    public void setMenuItems() {
        ItemStack yes = new ItemStack(Material.EMERALD, 1);
        ItemMeta yes_meta = yes.getItemMeta();
        yes_meta.setDisplayName(ChatColor.GREEN + "Да");
        ArrayList<String> yes_lore = new ArrayList<>();
        yes_lore.add(ChatColor.AQUA + "Добавить ли ");
        yes_lore.add(ChatColor.AQUA + "этого игрока в клан?");
        yes_meta.setLore(yes_lore);
        yes.setItemMeta(yes_meta);
        ItemStack no = new ItemStack(Material.BARRIER, 1);
        ItemMeta no_meta = no.getItemMeta();
        no_meta.setDisplayName(ChatColor.DARK_RED + "Нет");
        no.setItemMeta(no_meta);
        setFillerGlass();

        inventory.setItem(3, yes);
        inventory.setItem(5, no);

    }
}
