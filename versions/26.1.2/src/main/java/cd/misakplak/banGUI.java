package cd.misakplak;


import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.awt.*;
import java.time.Duration;
import java.util.*;

public class banGUI implements Listener {

    private final Map<UUID, String> reasons = new HashMap<>();
    private final Map<UUID, String> durations = new HashMap<>();
    private final Map<UUID, UUID> playerClickedReason = new HashMap<>();
    private final Map<UUID, UUID> playerClickedDuration = new HashMap<>();


    public Inventory getInventory(Player player) {
        Inventory gui = Bukkit.createInventory(player, 27, "§cBAN");

        String reasonStr = reasons.getOrDefault(player.getUniqueId(), "§7Not set");
        ItemStack reason = new MakeItem(Material.OAK_SIGN)
                .setName((MiniMessage.miniMessage().deserialize(
                        "<i><b><gradient:#AD3434:#D73D4C><i><b>ʙᴀɴ ʀᴇᴀ</b></i></gradient><gradient:#D73D4C:#DD1818><i><b>ѕᴏɴ:</b></i></gradient></b></i>"
                )))
                .setLore(Collections.singletonList(Component.text(reasonStr)))
                .build();

        String durationStr = durations.getOrDefault(player.getUniqueId(), "§7Not set");
        ItemStack duration = new MakeItem(Material.CLOCK)
                .setName((MiniMessage.miniMessage().deserialize(
                        "<i><b><gradient:#AD3434:#D73D4C><i><b>ᴅᴜʀᴀᴛɪ</b></i></gradient><gradient:#D73D4C:#DD1818><i><b>ᴏɴ:</b></i></gradient></b></i>"
                )))
                .setLore(Collections.singletonList(Component.text(durationStr)))
                .build();

        ItemStack confirm = new MakeItem(Material.YELLOW_CONCRETE)
                .setName((MiniMessage.miniMessage().deserialize(
                        "<i><b><gradient:#FCD05C:#FFFFFF><i><b>ᴄᴏɴꜰɪʀᴍ</b></i></gradient></b></i>"
                )))
                .build();

        gui.setItem(13, reason);
        gui.setItem(11, duration);
        gui.setItem(15, confirm);
        return gui;
    }


    @EventHandler
    public void onClick(InventoryClickEvent event) {

        if (!event.getView().getTitle().equals("§cBAN")) {
            return;
        }


        ItemStack clicked = event.getCurrentItem();
        Player player = (Player) event.getWhoClicked();
        UUID targetUUID = managePlayers.getInstance().getTargetPlayer().get(player.getUniqueId());

        Player target = Bukkit.getPlayer(targetUUID);

        if (clicked == null || clicked.getType() == Material.AIR) {
            return;
        }
        event.setCancelled(true);

        if (clicked.getType() == Material.OAK_SIGN) {
            playerClickedReason.put(player.getUniqueId(), player.getUniqueId());
            player.sendMessage("§8Type a reason, Valid reasons:  ");
            player.sendMessage("§8spam  ");
            player.sendMessage("§8esp  ");
            player.sendMessage("§8hacking: ");
            player.sendMessage("§aType cancel to cancel");
            player.closeInventory();
        }

        if (clicked.getType() == Material.CLOCK) {
            playerClickedDuration.put(player.getUniqueId(), player.getUniqueId());
            player.sendMessage("§8Type a reason, Examples:  ");
            player.sendMessage("§a1d/2h/5m  ");
            player.sendMessage("§a10d/9h/0m  ");
            player.sendMessage("§aperm       ");
            player.sendMessage("§aType cancel to cancel");
            player.closeInventory();
        }

        if (clicked.getType() == Material.YELLOW_CONCRETE) {
            String reasonType = reasons.get(player.getUniqueId());
            if (reasonType == null) {
                player.sendMessage("§cEnter reason first");
                return;
            }

            String durationStr = durations.get(player.getUniqueId());
            if (durationStr == null) {
                player.sendMessage("§cEnter a duration");
                return;
            }

            Duration duration;
            try {
                duration = parseDuration(durationStr);
            } catch (Exception e) {
                player.sendMessage("§cInvalid duration format!");
                return;
            }

            if (reasonType.equals("SPAM")) {  // Spam
                target.ban(
                        "§cYou have been banned from the server.\n\n" +
                                "§7Reason: §fSpam\n" +
                                "§7If you believe this was a mistake, contact the server staff.",
                        duration,
                        null
                );
            } else if (reasonType.equals("HACKING")) { // hacks
                target.ban(
                        "§cYou have been banned from the server.\n\n" +
                                "§7Reason: §fUsing Hacks/Cheats\n" +
                                "§7Cheating is not allowed on this server.",
                        duration,
                        null
                );
            } else if (reasonType.equals("ESP")) { // ESP
                target.ban(
                        "§cYou have been banned from the server.\n\n" +
                                "§7Reason: §fUsing ESP/X-Ray\n" +
                                "§7Cheating is not allowed on this server.",
                        duration,
                        null
                );
            } else if (reasonType.equals("PERM")) { // Perm (Example if you added more reasons)
                target.ban(
                        "§cYou have been banned from the server.\n\n" +
                                "§7Reason: §fPermanent Ban\n" +
                                "§7Contact staff for more info.",
                        duration,
                        null
                );
            }

            reasons.remove(player.getUniqueId());
            durations.remove(player.getUniqueId());
            player.sendMessage("§aPlayer banned successfully!");
            player.closeInventory();
        }
    }


