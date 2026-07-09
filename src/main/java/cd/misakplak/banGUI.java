package cd.misakplak;


import io.papermc.paper.event.packet.UncheckedSignChangeEvent;
import io.papermc.paper.math.Position;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.sign.Side;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.time.Duration;
import java.util.*;

public class banGUI implements Listener {

    private final Map<UUID,SignMode> signModes = new HashMap();
    private final Map<UUID, String> reasons = new HashMap<>();
    private final Map<UUID, String> durations = new HashMap<>();
    private final Map<UUID, Location> fakeLoc = new HashMap<>();



    public enum SignMode {
        REASON,
        DURATION
    }

    public Inventory getInventory(Player player) {
        Inventory gui = Bukkit.createInventory(player, 27, "§cBAN");

        ItemStack reason = new MakeItem(Material.OAK_SIGN)
                .setName((MiniMessage.miniMessage().deserialize(
                        "<i><b><gradient:#AD3434:#D73D4C><i><b>ʙᴀɴ ʀᴇᴀ</b></i></gradient><gradient:#D73D4C:#DD1818><i><b>ѕᴏɴ:</b></i></gradient></b></i>"
                )))
                .build();

        ItemStack duration = new MakeItem(Material.CLOCK)
                .setName((MiniMessage.miniMessage().deserialize(
                        "<i><b><gradient:#AD3434:#D73D4C><i><b>ᴅᴜʀᴀᴛɪ</b></i></gradient><gradient:#D73D4C:#DD1818><i><b>ᴏɴ:</b></i></gradient></b></i>"
                )))
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

        if(!event.getView().getTitle().equals("§cBAN")) {
            return;
        }



        ItemStack clicked = event.getCurrentItem();
        Player player = (Player) event.getWhoClicked();
        UUID targetUUID =  managePlayers.getInstance().getTargetPlayer().get(player.getUniqueId());

        Player target = Bukkit.getPlayer(targetUUID);

        if(clicked == null || clicked.getType() == Material.AIR) {
            return;
        }
        event.setCancelled(true);

        if (clicked.getType() == Material.OAK_SIGN) {

            signModes.put(player.getUniqueId(), SignMode.REASON);

            Location loc = player.getLocation().clone().add(0, -64, 0);
            fakeLoc.put(player.getUniqueId(), loc);
            Position pos = Position.block(fakeLoc.get(player.getUniqueId()));

            player.sendBlockChange(loc, Material.OAK_SIGN.createBlockData());
            player.sendSignChange(
                    fakeLoc.get(player.getUniqueId()),
                    List.of(
                    Component.text("-enter on line 1-"),
                    Component.text("Enter ban reason"),
                    Component.text("----------"),
                    Component.text("----------")
                    ));

            Bukkit.getScheduler().runTask(managePlayers.getInstance(), () -> {
                player.openVirtualSign(pos, Side.FRONT);
            });



        }

        if  (clicked.getType() == Material.CLOCK) {


            signModes.put(player.getUniqueId(), SignMode.DURATION);
            if (!fakeLoc.containsKey(player.getUniqueId())) {
                fakeLoc.put(player.getUniqueId(), player.getLocation().clone().add(0, -64, 0));
                return;
            }
            player.sendBlockChange(fakeLoc.get(player.getUniqueId()).getBlock().getLocation(), Material.OAK_SIGN.createBlockData());

            Position pos = Position.block(fakeLoc.get(player.getUniqueId()));




            player.sendSignChange(
                    fakeLoc.get(player.getUniqueId()),
                    List.of(
                            Component.text("-enter on line 1-"),
                            Component.text("1d/20m/30s"),
                            Component.text("----------"),
                            Component.text("----------")

                    ));

            Bukkit.getScheduler().runTask(managePlayers.getInstance(), () -> {
                player.openVirtualSign(pos, Side.FRONT);
            });
        }

        if (clicked.getType() == Material.YELLOW_CONCRETE) {

            if (reasons.get(player.getUniqueId()) == null) {
                player.sendMessage("§cEnter reason first");
            }

            Duration duration = parseDuration(durations.get(player.getUniqueId()));


           target.ban(reasons.get(player.getUniqueId()),duration, null);

        }

    }


    @EventHandler
    public void onSign(UncheckedSignChangeEvent event){

            Player player = event.getPlayer();

            SignMode mode = signModes.remove(player.getUniqueId());

            if (mode == null) {
                return;
            }

                switch (mode) {

                case REASON -> {


                    String input = PlainTextComponentSerializer.plainText().serialize(event.lines().get(1));
                    reasons.put(player.getUniqueId(), input);

                }


                case DURATION -> {


                    Location loc = fakeLoc.remove(player.getUniqueId());
                    if (loc != null) {
                        player.sendBlockChange(loc, loc.getBlock().getBlockData());
                    }

                    String input = PlainTextComponentSerializer.plainText().serialize(event.lines().get(1));
                    durations.put(player.getUniqueId(), input);
                }

                default -> throw new IllegalStateException("Unexpected value");
            }
        }

        public Duration parseDuration(String input) {

        input = input.toLowerCase();

        if (input.contains("perm")) {
            return null;
        }

            int amount = Integer.parseInt(input.substring(0, input.length() - 1));
            char unit = input.charAt(input.length() - 1);

            return switch (unit) {
                case 'd' -> Duration.ofDays(amount);
                case 'h' -> Duration.ofHours(amount);
                case 'm' -> Duration.ofMinutes(amount);
                default -> throw new IllegalArgumentException();
            };
        }
    }
