package ambosencoding.aura.tasks;

import ambosencoding.aura.Aura;
import lombok.AccessLevel;
import lombok.Getter;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public abstract class AbstractTask implements Runnable {

    @Getter(AccessLevel.PROTECTED)
    private final int PID;

    protected AbstractTask() {
        PID = Bukkit.getScheduler().scheduleSyncRepeatingTask(Aura.getInstance(), this, 0, 20L);
    }

    public void cancel() {
        Bukkit.getScheduler().cancelTask(PID);
    }

    protected void broadcast(String message) {
        Bukkit.broadcastMessage(Aura.PREFIX + message);
    }

    protected void sendBar(String content) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            CraftPlayer cp = (CraftPlayer) p;
            PacketPlayOutChat chat = new PacketPlayOutChat(IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + content + "\"}"), (byte) 2);
            cp.getHandle().playerConnection.sendPacket(chat);
        }
    }

}
