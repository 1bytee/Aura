package ambosencoding.aura.tasks;

import ambosencoding.aura.Aura;
import ambosencoding.aura.Team;
import ambosencoding.aura.database.Stats;
import ambosencoding.aura.utils.LocationManager;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class RestartTask {

    public static void execute() {
        Bukkit.getOnlinePlayers().forEach(p -> Bukkit.getOnlinePlayers().forEach(p_ -> p_.showPlayer(p)));
        broadcast("Das Spiel ist vorbei.");

        Bukkit.getOnlinePlayers().forEach(p -> {
            p.setAllowFlight(false);
            p.getInventory().clear();
            p.getInventory().setArmorContents(new ItemStack[4]);
            p.getActivePotionEffects().forEach(potionEffect -> p.removePotionEffect(potionEffect.getType()));
            p.setFireTicks(0);

            if (p.isFlying()) {
                p.teleport(LocationManager.getSpawn("spawn"));
            }
        });

        if (Aura.INGAME.size() != 1) {
            broadcast("Es gab keinen Gewinner.");
        } else {
            Player lastPlayer = Aura.INGAME.get(0);
            Team team = Team.getTeam(lastPlayer);
            Stats.get(lastPlayer).addWin();
            sendTitle(team.getColor() + lastPlayer.getName(), "§7hat gewonnen.");
            broadcast(team.getColor() + lastPlayer.getName() + " §7hat das Spiel gewonnen.");
        }

        Stats.getAllStats().forEach((uuid, stats) -> {
            stats.addGamesPlayed();
            stats.saveStats();
        });

        Bukkit.broadcastMessage("§cServer startet in 10 Sekunden neu.");
        Bukkit.getScheduler().scheduleSyncDelayedTask(Aura.getInstance(), () -> {
            Bukkit.broadcastMessage("§cServer startet neu...");
            Bukkit.getOnlinePlayers().forEach(o -> o.kickPlayer("§cServer startet neu..."));
            Bukkit.shutdown();
        }, 200L);
    }

    private static void broadcast(String message) {
        Bukkit.broadcastMessage(Aura.PREFIX + message);
    }

    private static void sendTitle(String title, String subtitle) {
        Bukkit.getOnlinePlayers().forEach(p -> sendTitle(p, title, subtitle));
    }

    private static void sendTitle(Player p, String title, String subtitle) {
        CraftPlayer cp = (CraftPlayer) p;
        PacketPlayOutTitle packetTitle = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + title + "\"}"));
        PacketPlayOutTitle packetSubtitle = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.SUBTITLE, IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + subtitle + "\"}"));
        PacketPlayOutTitle times = new PacketPlayOutTitle(10, 40, 10);
        cp.getHandle().playerConnection.sendPacket(times);
        cp.getHandle().playerConnection.sendPacket(packetTitle);
        cp.getHandle().playerConnection.sendPacket(packetSubtitle);
    }

}
