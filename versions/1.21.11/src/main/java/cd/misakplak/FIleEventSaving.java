package cd.misakplak;

import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class FIleEventSaving implements Listener {
    private final PlayerData data = managePlayers.getInstance().getPlayerData();

    private final Map<UUID, String> currentSessions = managePlayers.getInstance().getCurrentSessions();




    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) throws IOException{

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        String sessionId = String.valueOf(System.currentTimeMillis());
        currentSessions.put(event.getPlayer().getUniqueId(), sessionId);


        data.set(event.getPlayer().getUniqueId() + ".logs." + sessionId + ".jointime", timestamp.toString());
        data.save();
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) throws IOException, InvalidConfigurationException {
        UUID uuid = event.getPlayer().getUniqueId();
        Player player = event.getPlayer();

        saveSnapshot(uuid, player);


    }

    @EventHandler
    public void onPlayerComand(PlayerCommandPreprocessEvent event) throws IOException {


        UUID uuid = event.getPlayer().getUniqueId();
        String sessionId = currentSessions.get(uuid);
        String path = uuid + ".logs." + sessionId + ".commands";

        if (sessionId == null) {
            return;
        }

        List<String> logs = data.getStringList(path);
        logs.add("[" + LocalTime.now().withNano(0) + "] " + event.getMessage());

                data.set(path, logs);
                data.save();
    }

    @EventHandler
    public void onPLayerSendMessage(AsyncPlayerChatEvent event) throws IOException {


        UUID uuid = event.getPlayer().getUniqueId();
        String sessionId = currentSessions.get(uuid);
        String path = uuid + ".logs." + sessionId + ".chat";

        if (sessionId == null) {
            return;
        }

        List<String> logs = data.getStringList(path);
        logs.add("[" + LocalTime.now().withNano(0) + "] " + event.getMessage());


        Bukkit.getScheduler().runTask(managePlayers.getInstance(), () -> {
            try {
                data.set(path, logs);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            try {
                data.save();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });


    }

    public void saveSnapshot(UUID uuid, Player player) throws IOException, InvalidConfigurationException {

        String id = currentSessions.get(uuid);
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        Bukkit.getLogger().info("Saving " + uuid);
        Bukkit.getLogger().info("Session = " + id);
        Bukkit.getLogger().info("Current sessions = " + currentSessions);


        data.set(uuid + ".logs." + id + ".inventory", player.getInventory().getContents());
        data.set(uuid + ".logs." + id + ".helmet", player.getInventory().getHelmet());
        data.set(uuid + ".logs." + id + ".chestplate", player.getInventory().getChestplate());
        data.set(uuid + ".logs." + id + ".leggings", player.getInventory().getLeggings());
        data.set(uuid + ".logs." + id + ".boots", player.getInventory().getBoots());
        data.set(uuid + ".logs." + id + ".offhand", player.getInventory().getItemInOffHand());

        data.set(uuid + ".logs." + id + ".gamemode", player.getGameMode().name());
        data.set(uuid + ".logs." + id + ".health", player.getHealth());
        data.set(uuid + ".logs." + id + ".location", "X: "+player.getLocation().getBlockX()+", Y: "+player.getLocation().getBlockY()+", Z: "+player.getLocation().getBlockZ());
        data.set(uuid + ".logs." + id + ".leavetime", timestamp.toString());
        currentSessions.remove(uuid);
        data.save();
    }

}