package ambosencoding.aura;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.bukkit.ChatColor.*;

@RequiredArgsConstructor
@Getter
public class Team {

    @Getter
    private static final Map<ChatColor, Team> teams = Maps.newLinkedHashMap();
    private Player player;
    private final ChatColor color;

    public boolean setPlayer(Player player) {
        if (this.player == null) {
            this.player = player;
            return true;
        }
        return false;
    }

    public void reset() {
        player = null;
    }

    public static void createTeams() {
        Preconditions.checkState(teams.size() == 0);

        List<ChatColor> colors = Arrays.asList(RED, GREEN, YELLOW, DARK_AQUA, LIGHT_PURPLE, GRAY, WHITE, AQUA, GOLD, DARK_GREEN, DARK_PURPLE, DARK_GRAY);
        colors.forEach(color -> teams.put(color, new Team(color)));
        System.out.println("Size " + teams.size());
    }

    public String getName() {
        String name;
        switch (color) {
            case YELLOW:
                name = "Gelb";
                break;
            case RED:
                name = "Rot";
                break;
            case GREEN:
                name = "Grün";
                break;
            case WHITE:
                name = "Weiss";
                break;
            case DARK_AQUA:
                name = "Cyan";
                break;
            case LIGHT_PURPLE:
                name = "Hell-Lila";
                break;
            case DARK_PURPLE:
                name = "Lila";
                break;
            case GRAY:
                name = "Grau";
                break;
            case DARK_GRAY:
                name = "Dunkel-Grau";
                break;
            case DARK_GREEN:
                name = "Dunkel-Grün";
                break;
            case AQUA:
                name = "Hell-Blau";
                break;
            case GOLD:
                name = "Orange";
                break;
            default:
                name = "Weiss";
                break;
        }
        return color + name;
    }

    public int toColor() {
        switch (color) {
            case RED:
                return 14;
            case GREEN:
                return 5;
            case YELLOW:
                return 4;
            case DARK_AQUA:
                return 9;
            case LIGHT_PURPLE:
                return 2;
            case GRAY:
                return 8;
            case AQUA:
                return 11;
            case GOLD:
                return 1;
            case DARK_GREEN:
                return 13;
            case DARK_PURPLE:
                return 10;
            case DARK_GRAY:
                return 7;
            default:
                return 0;
        }
    }

    public static Team getTeam(Player p) {
        for (ChatColor chatColor : teams.keySet()) {
            if (teams.get(chatColor).player == p) {
                return teams.get(chatColor);
            }
        }
        return null;
    }

    public static Team nextFreeTeam() {
        for (ChatColor chatColor : teams.keySet()) {
            if (teams.get(chatColor).player == null) {
                return teams.get(chatColor);
            }
        }
        return null;
    }

}
