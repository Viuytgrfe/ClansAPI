package me.vineer.clansapi.menu.custom.acceptMenu;

import me.vineer.clansapi.PlayerMenuUtility;
import me.vineer.clansapi.menu.Menu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collections;

public class AcceptMenu extends Menu {

    private ChoiceDecision decision;
    private String[] text;

    public AcceptMenu(PlayerMenuUtility playerMenuUtility, ChoiceDecision decision) {
        super(playerMenuUtility);
        this.decision = decision;
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
            decision.caseYes();
            event.getWhoClicked().closeInventory();
        } else if (event.getCurrentItem().getType() == Material.BARRIER) {
            decision.caseNo();
            event.getWhoClicked().closeInventory();
        }
    }

    @Override
    public void setMenuItems() {
        ItemStack yes = new ItemStack(Material.EMERALD, 1);
        ItemMeta yes_meta = yes.getItemMeta();
        yes_meta.setDisplayName(ChatColor.GREEN + "Да");
        ArrayList<String> yes_lore = new ArrayList<>();
        Collections.addAll(yes_lore, text);
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

    public void openWithText(String... text) {
        this.text = text;
        open();
    }
}