    public Duration parseDuration(String input) {
        if (input == null) return null;
        input = input.toLowerCase();

        if (input.equals("perm")) {
            return null;
        }

        Duration duration = Duration.ZERO;

        for (String part : input.split("/")) {
            if (part.isEmpty()) continue;
            int amount = Integer.parseInt(part.substring(0, part.length() - 1));
            char unit = part.charAt(part.length() - 1);

            switch (unit) {
                case 'd' -> duration = duration.plusDays(amount);
                case 'h' -> duration = duration.plusHours(amount);
                case 'm' -> duration = duration.plusMinutes(amount);
                default -> throw new IllegalArgumentException("Invalid unit: " + unit);
            }
        }

        return duration;
    }

    @EventHandler
    public void onChat(AsyncChatEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        if (playerClickedReason.containsKey(uuid)) {
            String message = PlainTextComponentSerializer.plainText().serialize(event.message()).trim();
            event.setCancelled(true);

            switch (message.toLowerCase()) {
                case "cancel":
                    player.sendMessage("§aCanceling");
                    playerClickedReason.remove(uuid);
                    Bukkit.getScheduler().runTask(managePlayers.getInstance(), () -> {
                        player.openInventory(getInventory(player));
                    });
                    return;
                case "spam":
                    reasons.put(uuid, "SPAM");
                    playerClickedReason.remove(uuid);
                    Bukkit.getScheduler().runTask(managePlayers.getInstance(), () -> {
                        player.openInventory(getInventory(player));
                    });
                    return;
                case "hacking":
                    reasons.put(uuid, "HACKING");
                    playerClickedReason.remove(uuid);
                    Bukkit.getScheduler().runTask(managePlayers.getInstance(), () -> {
                        player.openInventory(getInventory(player));
                    });
                    return;
                case "esp":
                    reasons.put(uuid, "ESP");
                    playerClickedReason.remove(uuid);
                    Bukkit.getScheduler().runTask(managePlayers.getInstance(), () -> {
                        player.openInventory(getInventory(player));
                    });
                    return;
                default:
                    player.sendMessage("§cInvalid Reason: §c§l" + message);
                    return;
            }
        }

        if (playerClickedDuration.containsKey(uuid)) {
            String message = PlainTextComponentSerializer.plainText().serialize(event.message()).trim();
            event.setCancelled(true);

            if (message.equalsIgnoreCase("cancel")) {
                player.sendMessage("§aCanceling");
                playerClickedDuration.remove(uuid);
                Bukkit.getScheduler().runTask(managePlayers.getInstance(), () -> {
                    player.openInventory(getInventory(player));
                });
                return;
            }

            try {
                parseDuration(message);

                durations.put(uuid, message);
                playerClickedDuration.remove(uuid);
                Bukkit.getScheduler().runTask(managePlayers.getInstance(), () -> {
                    player.openInventory(getInventory(player));
                });
            } catch (Exception e) {
                player.sendMessage("§cInvalid duration format! Example: §81d/2h/9m §7or §8perm");
            }
        }
    }
}
