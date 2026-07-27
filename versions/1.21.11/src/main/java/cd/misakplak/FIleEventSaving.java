package cd.misakplak;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.sql.Timestamp;

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



        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        data.set(event.getPlayer().getUniqueId() + ".leavetime", timestamp.toString());

        data.set(event.getPlayer().getUniqueId() + timestamp.toString() +".inventory", event.getPlayer().getInventory().getContents());

        data.set(event.getPlayer().getUniqueId() + ".offhand", event.getPlayer().getInventory().getItemInOffHand());
        data.set(event.getPlayer().getUniqueId() + ".helmet", event.getPlayer().getInventory().getHelmet());
        data.set(event.getPlayer().getUniqueId() + ".chestplate", event.getPlayer().getInventory().getChestplate());
        data.set(event.getPlayer().getUniqueId() + ".leggings", event.getPlayer().getInventory().getLeggings());
        data.set(event.getPlayer().getUniqueId() + ".boots", event.getPlayer().getInventory().getBoots());

        data.set(event.getPlayer().getUniqueId() + ".gamemode", event.getPlayer().getGameMode().name());



    }

}
