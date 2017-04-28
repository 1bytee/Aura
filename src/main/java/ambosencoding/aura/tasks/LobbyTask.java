package ambosencoding.aura.tasks;

import ambosencoding.aura.Aura;
import ambosencoding.aura.ItemList;
import ambosencoding.aura.Team;
import ambosencoding.aura.utils.GameState;
import ambosencoding.aura.utils.LocationManager;
import ambosencoding.aura.utils.ScoreboardManager;
import ambosencoding.aura.utils.Title;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class LobbyTask extends AbstractTask {

    int cooldown = 30;

    @Override
    public void run() {
        if (Bukkit.getOnlinePlayers().size() < 2 && cooldown <= 30) {
            cooldown = 30;
        } else if (cooldown != 0) {
            if (cooldown == 30 || cooldown == 10) {
                broadcast("Spiel startet in §e" + cooldown + " §7Sekunden.");
            }

            if (cooldown == 10) {
                broadcast("Map: §e" + Aura.MAP);
            }

            if (cooldown <= 5 && cooldown >= 1) {
                broadcast("Spiel startet in §e" + cooldown + "§7 Sekunde" + (cooldown == 0 ? "." : "n."));
                sendTitle();
            }
            cooldown--;
        } else {
            Aura.INGAME.addAll(Bukkit.getOnlinePlayers());
            final int[] i = {1};
            Aura.INGAME.forEach(player -> {
                PlayerInventory inv = player.getInventory();
                inv.setContents(new ItemStack[0]);

                inv.setHelmet(ItemList.HELMET.get());
                inv.setChestplate(ItemList.CHESTPLATE.get());
                inv.setLeggings(ItemList.LEGGINGS.get());
                inv.setBoots(ItemList.BOOTS.get());

                inv.addItem(
                        ItemList.KNOCKBACK_STICK.get(),
                        ItemList.TELEPORT_PEARL.get(64),
                        ItemList.PUMPKIN_PIE.get(32),
                        ItemList.ROD.get(),
                        ItemList.HEALING_POTION.get(5),
                        ItemList.POISION_POTION.get(),
                        ItemList.REGENERATION_POTION.get(2),
                        ItemList.HARMING_POTION.get(5),
                        ItemList.EGG.get(5)
                );

                //TODO stats

                if (Team.getTeam(player) == null) {
                    Team team = Team.nextFreeTeam();
                    team.setPlayer(player);
                    player.sendMessage(Aura.PREFIX + "Du bist jetzt in Team " + team.getName());
                }

                player.teleport(LocationManager.getMapSpawn(Aura.MAP_ID, i[0]));

                i[0]++;
            });
            cancel();
            Aura.CURRENT_TASK = new IngameTask();
            Aura.STATE = GameState.INGAME;
            ScoreboardManager.ingameScoreboard();
            broadcast("Spiel wurde gestartet.");
        }
    }

    private void sendTitle() {
        Title.builder()
                .setTimes(5, 12, 3)
                .setTitle(color().toString() + cooldown)
                .sendAll();
    }

    private ChatColor color() {
        switch (cooldown) {
            case 5:
                return ChatColor.DARK_RED;
            case 4:
                return ChatColor.RED;
            case 3:
                return ChatColor.GOLD;
            case 2:
                return ChatColor.YELLOW;
            case 1:
                return ChatColor.GREEN;
            default:
                return ChatColor.WHITE;
        }
    }
}
