package ambosencoding.aura.database;

import ambosencoding.aura.utils.UUIDManager;
import com.google.common.collect.Maps;
import javafx.util.Pair;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.bukkit.entity.Player;

import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Getter
@Setter
public class Stats {

    private static final DecimalFormat format;
    private static ExecutorService service = Executors.newCachedThreadPool();
    private static final Map<UUID, Stats> STATS = Maps.newHashMap();

    static {
        format = new DecimalFormat("#.##");
        format.setRoundingMode(RoundingMode.CEILING);
    }

    public static Stats get(Player p) {
        if (STATS.containsKey(p.getUniqueId())) {
            return STATS.get(p.getUniqueId());
        }
        Stats stats = new Stats(p.getUniqueId(), p.getName());
        stats.loadStats();
        STATS.put(p.getUniqueId(), stats);
        return stats;
    }

    public static Stats find(String name) {
        Pair<String, UUID> pair = UUIDManager.getUUIDAndName(name);
        if (pair == null) {
            return null;
        }
        if (STATS.containsKey(pair.getValue())) {
            return STATS.get(pair.getValue());
        }
        Stats stats = new Stats(pair.getValue(), pair.getKey());
        stats.loadStats();
        STATS.put(pair.getValue(), stats);
        return stats;
    }

    public static Map<UUID, Stats> getAllStats() {
        return STATS;
    }

    private String name;
    private UUID uniqueId;
    private int kills;
    private int deaths;
    private int gamesPlayed;
    private int won;

    private Stats(UUID uuid, String name) {
        this.uniqueId = uuid;
        this.name = name;
    }

    public double getKD() {
        return deaths == 0 ? kills : Double.valueOf(format.format(kills / deaths));
    }

    public void addKill() {
        setKills(getKills() + 1);
    }

    public void addDeath() {
        setDeaths(getDeaths() + 1);
    }

    public void addWin() {
        setWon(getWon() + 1);
    }

    public void addGamesPlayed() {
        setGamesPlayed(getGamesPlayed() + 1);
    }

    @SneakyThrows
    public void loadStats() {
        Callable<Void> callable = () -> {
            Connection connection = DatabaseConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM Aura WHERE UUID = ?");
            statement.setString(1, uniqueId.toString());
            ResultSet result = statement.executeQuery();
            if (result.next()) {
                name = result.getString("Spielername");
                kills = result.getInt("Kills");
                deaths = result.getInt("Tode");
                gamesPlayed = result.getInt("Spiele");
                won = result.getInt("Gewonnen");
            }
            result.close();
            statement.close();
            return null;
        };
        service.submit(callable).get();
    }

    public void saveStats() {
        service.execute(() -> {
            Connection connection = DatabaseConnection.getConnection();
            try {
                PreparedStatement statement = connection.prepareStatement("UPDATE Aura SET Kills = ?, Tode = ?, Spiele = ?, Gewonnen = ? WHERE UUID = ?");
                statement.setInt(1, kills);
                statement.setInt(2, deaths);
                statement.setInt(3, gamesPlayed);
                statement.setInt(4, won);
                statement.setString(5, uniqueId.toString());
                statement.executeUpdate();
                statement.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });
    }

}
