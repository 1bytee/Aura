package ambosencoding.aura.commands;

import ambosencoding.aura.Aura;
import ambosencoding.aura.database.Stats;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandHandler(name = "stats", description = "Um die Stats eines Spielers zu sehen", aliases = {"records"})
public class StatsCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!(sender instanceof Player))
            return true;

        Player p = (Player) sender;

        if (args.length == 0) {
            displayStats(p, Stats.get(p));
        } else {
            Player target = Bukkit.getPlayer(args[0]);
            if (target != null && target.isOnline()) {
                displayStats(p, Stats.get(target));
            } else {
                Stats stats = Stats.find(args[0]);
                if (stats == null) {
                    p.sendMessage(Aura.PREFIX + "Konnte Stats von Spieler §e" + args[0] + " §7nicht finden.");
                } else {
                    displayStats(p, stats);
                }
            }
        }

        return true;
    }

    private void displayStats(Player p, Stats stats) {
        p.sendMessage(Aura.PREFIX + "§e" + stats.getName() + "'s §7Stats:");
        p.sendMessage(Aura.PREFIX + "Kills: §e" + stats.getKills());
        p.sendMessage(Aura.PREFIX + "Tode: §e" + stats.getDeaths());
        p.sendMessage(Aura.PREFIX + "K/D: §e" + stats.getKD());
        p.sendMessage(Aura.PREFIX + "Spiele gewonnen: §e" + stats.getWon());
        p.sendMessage(Aura.PREFIX + "Spiele gespielt: §e" + stats.getGamesPlayed());
    }
}
