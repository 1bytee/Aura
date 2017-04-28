package ambosencoding.aura.commands;

import ambosencoding.aura.Aura;
import ambosencoding.aura.utils.LocationManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandHandler(name = "setlobbyspawn", description = "Den Lobby Spawn setzen")
public class SetLobbySpawn implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!(sender instanceof Player))
            return true;

        Player p = (Player) sender;

        if (!p.hasPermission("aura.admin")) {
            p.sendMessage(Aura.PREFIX + "Du hast keine Rechte um den Befehl auszuführen.");
            return true;
        }

        LocationManager.setSpawn("spawn", p.getLocation());
        p.sendMessage(Aura.PREFIX + "Spawn wurde gesetzt.");
        return true;
    }
}
