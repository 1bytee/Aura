package ambosencoding.aura.utils;

import ambosencoding.aura.Aura;
import com.google.common.collect.Maps;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.util.NumberConversions;

import java.io.File;
import java.util.Map;

public class LocationManager {

    private static final Map<String, FileConfiguration> CONFIGURATIONS = Maps.newHashMap();

    public static FileConfiguration getConfiguration(String name) {
        if (CONFIGURATIONS.containsKey(name)) {
            return CONFIGURATIONS.get(name);
        }
        FileConfiguration configuration = YamlConfiguration.loadConfiguration(new File(Aura.getInstance().getDataFolder(), name + ".yml"));
        CONFIGURATIONS.put(name, configuration);
        return configuration;
    }

    @SneakyThrows
    public static void saveConfiguration(String name, FileConfiguration cfg) {
        cfg.save(new File(Aura.getInstance().getDataFolder(), name + ".yml"));
    }

    public static Location getSpawn(String location) {
        FileConfiguration cfg = getConfiguration("spawns");

        if (!cfg.contains(location)) {
            return null;
        }

        double x = cfg.getDouble(location + ".x");
        double y = cfg.getDouble(location + ".y");
        double z = cfg.getDouble(location + ".z");
        float yaw = NumberConversions.toFloat(cfg.get(location + ".yaw"));
        float pitch = NumberConversions.toFloat(cfg.get(location + ".pitch"));
        World world = Bukkit.getWorld(cfg.getString(location + ".world"));
        return new Location(world, x, y, z, yaw, pitch);
    }

    public static void setMapSpawn(int map, int spawn, Location loc) {
        String path = map + "." + spawn;

        FileConfiguration cfg = getConfiguration("maps");

        cfg.set(path, serialize(loc));

        saveConfiguration("maps", cfg);
    }

    public static Location getMapSpawn(int map, int spawn) {
        FileConfiguration cfg = getConfiguration("maps");

        String location = map + "." + spawn;

        double x = cfg.getDouble(location + ".x");
        double y = cfg.getDouble(location + ".y");
        double z = cfg.getDouble(location + ".z");
        float yaw = NumberConversions.toFloat(cfg.get(location + ".yaw"));
        float pitch = NumberConversions.toFloat(cfg.get(location + ".pitch"));
        World world = Bukkit.getWorld(cfg.getString(location + ".world"));

        if (world == null)
            return null;

        return new Location(world, x, y, z, yaw, pitch);
    }

    public static void setSpawn(String spawn, Location loc) {
        FileConfiguration cfg = getConfiguration("spawns");
        cfg.set(spawn, serialize(loc));
        saveConfiguration("spawns", cfg);
    }

    public static Map<String, Object> serialize(Location loc) {
        Map<String, Object> data = Maps.newHashMap();
        data.put("world", loc.getWorld().getName());

        data.put("x", loc.getX());
        data.put("y", loc.getY());
        data.put("z", loc.getZ());

        data.put("yaw", loc.getYaw());
        data.put("pitch", loc.getPitch());

        return data;
    }

}
