package ambosencoding.aura.listener;

import ambosencoding.aura.Aura;
import ambosencoding.aura.ItemList;
import ambosencoding.aura.Team;
import ambosencoding.aura.database.DatabaseConnection;
import ambosencoding.aura.database.Stats;
import ambosencoding.aura.utils.GameState;
import ambosencoding.aura.utils.LocationManager;
import ambosencoding.aura.utils.ScoreboardManager;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;

public class PlayerListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();

        if (p.isDead()) {
            p.spigot().respawn();
        }

        DatabaseConnection.initPlayer(p);
        //load stats
        Stats.get(p);

        if (Aura.STATE == GameState.LOBBY) {
            e.setJoinMessage(Aura.PREFIX + "§f" + p.getName() + " §7ist beigetreten.");
            p.getActivePotionEffects().forEach(potionEffect -> p.removePotionEffect(potionEffect.getType()));
            p.setHealth(20D);
            p.setFoodLevel(20);
            p.setFireTicks(0);
            p.setFlying(false);
            p.setAllowFlight(false);
            p.getInventory().clear();
            p.getInventory().setArmorContents(new ItemStack[4]);
            p.getInventory().addItem(ItemList.TEAM_SELECTOR.get());
            p.setGameMode(GameMode.SURVIVAL);
            ScoreboardManager.lobbyScoreboard();
        } else {
            ScoreboardManager.spectatorJoined(p);
            Aura.setToSpectator(p, false);
            e.setJoinMessage("");
        }
        if (LocationManager.getSpawn("spawn") != null) {
            p.teleport(LocationManager.getSpawn("spawn"));
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();

        if (Aura.STATE != GameState.INGAME) {
            e.setQuitMessage(Aura.PREFIX + "§f" + p.getName() + " §7hat das Spiel verlassen.");
            Team team = Team.getTeam(p);
            if (team != null) {
                team.reset();
            }
        } else {
            if (Aura.INGAME.contains(p)) {
                ScoreboardManager.death(p);
                e.setQuitMessage(Aura.PREFIX + "§f" + p.getName() + " §7hat das Spiel verlassen.");
                Aura.INGAME.remove(p);
                Stats.get(p).addDeath();
            } else if (Aura.SPECTATING.contains(p)) {
                Aura.SPECTATING.remove(p);
            }
        }
    }

    @EventHandler
    public void onKick(PlayerKickEvent e) {
        Player p = e.getPlayer();

        if (Aura.STATE != GameState.INGAME) {
            e.setLeaveMessage(Aura.PREFIX + "§f" + p.getName() + " §7hat das Spiel verlassen.");
        } else {
            if (Aura.INGAME.contains(p)) {
                ScoreboardManager.death(p);
                e.setLeaveMessage(Aura.PREFIX + "§f" + p.getName() + " §7hat das Spiel verlassen.");
                Aura.INGAME.remove(p);
                Stats.get(p).addDeath();
            } else if (Aura.SPECTATING.contains(p)) {
                Aura.SPECTATING.remove(p);
            }
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        e.setCancelled(true);
        if (Aura.STATE != GameState.INGAME) {
            Bukkit.broadcastMessage("§f" + p.getDisplayName() + " §8§l➜ §r§7" + e.getMessage());
        } else {
            if (Aura.INGAME.contains(p)) {
                Bukkit.broadcastMessage("§f" + p.getDisplayName() + " §8§l➜ §r§7" + e.getMessage());
            } else {
                Aura.SPECTATING.forEach(player -> player.sendMessage("§c§l[SPEC] " + "§f" + p.getDisplayName() + " §8§l➜ §r§7" + e.getMessage()));
            }
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent e) {
        if (Aura.STATE == GameState.RESTARTING || Aura.STATE == GameState.LOBBY) {
            e.setCancelled(true);
            return;
        }

        if (e.getEntity() instanceof Player) {
            Player p = (Player) e.getEntity();
            Player killer = null;
            if (e.getFinalDamage() > p.getHealth()) {

                p.setHealth(20D);
                p.setFoodLevel(20);

                p.getInventory().clear();
                p.getInventory().setArmorContents(new ItemStack[4]);

                e.setCancelled(true);
                e.setDamage(0D);

                if (p.getLastDamageCause() instanceof EntityDamageByEntityEvent) {
                    EntityDamageByEntityEvent event = (EntityDamageByEntityEvent) p.getLastDamageCause();
                    if (event.getDamager() instanceof Player) {
                        Bukkit.broadcastMessage(Aura.PREFIX + "Spieler §e" + p.getDisplayName() + " §7wurde von §e" + ((Player) event.getDamager()).getDisplayName() + "§7 getötet.");
                        killer = (Player) event.getDamager();
                    } else if (event.getDamager() instanceof Arrow) {
                        Arrow arrow = (Arrow) event.getDamager();
                        if (arrow.getShooter() instanceof Player) {
                            Bukkit.broadcastMessage(Aura.PREFIX + "Spieler §e" + p.getDisplayName() + " §7wurde von §e" + ((Player) arrow.getShooter()).getDisplayName() + "§7 getötet.");
                            killer = (Player) arrow.getShooter();
                        } else {
                            Bukkit.broadcastMessage(Aura.PREFIX + "Spieler §e" + p.getDisplayName() + " §7ist gestorben.");
                        }
                    } else if (event.getDamager() instanceof FishHook) {
                        FishHook hook = (FishHook) event.getDamager();
                        if (hook.getShooter() instanceof Player) {
                            Bukkit.broadcastMessage(Aura.PREFIX + "Spieler §e" + p.getDisplayName() + " §7wurde von §e" + ((Player) hook.getShooter()).getDisplayName() + "§7 getötet.");
                            killer = (Player) hook.getShooter();
                        } else {
                            Bukkit.broadcastMessage(Aura.PREFIX + "Spieler §e" + p.getDisplayName() + " §7ist gestorben.");
                        }
                    } else if (event.getDamager() instanceof ThrownPotion) {
                        ThrownPotion potion = (ThrownPotion) event.getDamager();
                        if (potion.getShooter() instanceof Player) {
                            Bukkit.broadcastMessage(Aura.PREFIX + "Spieler §e" + p.getDisplayName() + " §7wurde von §e" + ((Player) potion.getShooter()).getDisplayName() + "§7 getötet.");
                            killer = (Player) potion.getShooter();
                        } else {
                            Bukkit.broadcastMessage(Aura.PREFIX + "Spieler §e" + p.getDisplayName() + " §7ist gestorben.");
                        }
                    } else {
                        Bukkit.broadcastMessage(Aura.PREFIX + "Spieler §e" + p.getDisplayName() + " §7ist gestorben.");
                    }
                } else {
                    Bukkit.broadcastMessage(Aura.PREFIX + "Spieler §e" + p.getDisplayName() + " §7ist gestorben.");
                }
                Stats.get(p).addDeath();

                if (killer != null) {
                    Stats.get(killer).addKill();
                }

                Aura.setToSpectator(p, true);
                ScoreboardManager.death(p);
            }
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        e.setDeathMessage(null);
        Player p = e.getEntity();
        Player killer = null;

        if (p.getLastDamageCause() instanceof EntityDamageByEntityEvent) {
            EntityDamageByEntityEvent event = (EntityDamageByEntityEvent) p.getLastDamageCause();
            if (event.getDamager() instanceof Player) {
                Bukkit.broadcastMessage(Aura.PREFIX + "Spieler §e" + p.getDisplayName() + " §7wurde von §e" + ((Player) event.getDamager()).getDisplayName() + "§7 getötet.");
                killer = (Player) event.getDamager();
            } else if (event.getDamager() instanceof Arrow) {
                Arrow arrow = (Arrow) event.getDamager();
                if (arrow.getShooter() instanceof Player) {
                    Bukkit.broadcastMessage(Aura.PREFIX + "Spieler §e" + p.getDisplayName() + " §7wurde von §e" + ((Player) arrow.getShooter()).getDisplayName() + "§7 getötet.");
                    killer = (Player) arrow.getShooter();
                } else {
                    Bukkit.broadcastMessage(Aura.PREFIX + "Spieler §e" + p.getDisplayName() + " §7ist gestorben.");
                }
            } else if (event.getDamager() instanceof FishHook) {
                FishHook hook = (FishHook) event.getDamager();
                if (hook.getShooter() instanceof Player) {
                    Bukkit.broadcastMessage(Aura.PREFIX + "Spieler §e" + p.getDisplayName() + " §7wurde von §e" + ((Player) hook.getShooter()).getDisplayName() + "§7 getötet.");
                    killer = (Player) hook.getShooter();
                } else {
                    Bukkit.broadcastMessage(Aura.PREFIX + "Spieler §e" + p.getDisplayName() + " §7ist gestorben.");
                }
            } else if (p.getKiller() != null){
                Bukkit.broadcastMessage(Aura.PREFIX + "Spieler §e" + p.getDisplayName() + " §7wurde von §e" + p.getKiller() + "§7 getötet.");
                killer = p.getKiller();
            } else {
                Bukkit.broadcastMessage(Aura.PREFIX + "Spieler §e" + p.getDisplayName() + " §7ist gestorben.");
            }
        }

        e.getDrops().clear();
        e.setDroppedExp(0);
        Stats.get(p).addDeath();

        if (p.getKiller() != null) {
            killer = p.getKiller();
        }

        if (killer != null) {
            Stats.get(killer).addKill();
        }

        p.spigot().respawn();

        Aura.setToSpectator(p, false);
        ScoreboardManager.death(p);
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent e) {
        e.setRespawnLocation(LocationManager.getSpawn("spawn"));
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
        if (Aura.STATE == GameState.LOBBY || Aura.STATE == GameState.ENDING || Aura.STATE == GameState.RESTARTING) {
            e.setCancelled(true);
            return;
        }

        if (!(e.getEntity() instanceof Player)) {
            e.setCancelled(true);
            e.setDamage(0D);
            return;
        }

        Player p = (Player) e.getEntity();
        Player killer = null;

        if (e.getFinalDamage() > p.getHealth()) {
            p.setHealth(20D);
            p.setFoodLevel(20);

            p.getInventory().clear();
            p.getInventory().setArmorContents(new ItemStack[4]);

            e.setCancelled(true);
            e.setDamage(0D);

            if (e.getDamager() instanceof Player) {
                Bukkit.broadcastMessage(Aura.PREFIX + "Spieler §e" + p.getDisplayName() + " §7wurde von §e" + ((Player) e.getDamager()).getDisplayName() + "§7 getötet.");
                killer = (Player) e.getDamager();
            } else if (e.getDamager() instanceof Arrow) {
                Arrow arrow = (Arrow) e.getDamager();
                if (arrow.getShooter() instanceof Player) {
                    Bukkit.broadcastMessage(Aura.PREFIX + "Spieler §e" + p.getDisplayName() + " §7wurde von §e" + ((Player) arrow.getShooter()).getDisplayName() + "§7 getötet.");
                    killer = (Player) arrow.getShooter();
                } else {
                    Bukkit.broadcastMessage(Aura.PREFIX + "Spieler §e" + p.getDisplayName() + " §7ist gestorben.");
                }
            } else if (e.getDamager() instanceof FishHook) {
                FishHook hook = (FishHook) e.getDamager();
                if (hook.getShooter() instanceof Player) {
                    Bukkit.broadcastMessage(Aura.PREFIX + "Spieler §e" + p.getDisplayName() + " §7wurde von §e" + ((Player) hook.getShooter()).getDisplayName() + "§7 getötet.");
                    killer = (Player) hook.getShooter();
                } else {
                    Bukkit.broadcastMessage(Aura.PREFIX + "Spieler §e" + p.getDisplayName() + " §7ist gestorben.");
                }
            } else if (e.getDamager() instanceof ThrownPotion) {
                ThrownPotion potion = (ThrownPotion) e.getDamager();
                if (potion.getShooter() instanceof Player) {
                    Bukkit.broadcastMessage(Aura.PREFIX + "Spieler §e" + p.getDisplayName() + " §7wurde von §e" + ((Player) potion.getShooter()).getDisplayName() + "§7 getötet.");
                    killer = (Player) potion.getShooter();
                } else {
                    Bukkit.broadcastMessage(Aura.PREFIX + "Spieler §e" + p.getDisplayName() + " §7ist gestorben.");
                }
            } else {
                Bukkit.broadcastMessage(Aura.PREFIX + "Spieler §e" + p.getDisplayName() + " §7ist gestorben.");
            }
            Stats.get(p).addDeath();

            if (killer != null) {
                Stats.get(killer).addKill();
            }

            Aura.setToSpectator(p, true);
            ScoreboardManager.death(p);
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        Player killer = null;

        if (Aura.STATE != GameState.INGAME) {
            return;
        }

        if (p.getLocation().getY() < -23) {
            p.setFallDistance(-100F);

            p.setHealth(20D);
            p.setFoodLevel(20);
            p.getInventory().clear();
            p.getInventory().setArmorContents(new ItemStack[4]);

            if (p.getLastDamageCause() instanceof EntityDamageByEntityEvent) {
                EntityDamageByEntityEvent event = (EntityDamageByEntityEvent) p.getLastDamageCause();
                if (event.getDamager() instanceof Player) {
                    Bukkit.broadcastMessage(Aura.PREFIX + "Spieler §e" + p.getDisplayName() + " §7wurde von §e" + ((Player) event.getDamager()).getDisplayName() + "§7 getötet.");
                    killer = (Player) event.getDamager();
                } else if (event.getDamager() instanceof Arrow) {
                    Arrow arrow = (Arrow) event.getDamager();
                    if (arrow.getShooter() instanceof Player) {
                        Bukkit.broadcastMessage(Aura.PREFIX + "Spieler §e" + p.getDisplayName() + " §7wurde von §e" + ((Player) arrow.getShooter()).getDisplayName() + "§7 getötet.");
                        killer = (Player) arrow.getShooter();
                    } else {
                        Bukkit.broadcastMessage(Aura.PREFIX + "Spieler §e" + p.getDisplayName() + " §7ist gestorben.");
                    }
                } else if (event.getDamager() instanceof FishHook) {
                    FishHook hook = (FishHook) event.getDamager();
                    if (hook.getShooter() instanceof Player) {
                        Bukkit.broadcastMessage(Aura.PREFIX + "Spieler §e" + p.getDisplayName() + " §7wurde von §e" + ((Player) hook.getShooter()).getDisplayName() + "§7 getötet.");
                        killer = (Player) hook.getShooter();
                    } else {
                        Bukkit.broadcastMessage(Aura.PREFIX + "Spieler §e" + p.getDisplayName() + " §7ist gestorben.");
                    }
                } else {
                    Bukkit.broadcastMessage(Aura.PREFIX + "Spieler §e" + p.getDisplayName() + " §7ist gestorben.");
                }
            }
            Stats.get(p).addDeath();

            if (killer != null) {
                Stats.get(p).addKill();
            }

            p.teleport(LocationManager.getSpawn("spawn"));
            Aura.setToSpectator(p, false);
            ScoreboardManager.death(p);
        }
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent e) {
        Player p = (Player) e.getEntity();
        if (Aura.STATE != GameState.INGAME) {
            e.setCancelled(true);
            e.setFoodLevel(20);
        } else {
            p.setSaturation(5f);
        }
    }
}
