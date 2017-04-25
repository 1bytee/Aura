package ambosencoding.aura.database;

import ambosencoding.aura.Aura;
import org.bukkit.entity.Player;

import java.sql.*;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DatabaseConnection {

    private static String host = Aura.getInstance().getConfig().getString("MySQL.Host");
    private static String port = Aura.getInstance().getConfig().getString("MySQL.Port");
    private static String database = Aura.getInstance().getConfig().getString("MySQL.Datenbank");
    private static String username = Aura.getInstance().getConfig().getString("MySQL.Benutzername");
    private static String password = Aura.getInstance().getConfig().getString("MySQL.Passwort");

    private static Connection connection;
    private static ExecutorService service = Executors.newCachedThreadPool();

    public static Connection getConnection() {
        connect();
        return connection;
    }

    public static boolean isConnected() {
        return (connection != null);
    }

    public static void connect() {
        if (!isConnected()) {
            try {
                connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, username, password);
                createTableIfNotExists();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void disconnect() {
        if (isConnected()) {
            try {
                getConnection().close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void createTableIfNotExists() {
        if (isConnected()) {
            try {
                PreparedStatement ps = getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS Aura (UUID VARCHAR(100), Spielername VARCHAR(100), Kills INT, Tode INT, Spiele INT, Gewonnen INT)");
                ps.executeUpdate();
                ps.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void initPlayer(Player p) {
        Callable<Void> callable = () -> {
            if (!isPlayerExisting(p.getUniqueId())) {
                registerPlayer(p.getUniqueId(), p.getName());
            } else {
                updateName(p.getUniqueId(), p.getName());
            }
            return null;
        };
        try {
            service.submit(callable).get();
        } catch (InterruptedException | ExecutionException ignored) {
        }
    }

    public static void registerPlayer(UUID uuid, String name) {
        try {
            PreparedStatement ps = getConnection().prepareStatement("INSERT INTO Aura (UUID, Spielername, Kills, Tode, Spiele, Gewonnen) VALUES (?, ?, 0, 0, 0, 0)");
            ps.setString(1, uuid.toString());
            ps.setString(2, name);
            ps.execute();
            ps.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static boolean isPlayerExisting(UUID uuid) {
        try {
            PreparedStatement ps = getConnection().prepareStatement("SELECT UUID FROM Aura WHERE UUID = ?");
            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();
            boolean isExisting = rs.next();
            rs.close();
            ps.close();
            return isExisting;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public static void updateName(UUID uuid, String name) {
        try {
            PreparedStatement ps = getConnection().prepareStatement("UPDATE Aura SET Spielername = ? WHERE UUID = ?");
            ps.setString(1, name);
            ps.setString(2, uuid.toString());
            ps.executeUpdate();
            ps.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
