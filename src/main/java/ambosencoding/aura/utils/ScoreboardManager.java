package ambosencoding.aura.utils;

import ambosencoding.aura.Aura;
import ambosencoding.aura.Team;
import com.google.common.collect.Maps;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import java.util.Map;

public class ScoreboardManager {

    private static final Map<Team, Score> SCORES = Maps.newHashMap();
    private static Scoreboard ingameScoreboard;

    public static void lobbyScoreboard() {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective objective = scoreboard.registerNewObjective("dummy", "lobby");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName("§lAURA");
        Score map = objective.getScore("§eMap");
        map.setScore(2);
        Score teams = objective.getScore("§b" + Aura.MAP);
        teams.setScore(1);

        Bukkit.getOnlinePlayers().forEach(p -> p.setScoreboard(scoreboard));
    }

    public static void ingameScoreboard() {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective objective = scoreboard.registerNewObjective("dummy", "ingame");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName("§e§lAURA");

        for (ChatColor color : Team.getTeams().keySet()) {
            Team team = Team.getTeams().get(color);
            org.bukkit.scoreboard.Team sbTeam = scoreboard.registerNewTeam(ChatColor.stripColor(team.getName()));
            sbTeam.setPrefix(team.getColor().toString());

            Score score = objective.getScore(team.getName());
            score.setScore(0);
            if (team.getPlayer() != null) {
                sbTeam.addEntry(team.getPlayer().getName());
                score.setScore(1);
            }

            SCORES.put(team, score);
        }

        Bukkit.getOnlinePlayers().forEach(p -> p.setScoreboard(scoreboard));
        ingameScoreboard = scoreboard;
    }

    public static void spectatorJoined(Player p) {
        p.setScoreboard(ingameScoreboard);
    }

    public static void death(Player p) {
        if (Team.getTeam(p) == null) {
            return;
        }
        SCORES.get(Team.getTeam(p)).setScore(0);
    }

}
