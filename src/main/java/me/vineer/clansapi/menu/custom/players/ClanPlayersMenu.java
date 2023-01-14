package me.vineer.clansapi.menu.custom.players;

import me.vineer.clansapi.ClansAPI;
import me.vineer.clansapi.PlayerMenuUtility;
import me.vineer.clansapi.clans.player.ClanPlayer;
import me.vineer.clansapi.database.ClansController;
import me.vineer.clansapi.heads.NickController;
import me.vineer.clansapi.menu.PaginatedMenu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;

public class ClanPlayersMenu extends PaginatedMenu {

    private final String clanName;

    public ClanPlayersMenu(PlayerMenuUtility playerMenuUtility, String clanName) {
        super(playerMenuUtility);
        this.clanName = clanName;
    }

    @Override
    public String getMenuName() {
        return "Игроки клана " + clanName;
    }

    @Override
    public int getSlots() {
        return 54;
    }

    @Override
    public void handleMenu(InventoryClickEvent event) {
        Player p = (Player) event.getWhoClicked();
        ArrayList<ClanPlayer> players = new ArrayList<>(ClansController.getClan(clanName).getPlayers());
        if(event.getCurrentItem() == null) return;
        if(event.getCurrentItem().getType() == Material.PLAYER_HEAD) {
            event.setCancelled(true);
            PlayerMenuUtility playerMenuUtility = ClansAPI.getPlayerMenuUtility(p);
            playerMenuUtility.setClanName(clanName);
            new ClanPlayerMenu(playerMenuUtility, event.getCurrentItem().getItemMeta().getDisplayName()).open();
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

        ArrayList<ClanPlayer> players = new ArrayList<>(ClansController.getClan(clanName).getPlayers());

        addMenuBorder();

        ///////////////////////////////////// Pagination loop template
        if(players != null && !players.isEmpty()) {
            for(int i = 0; i < getMaxItemsPerPage(); i++) {
                index = getMaxItemsPerPage() * page + i;
                if(index >= players.size()) break;
                if (players.get(index) != null) {
                    ///////////////////////////

                    //Create an item from our collection and place it into the inventory
                    ItemStack playerItem = new ItemStack(Material.PLAYER_HEAD, 1);
                    SkullMeta skullMeta = (SkullMeta) playerItem.getItemMeta();
                    List<String> lore = new ArrayList<>();
                    skullMeta.setDisplayName(NickController.getTabNameFromNick(players.get(index).getPlayerName()));
                    lore.add(ChatColor.GOLD + players.get(index).getRank().translateToRus());
                    skullMeta.setLore(lore);
                    playerItem.setItemMeta(skullMeta);

                    inventory.addItem(playerItem);

                    ////////////////////////
                }
            }
        }
    }
}
