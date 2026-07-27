package cd.misakplak;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
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



    private PlayerData data = managePlayers.getInstance().getPlayerData();




    public Inventory getInventory(UUID target) throws IOException, InvalidConfigurationException {
        data.reload();



        Inventory inventory = Bukkit.createInventory(null, 54, "Players history");

        ConfigurationSection logs = data.getSection(target + ".logs");


        if (logs == null) {
            return inventory;
        }

        for (String logId : logs.getKeys(false)) {
            String base = target + ".logs." + logId;



            if (data.getGameMode(base + ".gamemode") == null) {
                continue;
            }

            NamespacedKey key = new NamespacedKey(managePlayers.getInstance(), "log-id");

                ItemStack chest = new MakeItem(Material.BOOK)
                        .setName("§6Session Information")
                        .setLoreLegacy(List.of(
                                "§7Joined: " + data.getString(base + ".jointime"),
                                "§7Gamemode: " + data.getGameMode(base + ".gamemode"),
                                "§7Health: " + data.getConfig().getDouble(base + ".health"),
                                "§7Location: " + data.getString(base + ".location")
                        ))
                        .build();


            ItemMeta meta = chest.getItemMeta();

            meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, logId);
            chest.setItemMeta(meta);

            inventory.addItem(chest);


        }


        return inventory;
    }

    public Inventory getInventory(UUID target, String logId) throws IOException, InvalidConfigurationException {
        data.reload();

        ItemStack chat = new MakeItem(Material.PAPER)
                .setName("§eChat")
                .build();

        ItemStack commands = new MakeItem(Material.DARK_OAK_SIGN)
                .setName("§eCommands")
                .build();

        NamespacedKey key = new NamespacedKey(managePlayers.getInstance(), "log-id");

        ItemMeta chatMeta = chat.getItemMeta();
        chatMeta.getPersistentDataContainer().set(key, PersistentDataType.STRING, logId);
        chat.setItemMeta(chatMeta);

        ItemMeta commandMeta = commands.getItemMeta();
        commandMeta.getPersistentDataContainer().set(key, PersistentDataType.STRING, logId);
        commands.setItemMeta(commandMeta);

        Inventory inv = Bukkit.createInventory(null, 54, "Snapshot");

        String base = target + ".logs." + logId;

        inv.setContents(data.getInventory(base + ".inventory"));

        inv.setItem(36, data.getItem(base + ".helmet"));
        inv.setItem(37, data.getItem(base + ".chestplate"));
        inv.setItem(38, data.getItem(base + ".leggings"));
        inv.setItem(39, data.getItem(base + ".boots"));
        inv.setItem(40, data.getItem(base + ".offhand"));

        inv.setItem(48, chat);
        inv.setItem(49, commands);

        return inv;
    }

    public Inventory getChatGui(UUID target, String logId) throws IOException, InvalidConfigurationException {
        data.reload();
        Inventory inventory = Bukkit.createInventory(null, 54, "Chat");


        List<String> messages =
                data.getStringList(target + ".logs." + logId + ".chat");


        for (String message : messages) {

            ItemStack item = new MakeItem(Material.PAPER)
                    .setName("§fMessage")
                    .setLoreLegacy(List.of(message))
                    .build();

            inventory.addItem(item);
        }


        return inventory;
    }

    public Inventory getComandGui(UUID target,  String logId) throws IOException, InvalidConfigurationException {
        Inventory inventory = Bukkit.createInventory(null, 54, "Commands");
        data.reload();


        List<String> commands = data.getStringList(target + ".logs." + logId +".commands");


        for (String message : commands) {

            ItemStack item = new MakeItem(Material.DARK_OAK_SIGN)
                    .setName("§fcommand")
                    .setLoreLegacy(List.of(message))
                    .build();

            inventory.addItem(item);
        }


        return inventory;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) throws IOException, InvalidConfigurationException {

        UUID targetUUID =  managePlayers.getInstance().getTargetPlayer().get(event.getWhoClicked().getUniqueId());

        ItemStack clicked = event.getCurrentItem();

        if(!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        if (clicked == null || clicked.getType() == Material.AIR || !clicked.hasItemMeta()) {
            return;
        }
        NamespacedKey key = new NamespacedKey(managePlayers.getInstance(), "log-id");



        String title = event.getView().getTitle();

        if (!title.equals("Players history")
                && !title.equals("Snapshot")
                && !title.equals("Chat")
                && !title.equals("Commands")) {
            return;
        }

        String logId = clicked.getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING);

        if (logId == null) {
            return;
        }

        event.setCancelled(true);

        switch (clicked.getType()) {
            case BOOK -> {
                event.setCancelled(true);
                event.getWhoClicked().openInventory(getInventory(targetUUID, logId));
            }
            case DARK_OAK_SIGN -> {
                event.setCancelled(true);
                    event.getWhoClicked().openInventory(getComandGui(targetUUID, logId));
            }
            case PAPER -> {
                event.setCancelled(true);
                event.getWhoClicked().openInventory(getChatGui(targetUUID, logId));
            }
        }


    }

}
