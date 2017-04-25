package ambosencoding.aura.utils;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import javafx.util.Pair;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.bukkit.entity.Player;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UUIDManager {

    private static final Map<String, UUID> UUIDbyName = Maps.newHashMap();
    private static final Map<UUID, String> nameByUUID = Maps.newHashMap();
    private static final Gson gson = new Gson();
    private static final ExecutorService service = Executors.newCachedThreadPool();

    @SneakyThrows
    public static UUID getUUID(String name) {
        if (UUIDbyName.containsKey(name)) {
            return UUIDbyName.get(name);
        } else {
            Callable<UUID> callable = () -> {
                HttpURLConnection connection = (HttpURLConnection) new URL("https://api.mojang.com/users/profiles/minecraft/" + name).openConnection();
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return null;
                }
                JsonObject object = gson.fromJson(IOUtils.toString(connection.getInputStream()), JsonObject.class);
                String foundName = object.get("name").getAsString();
                UUID uuid = UUID.fromString(object.get("id").getAsString().replaceAll("(?i)(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w+)", "$1-$2-$3-$4-$5"));
                UUIDbyName.put(foundName, uuid);
                nameByUUID.put(uuid, foundName);
                return uuid;
            };
            return service.submit(callable).get();
        }
    }

    public static Pair<String, UUID> getUUIDAndName(String name) {
        UUID uuid = getUUID(name);
        if (uuid == null) {
            return null;
        }
        return new Pair<>(nameByUUID.get(uuid), uuid);
    }

    public static UUID getUUID(Player p) {
        return getUUID(p.getName());
    }

}
