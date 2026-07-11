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

import java.time.Duration;
import java.util.*;

public class banGUI implements Listener {

    private final Map<UUID, String> reasons = new HashMap<>();
    private final Map<UUID, String> durations = new HashMap<>();
    private final Map<UUID, UUID> playerClickedReason = new HashMap<>();
    private final Map<UUID, UUID> playerClickedDuration = new HashMap<>();


    public Inventory getInventory(Player player) {
        Inventory gui = Bukkit.createInventory(player, 27, "§cBAN");

        ItemStack reason = new MakeItem(Material.OAK_SIGN)
                .setName((MiniMessage.miniMessage().deserialize(
                        "<i><b><gradient:#AD3434:#D73D4C><i><b>ʙᴀɴ ʀᴇᴀ</b></i></gradient><gradient:#D73D4C:#DD1818><i><b>ѕᴏɴ:</b></i></gradient></b></i>"
                )))
                .setLore(Collections.singletonList(Component.text(String.valueOf(playerClickedReason.get(player.getUniqueId())))))
                .build();

        ItemStack duration = new MakeItem(Material.CLOCK)
                .setName((MiniMessage.miniMessage().deserialize(
                        "<i><b><gradient:#AD3434:#D73D4C><i><b>ᴅᴜʀᴀᴛɪ</b></i></gradient><gradient:#D73D4C:#DD1818><i><b>ᴏɴ:</b></i></gradient></b></i>"
                )))
                .setLore(Collections.singletonList(Component.text(String.valueOf(playerClickedDuration.get(player.getUniqueId())))))
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

            if (reasons.get(player.getUniqueId()) == null) {
                player.sendMessage("§cEnter reason first");
                return;
            }

            Duration duration = parseDuration(durations.get(player.getUniqueId()));

            if (duration == null) {
                player.sendMessage("§cEnter a duration");
                return;
            }

            if (reasons.get(player.getUniqueId()).equals("SPAM")) {  // Spam

                target.ban(
                        "§cYou have been banned from the server.\n\n" +
                                "§7Reason: §fSpam\n" +
                                "§7If you believe this was a mistake, contact the server staff.",
                        duration,
                        null
                );
                reasons.remove(player.getUniqueId(), "SPAM");
            } else if (reasons.get(player.getUniqueId()).equals("HACKING")) { // hacks

                target.ban(
                        "§cYou have been banned from the server.\n\n" +
                                "§7Reason: §fUsing Hacks/Cheats\n" +
                                "§7Cheating is not allowed on this server.",
                        duration,
                        null
                );
                reasons.remove(player.getUniqueId(), "HACKING");
            } else if (reasons.get(player.getUniqueId()).equals("ESP")) { // ESP
                target.ban(
                        "§cYou have been banned from the server.\n\n" +
                                "§7Reason: §fUsing ESP/X-Ray\n" +
                                "§7Cheating is not allowed on this server.",
                        duration,
                        null
                );
                reasons.remove(player.getUniqueId(), "ESP");
            }
        }
    }


    public Duration parseDuration(String input) {
        input = input.toLowerCase();

        Duration duration = Duration.ZERO;

        if (input.equals("PERM")) {
            return null;
        }

        for (String part : input.split("/")) {
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
        Player player  = event.getPlayer();

        if (playerClickedReason.containsKey(event.getPlayer().getUniqueId())) {

            String message = PlainTextComponentSerializer.plainText().serialize(event.message()).trim();

            switch (message.toLowerCase()) {

                case "cancel":
                    player.sendMessage("§aCanceling");
                    Bukkit.getScheduler().runTask(managePlayers.getInstance(), () -> {
                        event.getPlayer().openInventory(
                                managePlayers.getInstance().getBanGUI().getInventory(event.getPlayer())
                        );
                        playerClickedReason.remove(event.getPlayer().getUniqueId());
                    });
                    event.setCancelled(true);
                    return;
                    case "spam":

                        reasons.put(player.getUniqueId(), "SPAM");
                        playerClickedReason.remove(player.getUniqueId(), player.getUniqueId());

                        Bukkit.getScheduler().runTask(managePlayers.getInstance(), () -> {
                            event.getPlayer().openInventory(
                                    managePlayers.getInstance().getBanGUI().getInventory(event.getPlayer())
                            );
                        });
                        event.setCancelled(true);
                        return;

                        case "hacking":
                        reasons.put(player.getUniqueId(), "HACKING");
                        playerClickedReason.remove(player.getUniqueId(), player.getUniqueId());
                        Bukkit.getScheduler().runTask(managePlayers.getInstance(), () -> {
                            event.getPlayer().openInventory(
                                    managePlayers.getInstance().getBanGUI().getInventory(event.getPlayer())
                            );
                        });
                        event.setCancelled(true);
                        return;

                        case "esp":
                            reasons.put(player.getUniqueId(), "ESP");
                            playerClickedReason.remove(player.getUniqueId(), player.getUniqueId());
                            Bukkit.getScheduler().runTask(managePlayers.getInstance(), () -> {
                                event.getPlayer().openInventory(
                                        managePlayers.getInstance().getBanGUI().getInventory(event.getPlayer())

                                );
                            });
                            event.setCancelled(true);
                            return;

                            case "perm:":
                                reasons.put(player.getUniqueId(), "PERM");
                                playerClickedReason.remove(player.getUniqueId(), player.getUniqueId());
                                Bukkit.getScheduler().runTask(managePlayers.getInstance(), () -> {
                                    event.getPlayer().openInventory(
                                            managePlayers.getInstance().getBanGUI().getInventory(event.getPlayer())
                                    );
                                });
                                event.setCancelled(true);
                                return;


                default:
                    player.sendMessage("§cInvalid Reason: §c§l" + message);
                    event.setCancelled(true);
                    return;


            }

        }
        if (playerClickedDuration.containsKey(event.getPlayer().getUniqueId())) {
            try {


                String message = PlainTextComponentSerializer.plainText().serialize(event.message()).trim();

                Bukkit.getScheduler().runTask(managePlayers.getInstance(), () -> {
                    event.getPlayer().openInventory(
                            managePlayers.getInstance().getBanGUI().getInventory(event.getPlayer())
                    );
                });
                durations.put(event.getPlayer().getUniqueId(), (message));
                event.setCancelled(true);

            } catch (Exception e) {
                event.getPlayer().sendMessage("§cInvalid duration, duration example: §81d/2h/9m");
            }
        }
    }
}
