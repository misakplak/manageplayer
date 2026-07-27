package cd.misakplak;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public class LogsPlayerHistory implements Listener {



    private PlayerData data;

    public LogsPlayerHistory() throws IOException {
        this.data = new PlayerData(managePlayers.getInstance());
    }




    public Inventory getInventory( UUID target) throws IOException {

        ItemStack gamemode = new MakeItem(Material.IRON_DOOR)
                .setName(MiniMessage.miniMessage().deserialize(
                        "<i><b><gradient:#CBA981:#FD8B46><shadow:#4D743E:1><i><b>ɢ</b></i></shadow><shadow:#4F6B50:1><i><b>ᴀ</b></i></shadow><shadow:#526362:1><i><b>ᴍ</b></i></shadow><shadow:#545A74:1><i><b>ᴇ</b></i></shadow><shadow:#565286:1><i><b>ᴍ</b></i></shadow><shadow:#584998:1><i><b>ᴏ</b></i></shadow><shadow:#5B41AA:1><i><b>ᴅ</b></i></shadow><shadow:#5D38BC:1><i><b>ᴇ </b></i></shadow></gradient></b></i>"
                ))
                .setLoreLegacy(List.of(String.valueOf(data.getGameMode(target + ".gamemode"))))
                .build();

        Inventory inventory = Bukkit.createInventory(null, 54, "Players history");



        inventory.setContents(data.getInventory(target + ".inventory"));
        inventory.setItem(43, gamemode);



        return inventory;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        UUID targetUUID =  managePlayers.getInstance().getTargetPlayer().get(event.getWhoClicked().getUniqueId());

        if(!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        if(!event.getView().getTitle().equals("Players history")) {
            return;
        }
        event.setCancelled(true);
    }

}
