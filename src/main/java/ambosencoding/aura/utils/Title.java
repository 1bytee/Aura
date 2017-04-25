package ambosencoding.aura.utils;

import com.google.common.base.Preconditions;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
import net.minecraft.server.v1_8_R3.PlayerConnection;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Title {

    @Getter
    private String title;
    @Getter
    private String subtitle;

    private PacketPlayOutTitle packetTitle;
    private PacketPlayOutTitle packetSubtitle;
    private PacketPlayOutTitle times;

    private static final PacketPlayOutTitle CLEAR = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.CLEAR, null);
    private static final PacketPlayOutTitle RESET = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.RESET, null);

    public Title setTitle(String title) {
        this.title = title;
        packetTitle = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + title + "\"}"));
        return this;
    }

    public Title setSubtitle(String subtitle) {
        this.subtitle = subtitle;
        packetSubtitle = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.SUBTITLE, IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + subtitle + "\"}"));
        return this;
    }

    public Title setTimes(int fadeIn, int stay, int fadeOut) {
        times = new PacketPlayOutTitle(fadeIn, stay, fadeOut);
        return this;
    }

    public void sendPlayer(Player p) {
        Preconditions.checkNotNull(packetTitle);

        PlayerConnection connection = ((CraftPlayer) p).getHandle().playerConnection;

        connection.sendPacket(CLEAR);
        connection.sendPacket(RESET);

        connection.sendPacket(times);
        connection.sendPacket(packetTitle);
        if (packetSubtitle != null) {
            connection.sendPacket(packetSubtitle);
        }
    }

    public void sendAll() {
        Bukkit.getOnlinePlayers().forEach(this::sendPlayer);
    }

    public static Title builder() {
        return new Title();
    }

}
