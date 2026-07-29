package cd.misakplak;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.io.IOException;
import java.util.*;

public class LogsPlayerHistory implements Listener {



    private PlayerData data = managePlayers.getInstance().getPlayerData();





    public Inventory getInventory(UUID target, int page) throws IOException, InvalidConfigurationException {
        data.reload(target);

        Inventory inventory = Bukkit.createInventory(null, 54, "Players history");

        ItemStack noLog = new MakeItem(Material.BARRIER)
                .setName("§c§lLogging disabled in §aconfig.yml§c!")
                .build();

        if (!managePlayers.getInstance().getConfig().getBoolean("history.log-history")) {
            inventory.setItem(0, noLog);
            return inventory;
        }

        ConfigurationSection logs = data.getSection(target,"logs");

        if (logs == null) {
            return inventory;
        }

        List<String> logIds = new ArrayList<>();

        for (String logId : logs.getKeys(false)) {
            String base = "logs."  + logId;

            if (data.getGameMode(target, base + ".gamemode") == null) {
                continue;
            }

            logIds.add(logId);
        }

        logIds.sort((a, b) -> Long.compare(Long.parseLong(b), Long.parseLong(a)));

        int PAGE_SIZE = 45;
        int totalPages = (int) Math.ceil(logIds.size() / (double) PAGE_SIZE);
        if (totalPages == 0) totalPages = 1;

        if (page < 0) page = 0;
        if (page > totalPages - 1) page = totalPages  - 1;

        int start = page * PAGE_SIZE;
        int end = Math.min(start + PAGE_SIZE, logIds.size());

        List<String> pageIds = start < end ? logIds.subList(start, end) : new ArrayList<>();

        NamespacedKey key = new NamespacedKey(managePlayers.getInstance(), "log-id");

        int slot = 0;
        for (String logId : pageIds) {
            String base = "logs." + logId;

            ItemStack chest = new MakeItem(Material.BOOK)
                    .setName("§6Session Information")
                    .setLoreLegacy(List.of(
                            "§7Joined: " + data.getString(target, base + ".jointime"),
                            "§7Gamemode: " + data.getGameMode(target,base + ".gamemode"),
                            "§7Health: " + data.getConfig(target).getDouble(base + ".health"),
                            "§7Location: " + data.getString(target, base + ".location")
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
        data.reload(target);

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

        String base =  "logs." + logId;

        inv.setContents(data.getInventory(target, base + ".inventory"));


        inv.setItem(36, data.getItem(target, base + ".helmet"));
        inv.setItem(37, data.getItem(target, base + ".chestplate"));
        inv.setItem(38, data.getItem(target, base + ".leggings"));
        inv.setItem(39, data.getItem(target, base + ".boots"));
        inv.setItem(40, data.getItem(target, base + ".offhand"));
        inv.setItem(48, chat);
        inv.setItem(49, commands);

        return inv;
    }

    public Inventory getChatGui(UUID target, String logId, int page) throws IOException, InvalidConfigurationException {
        data.reload(target);
        Inventory inventory = Bukkit.createInventory(null, 54, "Chat");


        List<String> messages =
                data.getStringList(target, "logs." + logId + ".chat");

        ConfigurationSection logs = data.getSection(target, "logs");

        if (logs == null) {
            return inventory;
        }

        int PAGE_SIZE = 45;
        int totalPages = (int) Math.ceil(messages.size() / (double) PAGE_SIZE);
        if (totalPages == 0) totalPages = 1;

        if (page < 0) page = 0;
        if (page > totalPages - 1) page = totalPages - 1;

        int start = page * PAGE_SIZE;
        int end = Math.min(start + PAGE_SIZE, messages.size());

        List<String> pageMessages = start < end ? messages.subList(start, end) : new ArrayList<>();



        int slot = 0;
        for (String message : pageMessages) {

            ItemStack item = new MakeItem(Material.PAPER)
                    .setName("§fMessage")
                    .setLoreLegacy(List.of(message))
                    .build();

            inventory.setItem(slot, item);
            slot++;
        }

        NamespacedKey navKey = new NamespacedKey(managePlayers.getInstance(), "history-nav");
        NamespacedKey navTypeKey = new NamespacedKey(managePlayers.getInstance(), "history-nav-type");
        NamespacedKey navLogKey = new NamespacedKey(managePlayers.getInstance(), "history-nav-logid");

        if (page > 0) {
            ItemStack prev = new MakeItem(Material.ARROW)
                    .setName("§ePrevious Page")
                    .build();

            ItemMeta prevMeta = prev.getItemMeta();
            prevMeta.getPersistentDataContainer().set(navKey, PersistentDataType.STRING, "prev");
            prevMeta.getPersistentDataContainer().set(navTypeKey, PersistentDataType.STRING, "chat");
            prevMeta.getPersistentDataContainer().set(navLogKey, PersistentDataType.STRING, logId);
            prev.setItemMeta(prevMeta);

            inventory.setItem(45, prev);
        }

        if (page < totalPages - 1) {
            ItemStack next = new MakeItem(Material.ARROW)
                    .setName("§eNext Page")
                    .build();

            ItemMeta nextMeta = next.getItemMeta();
            nextMeta.getPersistentDataContainer().set(navKey, PersistentDataType.STRING, "next");
            nextMeta.getPersistentDataContainer().set(navTypeKey, PersistentDataType.STRING, "chat");
            nextMeta.getPersistentDataContainer().set(navLogKey, PersistentDataType.STRING, logId);
            next.setItemMeta(nextMeta);

            inventory.setItem(53, next);
        }

        ItemStack pageInfo = new MakeItem(Material.PAPER)
                .setName("§7Page " + (page + 1) + " / " + totalPages)
                .build();

        inventory.setItem(49, pageInfo);


        return inventory;
    }

    public Inventory getComandGui(UUID target,  String logId, int page) throws IOException, InvalidConfigurationException {
        Inventory inventory = Bukkit.createInventory(null, 54, "Commands");
        data.reload(target);


        List<String> commands = data.getStringList(target, "logs." + logId + ".commands");

        ConfigurationSection logs = data.getSection(target, "logs");

        if (logs == null) {
            return inventory;
        }

        int PAGE_SIZE = 45;
        int totalPages = (int) Math.ceil(commands.size() / (double) PAGE_SIZE);
        if (totalPages == 0) totalPages = 1;

        if (page < 0) page = 0;
        if (page > totalPages - 1) page = totalPages - 1;

        int start = page * PAGE_SIZE;
        int end = Math.min(start + PAGE_SIZE, commands.size());

        List<String> pageMessages = start < end ? commands.subList(start, end) : new ArrayList<>();



        int slot = 0;


        for (String message : pageMessages) {

            ItemStack item = new MakeItem(Material.DARK_OAK_SIGN)
                    .setName("§fcommand")
                    .setLoreLegacy(List.of(message))
                    .build();

            inventory.setItem(slot, item);
            slot++;
        }

        NamespacedKey navKey = new NamespacedKey(managePlayers.getInstance(), "history-nav");
        NamespacedKey navTypeKey = new NamespacedKey(managePlayers.getInstance(), "history-nav-type");
        NamespacedKey navLogKey = new NamespacedKey(managePlayers.getInstance(), "history-nav-logid");

        if (page > 0) {
            ItemStack prev = new MakeItem(Material.ARROW)
                    .setName("§ePrevious Page")
                    .build();

            ItemMeta prevMeta = prev.getItemMeta();
            prevMeta.getPersistentDataContainer().set(navKey, PersistentDataType.STRING, "prev");
            prevMeta.getPersistentDataContainer().set(navTypeKey, PersistentDataType.STRING, "commands");
            prevMeta.getPersistentDataContainer().set(navLogKey, PersistentDataType.STRING, logId);
            prev.setItemMeta(prevMeta);

            inventory.setItem(45, prev);
        }

        if (page < totalPages - 1) {
            ItemStack next = new MakeItem(Material.ARROW)
                    .setName("§eNext Page")
                    .build();

            ItemMeta nextMeta = next.getItemMeta();
            nextMeta.getPersistentDataContainer().set(navKey, PersistentDataType.STRING, "next");
            nextMeta.getPersistentDataContainer().set(navTypeKey, PersistentDataType.STRING, "commands");
            nextMeta.getPersistentDataContainer().set(navLogKey, PersistentDataType.STRING, logId);
            next.setItemMeta(nextMeta);

            inventory.setItem(53, next);
        }

        ItemStack pageInfo = new MakeItem(Material.PAPER)
                .setName("§7Page " + (page + 1) + " / " + totalPages)
                .build();

        inventory.setItem(49, pageInfo);


        return inventory;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) throws IOException, InvalidConfigurationException {

        UUID targetUUID =  managePlayers.getInstance().getTargetPlayer().get(event.getWhoClicked().getUniqueId());

        ItemStack clicked = event.getCurrentItem();

        if(!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        String title = event.getView().getTitle();

        if (!title.equals("Players history")
                && !title.equals("Snapshot")
                && !title.equals("Chat")
                && !title.equals("Commands")) {
            return;
        }
        event.setCancelled(true);

        if (clicked == null || clicked.getType() == Material.AIR || !clicked.hasItemMeta()) {
            return;
        }

        NamespacedKey navKey = new NamespacedKey(managePlayers.getInstance(), "history-nav");
        String navAction = clicked.getItemMeta().getPersistentDataContainer().get(navKey, PersistentDataType.STRING);

        if (navAction != null) {
            NamespacedKey navTypeKey = new NamespacedKey(managePlayers.getInstance(), "history-nav-type");
            NamespacedKey navLogIdKey = new NamespacedKey(managePlayers.getInstance(), "history-nav-logid");

            String navType = clicked.getItemMeta().getPersistentDataContainer().get(navTypeKey, PersistentDataType.STRING);
            String navLogId = clicked.getItemMeta().getPersistentDataContainer().get(navLogIdKey, PersistentDataType.STRING);

            UUID viewer = event.getWhoClicked().getUniqueId();

            if (navType == null) {
                int currentPage = managePlayers.getInstance().getHistoryPage().getOrDefault(viewer, 0);
                int newPage = navAction.equals("next") ? currentPage + 1 : currentPage - 1;

                managePlayers.getInstance().getHistoryPage().put(viewer, newPage);
                event.getWhoClicked().openInventory(getInventory(targetUUID, newPage));

            } else if (navType.equals("chat")) {
                int currentPage = managePlayers.getInstance().getChatPage().getOrDefault(viewer, 0);
                int newPage = navAction.equals("next") ? currentPage + 1 : currentPage - 1;

                managePlayers.getInstance().getChatPage().put(viewer, newPage);
                event.getWhoClicked().openInventory(getChatGui(targetUUID, navLogId, newPage));

            } else if (navType.equals("commands")) {
                int currentPage = managePlayers.getInstance().getCommandsPage().getOrDefault(viewer, 0);
                int newPage = navAction.equals("next") ? currentPage + 1 : currentPage - 1;

                managePlayers.getInstance().getCommandsPage().put(viewer, newPage);
                event.getWhoClicked().openInventory(getComandGui(targetUUID, navLogId, newPage));
            }

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
                event.getWhoClicked().openInventory(getComandGui(targetUUID, logId, 0));
            }
            case PAPER -> {
                event.setCancelled(true);
                event.getWhoClicked().openInventory(getChatGui(targetUUID, logId, 0));
            }
        }
    }

}