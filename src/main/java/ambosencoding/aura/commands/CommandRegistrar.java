package ambosencoding.aura.commands;

import com.google.common.base.Preconditions;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Arrays;

@RequiredArgsConstructor
public class CommandRegistrar {

    private final Plugin plugin;

    public void registerCommand(CommandExecutor executor) {
        Preconditions.checkNotNull(executor);
        CommandHandler command = executor.getClass().getAnnotation(CommandHandler.class);
        Preconditions.checkNotNull(command, "Couldn't register " + executor.getClass().getSimpleName() + "! @CommandHandler not found.");

        CommandMap map = getCommandMap();
        PluginCommand cmd = newCommand(command.name());
        cmd.setDescription(command.description());
        cmd.setExecutor(executor);
        cmd.setTabCompleter(newInstance(command.tabCompleter()));

        if (!command.permission().equals("")) {
            cmd.setPermission(command.permission());
            if (!command.permissionMessage().equals("")) {
                cmd.setPermissionMessage(command.permissionMessage());
            }
        }

        if (!command.usage().equals("")) {
            cmd.setUsage(command.usage());
        }

        if (command.aliases().length != 0) {
            cmd.setAliases(Arrays.asList(command.aliases()));
        }

        map.register(getClass().getSimpleName().toLowerCase(), cmd);
        System.out.printf("[MongoPerms] Registered command %s", command.name());
    }

    @SneakyThrows
    @SuppressWarnings("unchecked")
    private <T> T newInstance(Class<? extends T> clazz, Object... args) {
        Constructor<T> constructor = (Constructor<T>) clazz.getConstructors()[0];

        Class<?>[] parameters = constructor.getParameterTypes();

        if (parameters.length == 0) {
            return constructor.newInstance();
        } else {
            if (Plugin.class.isAssignableFrom(parameters[0]) && parameters.length == 1) {
                return constructor.newInstance(this);
            }
        }

        throw new IllegalStateException();
    }

    @SneakyThrows
    private PluginCommand newCommand(String name) {
        Constructor<? extends Command> constructor = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
        constructor.setAccessible(true);
        return (PluginCommand) constructor.newInstance(name, plugin);
    }

    @SneakyThrows
    private CommandMap getCommandMap() {
        Field f = Bukkit.getServer().getClass().getDeclaredField("commandMap");
        f.setAccessible(true);
        return (CommandMap) f.get(Bukkit.getServer());
    }

}
