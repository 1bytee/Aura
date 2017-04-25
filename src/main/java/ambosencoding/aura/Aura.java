package ambosencoding.aura;

import ambosencoding.aura.commands.*;
import ambosencoding.aura.listener.BlockListener;
import ambosencoding.aura.listener.PlayerListener;
import ambosencoding.aura.listener.SpectatorListener;
import ambosencoding.aura.listener.TeamSelector;
import ambosencoding.aura.tasks.AbstractTask;
import ambosencoding.aura.tasks.LobbyTask;
import ambosencoding.aura.utils.GameState;
import ambosencoding.aura.utils.MapManager;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Aura extends JavaPlugin {

    private static @Getter Aura instance;
    public static final String PREFIX = "§8▍ §eAura §8┃ §7";
    public static List<Player> INGAME = Lists.newArrayList(), SPECTATING = Lists.newArrayList();
    public static GameState STATE = GameState.LOBBY;
    public static AbstractTask CURRENT_TASK;
    public static int MAP_ID;
    public static String MAP;
    public static int MIN_PLAYERS;

    @Getter
    private boolean isSetup;
    private final CommandRegistrar registrar = new CommandRegistrar(this);

    @Override
    public void onEnable() {
        instance = this;

        loadConfig();
        registerCommands();
        registerEvents();

        MapManager.init();
        Team.createTeams();

        if (!isSetup) {
            selectRandomMap();
            CURRENT_TASK = new LobbyTask();
        }
    }

    private void selectRandomMap() {
        MAP_ID = ThreadLocalRandom.current().nextInt(MapManager.getMapsById().size()) + 1;
        MAP = MapManager.getMapsById().get(MAP_ID);
        MapManager.loadWorld(MAP_ID);
        Bukkit.getWorlds().forEach(world -> world.getEntities().stream().filter(e -> e.getType() != EntityType.PLAYER).forEach(Entity::remove));
    }

    private void loadConfig() {
        getConfig().addDefault("setupMode", true);
        getConfig().addDefault("min-Spieler", 2);
        getConfig().addDefaults(ImmutableMap.of(
                "MySQL.Host", "host",
                "MySQL.Port", 3306,
                "MySQL.Datenbank", "datenbank",
                "MySQL.Benutzername", "benutzername",
                "MySQL.Passwort", "passwort"
        ));
        getConfig().options().copyDefaults(true);
        saveConfig();
        reloadConfig();

        isSetup = getConfig().getBoolean("setupMode");
        MIN_PLAYERS = getConfig().getInt("min-Spieler");
    }

    private void registerCommands() {
        registrar.registerCommand(new WorldCommand());
        registrar.registerCommand(new SetLobbySpawn());
        registrar.registerCommand(new SetMapSpawn());
        registrar.registerCommand(new StatsCommand());
    }

    private void registerEvents() {
        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new PlayerListener(), this);
        pm.registerEvents(new BlockListener(), this);
        pm.registerEvents(new SpectatorListener(), this);
        pm.registerEvents(new SpectatorListener.SpectatorCompass(), this);
        pm.registerEvents(new TeamSelector(), this);
    }

    public static void setToSpectator(Player p, boolean withTp) {
        Bukkit.getOnlinePlayers().stream().filter(o -> o != p).forEach(o -> o.hidePlayer(p));

        if (INGAME.contains(p)) {
            INGAME.remove(p);
        }

        if (INGAME.size() <= 1) {
            return;
        }

        if (withTp) {
            p.teleport(p.getLocation().add(0, 20, 0));
        }

        SPECTATING.add(p);

        ItemStack compass = new ItemStack(Material.COMPASS);
        ItemMeta meta = compass.getItemMeta();
        meta.setDisplayName("§eTeleporter");
        meta.setLore(Arrays.asList("", "§7Rechtsklick um das Menü zu öffnen."));
        compass.setItemMeta(meta);

        p.getInventory().clear();
        p.getInventory().setArmorContents(new ItemStack[4]);

        p.getInventory().addItem(compass);
        p.getInventory().setHeldItemSlot(0);
        p.setAllowFlight(true);
        p.setFlying(true);

        p.sendMessage(PREFIX + "Du bist ein Spectator.");
        p.sendMessage(PREFIX + "Du kannst den Kompass zu benutzen um anderen Spielern zuzuschauen!");
    }


}
