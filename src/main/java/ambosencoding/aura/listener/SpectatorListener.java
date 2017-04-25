package ambosencoding.aura.listener;

import ambosencoding.aura.Aura;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Witch;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SpectatorListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPickup(PlayerPickupItemEvent e) {
        if (isSpectating(e.getPlayer())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent e) {
        if (isSpectating(e.getPlayer())) {
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockPlace(BlockPlaceEvent e) {
        if (isSpectating(e.getPlayer())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player) {
            Player p = (Player) e.getEntity();
            if (isSpectating(p)) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockDamage(BlockDamageEvent e) {
        if (isSpectating(e.getPlayer())) {
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent e) {
        if (isSpectating(e.getPlayer())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityInteract(PlayerInteractEntityEvent e) {
        Player p = e.getPlayer();
        if (isSpectating(p)) {
            if (e.getRightClicked() instanceof Witch) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent e) {
        Player p = (Player) e.getPlayer();
        if (isSpectating(p)) {
            InventoryHolder holder = e.getInventory().getHolder();
            if (holder instanceof Chest || holder instanceof DoubleChest) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent e) {
        Player p;
        if (e.getEntity() instanceof Player) {
            p = (Player) e.getEntity();
            if (isSpectating(p)) {
                e.setCancelled(true);
                e.setDamage(0D);
            }
        } else return;
        if (e.getDamager().getType() == EntityType.FISHING_HOOK || e.getDamager().getType() == EntityType.ARROW) {
            if (isSpectating(p)) {
                e.setCancelled(true);
                e.setDamage(0D);
            }
        } else if (e.getDamager() instanceof Player) {
            if (isSpectating((Player) e.getDamager())) {
                e.setCancelled(true);
                e.setDamage(0D);
            }
        }
    }

    private boolean isSpectating(Player p) {
        return Aura.SPECTATING.contains(p);
    }

    public static class SpectatorCompass implements Listener {

        private List<Player> players = new ArrayList<>();

        @EventHandler
        public void onInteract(PlayerInteractEvent e) {
            Player p = e.getPlayer();
            if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                if (p.getItemInHand().getType() == Material.COMPASS) {
                    openInventory(p);
                }
            }
        }

        private void openInventory(Player p) {
            if (players.contains(p))
                players.remove(p);

            int i = 9;
            while (i < Aura.INGAME.size())
                i++;
            Inventory inv = Bukkit.createInventory(null, i, "§7Wem möchtest du zuschauen?");
            for (Player p_ : Aura.INGAME) {
                ItemStack item = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
                ItemMeta meta = item.getItemMeta();
                meta.setDisplayName("§e" + p_.getDisplayName());
                meta.setLore(Collections.singletonList("§7Klicke um zuzuschauen"));
                item.setItemMeta(meta);
                inv.addItem(item);
            }

            p.openInventory(inv);
            players.add(p);
        }

        @EventHandler
        public void onClick(InventoryClickEvent e) {
            if (!(e.getWhoClicked() instanceof Player)) return;
            Player p = (Player) e.getWhoClicked();

            if (players.contains(p)) {
                if (e.getSlot() == e.getRawSlot()) {
                    e.setCancelled(true);
                    p.updateInventory();
                    if (e.getCurrentItem().getType() == Material.SKULL_ITEM) {
                        String name = ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName());
                        Player target = Bukkit.getPlayer(name);
                        if (target == null) {
                            for (Player player : Bukkit.getOnlinePlayers()) {
                                if (p.getDisplayName().equals(name)) {
                                    target = player;
                                    break;
                                }
                            }
                        }
                        if (target != null && target.isOnline()) {
                            closeInventory(p);
                            p.teleport(target.getLocation().add(0, 10, 0));
                            p.sendMessage(Aura.PREFIX + "Du schaust jetzt §e" + target.getDisplayName() + " §7zu.");
                        } else {
                            closeInventory(p);
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

}
