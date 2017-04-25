package ambosencoding.aura.commands;

import ambosencoding.aura.Aura;
import com.google.common.base.Joiner;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.stream.Collectors;

@CommandHandler(name = "world", description = "Um die Welten zu wechseln")
public class WorldCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!(sender instanceof Player))
            return true;

        Player p = (Player) sender;

        if (!p.hasPermission("aura.admin")) {
            p.sendMessage(Aura.PREFIX + "Du hast keine Rechte um den Befehl auszuführen.");
            return true;
        }

        Iterable<String> worlds = Bukkit.getWorlds().stream().map(World::getName).collect(Collectors.toList());

        if (args.length != 1) {
            p.sendMessage(Aura.PREFIX + "Maps: §e" + Joiner.on(", ").join(worlds));
            return true;
        }

        World world = Bukkit.getWorld(args[0]);

        if (world == null) {
            p.sendMessage(Aura.PREFIX + "Map " + args[0] + " existiert nicht.");
            p.sendMessage(Aura.PREFIX + "Maps: §e" + Joiner.on(", ").join(worlds));
            return true;
        }

        p.teleport(world.getSpawnLocation());
        return true;
    }
}
