package ambosencoding.aura.utils;

import ambosencoding.aura.Aura;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.WorldCreator;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.List;

public class MapManager {

    @Getter
    private static final HashMap<Integer, String> mapsById = Maps.newHashMap();
    @Getter
    private static final HashMap<String, Integer> mapsByName = Maps.newHashMap();

    private static boolean loaded = false;

    @SneakyThrows
    public static void init() {
        File f = new File(Aura.getInstance().getDataFolder(), "worldnames.yml");
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(f);

        cfg.addDefault("Maps", new String[] {
                "Map1", "Map2", "Map3"
        });
        cfg.options().copyDefaults(true);
        cfg.save(f);

        cfg = YamlConfiguration.loadConfiguration(f);

        List<String> worlds = cfg.getStringList("Maps");
        for (int i = 0; i < worlds.size(); i++) {
            mapsById.put(i + 1, worlds.get(i));
            mapsByName.put(worlds.get(i), i + 1);
            if (Aura.getInstance().isSetup()) {
                Bukkit.createWorld(new WorldCreator("Map" + (i + 1)));
            }
        }
        loadWorlds();
    }

    public static void loadWorlds() {
        if (loaded) {
            throw new IllegalStateException("Worlds already loaded!");
        }

        Bukkit.createWorld(new WorldCreator("Lobby"));
        loaded = true;
    }

    public static void loadWorld(int id) {
        Preconditions.checkState(mapsById.containsKey(id));

        if (Bukkit.getWorld("Map" + id) != null) {
            return;
        }

        Bukkit.createWorld(new WorldCreator("Map" + id));
        System.out.println("[Aura] World " + mapsById.get(id) + " has been loaded.");
    }

}
