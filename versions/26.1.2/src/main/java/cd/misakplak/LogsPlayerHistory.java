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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class LogsPlayerHistory implements Listener {



    private PlayerData data = managePlayers.getInstance().getPlayerData();




    public Inventory getInventory(UUID target, int page) throws IOException, InvalidConfigurationException {
        data.reload();

        Inventory inventory = Bukkit.createInventory(null, 54, "Players history");

        ConfigurationSection logs = data.getSection(target + ".logs");

        if (logs == null) {
            return inventory;
        }

        List<String> logIds = new ArrayList<>();

        for (String logId : logs.getKeys(false)) {
            String base = target + ".logs." + logId;

            if (data.getGameMode(base + ".gamemode") == null) {
                continue;
            }

            logIds.add(logId);
        }

        logIds.sort((a, b) -> Long.compare(Long.parseLong(b), Long.parseLong(a)));

        int PAGE_SIZE = 45;
        int totalPages = (int) Math.ceil(logIds.size() / (double) PAGE_SIZE);
        if (totalPages == 0) totalPages = 1;

        if (page < 0) page = 0;
        if (page > totalPages - 1) page = totalPages - 1;

        int start = page * PAGE_SIZE;
        int end = Math.min(start + PAGE_SIZE, logIds.size());

        List<String> pageIds = start < end ? logIds.subList(start, end) : new ArrayList<>();

        NamespacedKey key = new NamespacedKey(managePlayers.getInstance(), "log-id");

        int slot = 0;
        for (String logId : pageIds) {
            String base = target + ".logs." + logId;

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

            inventory.setItem(slot, chest);

            slot++;
        }

        NamespacedKey navKey = new NamespacedKey(managePlayers.getInstance(), "history-nav");

        if (page > 0) {
            ItemStack prev = new MakeItem(Material.ARROW)
                    .setName("§ePrevious Page")
                    .build();

            ItemMeta prevMeta = prev.getItemMeta();
            prevMeta.getPersistentDataContainer().set(navKey, PersistentDataType.STRING, "prev");
            prev.setItemMeta(prevMeta);

            inventory.setItem(45, prev);
        }

        if (page < totalPages - 1) {
            ItemStack next = new MakeItem(Material.ARROW)
                    .setName("§eNext Page")
                    .build();

            ItemMeta nextMeta = next.getItemMeta();
            nextMeta.getPersistentDataContainer().set(navKey, PersistentDataType.STRING, "next");
            next.setItemMeta(nextMeta);

            inventory.setItem(53, next);
        }

        ItemStack pageInfo = new MakeItem(Material.PAPER)
                .setName("§7Page " + (page + 1) + " / " + totalPages)
                .build();

        inventory.setItem(49, pageInfo);

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

        String title = event.getView().getTitle();

        if (!title.equals("Players history")
                && !title.equals("Snapshot")
                && !title.equals("Chat")
                && !title.equals("Commands")) {
            return;
        }

        NamespacedKey navKey = new NamespacedKey(managePlayers.getInstance(), "history-nav");
        String navAction = clicked.getItemMeta().getPersistentDataContainer().get(navKey, PersistentDataType.STRING);

        if (navAction != null) {
            event.setCancelled(true);

            UUID viewer = event.getWhoClicked().getUniqueId();
            int currentPage = managePlayers.getInstance().getHistoryPage().getOrDefault(viewer, 0);

            int newPage = navAction.equals("next") ? currentPage + 1 : currentPage - 1;

            managePlayers.getInstance().getHistoryPage().put(viewer, newPage);

            event.getWhoClicked().openInventory(getInventory(targetUUID, newPage));
            return;
        }

        NamespacedKey key = new NamespacedKey(managePlayers.getInstance(), "log-id");

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
