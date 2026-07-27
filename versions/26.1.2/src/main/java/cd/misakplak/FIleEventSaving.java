package cd.misakplak;

import io.papermc.paper.event.player.AsyncChatEvent;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

public class FIleEventSaving implements Listener {
    private final PlayerData data;

    public FIleEventSaving(JavaPlugin plugin) throws IOException {
        this.data = new PlayerData(managePlayers.getInstance());
    }



    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) throws IOException {


        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        data.set(event.getPlayer().getUniqueId() + ".jointime", timestamp.toString());

    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) throws IOException {

        String id = String.valueOf(System.currentTimeMillis());
        UUID uuid = event.getPlayer().getUniqueId();
        Player player = event.getPlayer();
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());



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

    }

    @EventHandler
    public void onPlayerComand(PlayerCommandPreprocessEvent event) throws IOException, InvalidConfigurationException {

        data.reload();

        String command = event.getMessage();

        List<String> logs = data.getStringList("logs.");
        logs.add("[" + LocalTime.now().withNano(0) + "] COMMAND: " + command);


        data.set("logs", logs);
        data.save();
    }

    @EventHandler
    public void onPLayerSendMessage(AsyncPlayerChatEvent event) throws IOException, InvalidConfigurationException {

        data.reload();

        UUID uuid = event.getPlayer().getUniqueId();

        List<String> logs = data.getStringList(uuid + "logs.");
        logs.add("[" + LocalTime.now().withNano(0) + "] " + Bukkit.getPlayer(uuid)+ " CHAT: " + event.getMessage());


        data.set("logs", logs);
        data.save();

    }

}
