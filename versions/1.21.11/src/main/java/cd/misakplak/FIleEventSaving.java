package cd.misakplak;

import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalTime;
import java.util.*;

public class FIleEventSaving implements Listener {
    private final PlayerData data = managePlayers.getInstance().getPlayerData();

    private final Map<UUID, PendingSession> pendingSessions = new HashMap<>();
    private final Map<UUID, String> currentSessions = managePlayers.getInstance().getCurrentSessions();

    private final boolean logStuff = managePlayers.getInstance().getConfig().getBoolean("history.log-history");


    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) throws IOException {

        if (!logStuff) {
            return;
        }

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        String sessionId = String.valueOf(System.currentTimeMillis());

        BukkitTask task = startSessionTimer(event.getPlayer(), sessionId);
        pendingSessions.put(event.getPlayer().getUniqueId(), new PendingSession(sessionId, task));


        data.set(event.getPlayer().getUniqueId(), "logs." + sessionId + ".jointime", timestamp.toString());
        data.save(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) throws IOException, InvalidConfigurationException {
        if (!logStuff) {
            return;
        }
        UUID uuid = event.getPlayer().getUniqueId();
        Player player = event.getPlayer();
        PendingSession pending = pendingSessions.remove(uuid);

        if (pending != null) {
            pending.getTask().cancel();
            return;
        }

        saveSnapshot(uuid, player);
        data.unload(uuid);


    }

    @EventHandler
    public void onPlayerComand(PlayerCommandPreprocessEvent event) throws IOException {

        if (!logStuff) {
            return;
        }

        UUID uuid = event.getPlayer().getUniqueId();
        PendingSession pending = pendingSessions.remove(uuid);

        if (pending != null) {
            pending.getTask().cancel();
            currentSessions.put(uuid, pending.getSessionId());
        }

        String sessionId = currentSessions.get(uuid);


        if (sessionId == null) {
            return;
        }

        String path = "logs." + sessionId + ".commands";

        List<String> logs = data.getStringList(uuid, path);
        logs.add("[" + LocalTime.now().withNano(0) + "] " + event.getMessage());

        data.set(uuid, path, logs);
        data.save(uuid);
    }

    @EventHandler
    public void onPLayerSendMessage(AsyncPlayerChatEvent event) throws IOException {

        if (!logStuff) {
            return;
        }


        UUID uuid = event.getPlayer().getUniqueId();
        PendingSession pending = pendingSessions.remove(uuid);



        if (pending != null) {
            pending.getTask().cancel();
            currentSessions.put(uuid, pending.getSessionId());
        }

        String sessionId = currentSessions.get(uuid);


        if (sessionId == null) {
            return;
        }

        String path = "logs." + sessionId + ".chat";

        List<String> logs = data.getStringList(uuid, path);
        logs.add("[" + LocalTime.now().withNano(0) + "] " + event.getMessage());


        Bukkit.getScheduler().runTask(managePlayers.getInstance(), () -> {
            try {
                data.set(uuid, path, logs);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            try {
                data.save(uuid);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });


    }

    public void saveSnapshot(UUID uuid, Player player) throws IOException, InvalidConfigurationException {

        String id = currentSessions.get(uuid);
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());


        if (id == null) {
            return;
        }


        data.set(uuid, "logs." + id + ".inventory", player.getInventory().getContents());
        data.set(uuid, "logs." + id + ".helmet", player.getInventory().getHelmet());
        data.set(uuid, "logs." + id + ".chestplate", player.getInventory().getChestplate());
        data.set(uuid, "logs." + id + ".leggings", player.getInventory().getLeggings());
        data.set(uuid, "logs." + id + ".boots", player.getInventory().getBoots());
        data.set(uuid, "logs." + id + ".offhand", player.getInventory().getItemInOffHand());

        data.set(uuid, "logs." + id + ".gamemode", player.getGameMode().name());
        data.set(uuid, "logs." + id + ".health", player.getHealth());
        data.set(uuid, "logs." + id + ".location", "X: " + player.getLocation().getBlockX() + ", Y: " + player.getLocation().getBlockY() + ", Z: " + player.getLocation().getBlockZ());
        data.set(uuid, "logs." + id + ".leavetime", timestamp.toString());
        currentSessions.remove(uuid);
        data.save(uuid);
    }

    public BukkitTask startSessionTimer(Player player, String sessionId) {

        int secconds = managePlayers.getInstance().getConfig().getInt("history.secconds-untill-session-start");

        return new BukkitRunnable() {

            @Override
            public void run() {

                if (player == null || !player.isOnline()) {
                    return;
                }

                currentSessions.put(player.getUniqueId(), sessionId);
                pendingSessions.remove(player.getUniqueId());
                cancel();


            }

        }.runTaskLater(managePlayers.getInstance(), 20L * secconds);
    }
}

