package ambosencoding.aura.listener;

import ambosencoding.aura.Aura;
import ambosencoding.aura.ItemList;
import ambosencoding.aura.Team;
import com.google.common.collect.Lists;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class TeamSelector implements Listener {

    private final List<Player> players = Lists.newArrayList();

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (p.getItemInHand().getType() == Material.BED) {
                openInventory(p);
            }
        }
    }

    private void openInventory(Player p) {
        if (players.contains(p)) {
            players.remove(p);
        }

        Inventory inv = Bukkit.createInventory(null, 18, "§8Wähle dein Team aus");
        int i = 1;
        for (ChatColor color : Team.getTeams().keySet()) {
            while (i == 4 || i == 8 || i == 9 || i == 13) {
                i++;
            }
            Team team = Team.getTeams().get(color);
            inv.setItem(i,
                    ItemList.MENU_WOOL.getWithDurabilityLoreAndName(
                            team.toColor(),
                            team.getName(),
                            "§3" + (team.getPlayer() == null ? "0" : "1") + "/1"
                    )
            );
            i++;
        }

        players.add(p);
        p.openInventory(inv);
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player)) {
            return;
        }
        Player p = (Player) e.getWhoClicked();

        if (players.contains(p)) {
            if (e.getSlot() == e.getRawSlot()) {
                e.setCancelled(true);
                p.updateInventory();
                if (e.getCurrentItem() != null) {
                    ItemStack item = e.getCurrentItem();
                    if (item.getType() == Material.WOOL) {
                        ItemMeta meta = item.getItemMeta();
                        String noColors = ChatColor.stripColor(meta.getLore().get(0));
                        boolean free = noColors.split("/")[0].equals("0");
                        if (free) {
                            Team before = Team.getTeam(p);
                            if (before != null) {
                                before.reset();
                            }
                            ChatColor teamColor = ChatColor.getByChar(meta.getDisplayName().charAt(1));
                            Team team = Team.getTeams().get(teamColor);
                            if (team.setPlayer(p)) {
                                closeInventory(p);
                                p.sendMessage(Aura.PREFIX + "Du bist jetzt in Team " + team.getName());
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onKick(PlayerQuitEvent e) {
        closeInventory(e.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        closeInventory(e.getPlayer());
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        Player p = (Player) e.getPlayer();
        if (players.contains(p)) {
            players.remove(p);
        }
    }

    private void closeInventory(Player p) {
        if (players.contains(p)) {
            players.remove(p);
            p.closeInventory();
        }
    }


}
