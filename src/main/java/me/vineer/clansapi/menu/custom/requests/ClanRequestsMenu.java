package me.vineer.clansapi.menu.custom.requests;

import me.vineer.clansapi.ClansAPI;
import me.vineer.clansapi.PlayerMenuUtility;
import me.vineer.clansapi.database.ClansController;
import me.vineer.clansapi.heads.NickController;
import me.vineer.clansapi.menu.PaginatedMenu;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;

public class ClanRequestsMenu extends PaginatedMenu {

    private final String clanName;

    public ClanRequestsMenu(PlayerMenuUtility playerMenuUtility, String clanName) {
        super(playerMenuUtility);
        this.clanName = clanName;
    }

    @Override
    public String getMenuName() {
        return "Запросы в клан " + clanName;
    }

    @Override
    public int getSlots() {
        return 54;
    }

    @Override
    public void handleMenu(InventoryClickEvent event) {
        ArrayList<String> players = new ArrayList<>(ClansController.getClan(clanName).getRequests());
        Player p = (Player) event.getWhoClicked();
        if (event.getCurrentItem() == null) return;
        if(event.getCurrentItem().getType() == Material.PLAYER_HEAD) {
            event.setCancelled(true);
            PlayerMenuUtility playerMenuUtility = ClansAPI.getPlayerMenuUtility(p);
            playerMenuUtility.setPlayerFromRequest(NickController.getNickFromTabName(event.getCurrentItem().getItemMeta().getDisplayName()));
            playerMenuUtility.setClanName(clanName);
            new AcceptRequestMenu(playerMenuUtility).open();
        } else if(event.getCurrentItem().getType().equals(Material.DARK_OAK_BUTTON)){
            if (ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName()).equalsIgnoreCase("Предыдущая страница")){
                if (page == 0){
                    p.sendMessage(ChatColor.GRAY + "Вы уже на первой странице.");
                }else{
                    page = page - 1;
                    super.open();
                }
            }else if (ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName()).equalsIgnoreCase("Следующая страница")){
                if (!((index + 1) >= players.size())){
                    page = page + 1;
                    super.open();
                }else{
                    p.sendMessage(ChatColor.GRAY + "Вы на последней странице.");
                }
            }
        } else if (event.getCurrentItem().getType().equals(Material.BARRIER)) {

            //close inventory
            p.closeInventory();

        }


    }

    @Override
    public void setMenuItems() {
        ArrayList<String> players = new ArrayList<>(ClansController.getClan(clanName).getRequests());
        addMenuBorder();

        ///////////////////////////////////// Pagination loop template
        if(players != null && !players.isEmpty()) {
            for(int i = 0; i < getMaxItemsPerPage()-1; i++) {
                index = getMaxItemsPerPage() * page + i;
                if(index >= players.size()) break;
                if (players.get(index) != null) {
                    ///////////////////////////
                    //Create an item from our collection and place it into the inventory
                    ItemStack playerItem = new ItemStack(Material.PLAYER_HEAD, 1);
                    SkullMeta skullMeta = (SkullMeta) playerItem.getItemMeta();
                    skullMeta.setDisplayName(NickController.getTabNameFromNick(players.get(index)));
                    playerItem.setItemMeta(skullMeta);
                    inventory.addItem(playerItem);
                    ////////////////////////
                }
            }
        }
    }
}
