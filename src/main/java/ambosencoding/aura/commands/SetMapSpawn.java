package ambosencoding.aura.commands;

import ambosencoding.aura.Aura;
import ambosencoding.aura.utils.LocationManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandHandler(name = "setmapspawn", description = "Einen Map-Spawn setzen")
public class SetMapSpawn implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!(sender instanceof Player))
            return true;

        Player p = (Player) sender;

        if (!p.hasPermission("aura.admin")) {
            p.sendMessage(Aura.PREFIX + "Du hast keine Rechte um den Befehl auszuführen.");
            return true;
        }

        //setmapspawn <Map> <Spawn>

        if (args.length != 2) {
            p.sendMessage(Aura.PREFIX + "Benutze: /setmapspawn <Map> <Spawn>");
            return true;
        }

        int map = Integer.parseInt(args[0]);
        int spawn = Integer.parseInt(args[1]);

        LocationManager.setMapSpawn(map, spawn, p.getLocation());
        p.sendMessage(String.format(Aura.PREFIX + "Spawn wurde gesetzt. §e[Map: %d, Spawn: %d]", map, spawn));
        return true;
    }

}
