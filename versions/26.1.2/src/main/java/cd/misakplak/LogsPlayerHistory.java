package cd.misakplak;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public class LogsPlayerHistory implements Listener {



    private PlayerData data;

    public LogsPlayerHistory() throws IOException {
        this.data = new PlayerData(managePlayers.getInstance());
    }




    public Inventory getInventory(UUID target) throws IOException {



        Inventory inventory = Bukkit.createInventory(null, 54, "Players history");

        ConfigurationSection logs = data.getSection(target + ".logs");


        if (logs == null) {
            return inventory;
        }

        for (String logId : logs.getKeys(false)) {
            String base = target + ".logs." + logId;

            String leaveTime = data.getString(base + ".leavetime");

            NamespacedKey key = new NamespacedKey(managePlayers.getInstance(), "log-id");

            ItemStack chest = new MakeItem(Material.CHEST)
                    .setName("§6" + leaveTime)
                    .setLoreLegacy(List.of(
                            "§7Gamemode: " + data.getGameMode(base + ".gamemode"),
                            "§7Health: " + data.getConfig().getDouble(base + ".health"),
                            "§7Location " + data.getConfig().get(base + ".location")
                    ))
                    .build();

            ItemMeta meta = chest.getItemMeta();

            meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, logId);
            chest.setItemMeta(meta);

            inventory.addItem(chest);


        }





        return inventory;
    }

    public Inventory getInventory(UUID target, String logId) throws IOException {

        Inventory inv = Bukkit.createInventory(null, 54, "Snapshot");

        String base = target + ".logs." + logId;

        inv.setContents(data.getInventory(base + ".inventory"));

        inv.setItem(36, data.getItem(base + ".helmet"));
        inv.setItem(37, data.getItem(base + ".chestplate"));
        inv.setItem(38, data.getItem(base + ".leggings"));
        inv.setItem(39, data.getItem(base + ".boots"));
        inv.setItem(40, data.getItem(base + ".offhand"));

        return inv;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) throws IOException {
        UUID targetUUID =  managePlayers.getInstance().getTargetPlayer().get(event.getWhoClicked().getUniqueId());

        ItemStack clicked = event.getCurrentItem();

        if(!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        if (clicked == null || clicked.getType() == Material.AIR || !clicked.hasItemMeta()) {
            return;
        }
        NamespacedKey key = new NamespacedKey(managePlayers.getInstance(), "log-id");

        String logId = clicked.getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING);

        if (logId == null) {
            return;
        }


        if (!event.getView().getTitle().equals("Players history")
                && !event.getView().getTitle().equals("Snapshot")) {
            return;
        }
        event.setCancelled(true);

        if (clicked.getType() == Material.CHEST) {
            event.getWhoClicked().openInventory(getInventory(targetUUID, logId));
        }

    }

}
