package nl.infinityastro.asteroreceives;

import nl.infinityastro.asteroreceives.menusystem.PaginatedMenu;
import nl.infinityastro.asteroreceives.menusystem.PlayerMenuUtility;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;

public class ReceivesMenu extends PaginatedMenu {

    DatabaseManager databaseManager = AsteroReceives.getPlugin(AsteroReceives.class).getDatabaseManager();

    public ReceivesMenu(PlayerMenuUtility playerMenuUtility) {
        super(playerMenuUtility);
    }

    @Override
    public String getMenuName() {
        return "Receive Menu";
    }

    @Override
    public int getSlots() {
        return 54; // We gebruiken hier een standaard van 54 slots voor een dubbele kist.
    }

    @Override
    public void handleMenu(InventoryClickEvent e) {
        ItemStack clickedItem = e.getCurrentItem();
        if (clickedItem == null || clickedItem.getType() == Material.AIR) return;

        // Check welke actie er is geklikt en voer de bijbehorende actie uit
        switch (clickedItem.getType()) {
            case DARK_OAK_BUTTON:
                if (clickedItem.getItemMeta().getDisplayName().equals(ChatColor.GREEN + "Left")) {
                    if (page > 0) {
                        page--;
                        setMenuItems();
                    }
                } else if (clickedItem.getItemMeta().getDisplayName().equals(ChatColor.GREEN + "Right")) {
                    if ((index + 1) < databaseManager.getReceives(playerMenuUtility.getTarget()).size()) {
                        page++;
                        setMenuItems();
                    }
                }
                break;
            case BARRIER:
                if (clickedItem.getItemMeta().getDisplayName().equals(ChatColor.DARK_RED + "Close")) {
                    playerMenuUtility.getOwner().closeInventory();
                }
                break;
            case PAPER:
                e.getWhoClicked().sendMessage("§aReceive '" + e.getCurrentItem().getItemMeta().getDisplayName() + "' verwijdert van speler " + Bukkit.getPlayer(playerMenuUtility.getTarget()).getName());
                removeReceive(playerMenuUtility.getTarget(), e.getCurrentItem().getItemMeta().getDisplayName());
                super.open();
            default:
                break;
        }
    }

    @Override
    public void setMenuItems() {
        addMenuBorder();

        List<String> receives = databaseManager.getReceives(playerMenuUtility.getTarget());

        int startIndex = page * maxItemsPerPage;
        int endIndex = Math.min(startIndex + maxItemsPerPage, receives.size());

        for (int i = startIndex; i < endIndex; i++) {
            String receive = receives.get(i);
            ItemStack receiveItem = makeItem(Material.PAPER, receive);
            inventory.addItem(receiveItem);
        }

        setFillerGlass();
    }

private void removeReceive(UUID uuid, String receive) {
    Player targetPlayer = AsteroReceives.getPlugin(AsteroReceives.class).getServer().getPlayer(uuid);
    if (targetPlayer == null) {
        AsteroReceives.getPlugin(AsteroReceives.class).getLogger().warning("Speler met UUID " + uuid + " is niet online.");
        return;
    }

    if (databaseManager.removeReceive(targetPlayer.getUniqueId(), receive)) {
        AsteroReceives.getPlugin(AsteroReceives.class).getLogger().info("Receive '" + receive + "' verwijderd van speler" + targetPlayer.getName());
    } else {
        AsteroReceives.getPlugin(AsteroReceives.class).getLogger().info("Speler " + targetPlayer.getName() + " heeft geen receive met de naam '" + receive + "'.");
    }
}
}
