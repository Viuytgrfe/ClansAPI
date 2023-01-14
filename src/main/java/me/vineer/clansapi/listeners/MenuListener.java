package me.vineer.clansapi.listeners;

import me.vineer.clansapi.menu.Menu;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryHolder;

public class MenuListener implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        if(event.getClickedInventory() == null) return;
        if(event.getCurrentItem() == null) return;
        InventoryHolder holder = event.getClickedInventory().getHolder();
        if(holder instanceof Menu) {
            event.setCancelled(true);
            Menu menu = (Menu) holder;
            menu.handleMenu(event);
        }
    }
}