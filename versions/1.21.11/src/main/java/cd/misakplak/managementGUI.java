package cd.misakplak;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;


import java.io.IOException;
import java.util.List;
import java.util.UUID;

public class managementGUI implements Listener {

    public Inventory getInventory(Player player, Player sender) {


        Inventory managementgui = Bukkit.createInventory(null, 54, "§8Management");  {

            ItemStack filler = new MakeItem(Material.BLUE_STAINED_GLASS_PANE)

                    .setName(Component.empty())
                    .setLore((null))
                    .build();

            ItemStack kick = new MakeItem(Material.BRUSH)
                    .setName(MiniMessage.miniMessage().deserialize(
                            "<i><b><gradient:#AD3434:#D73D4C>ᴋɪᴄ</gradient><gradient:#D73D4C:#DD1818>ᴋ</gradient></b></i>"
                    ))
                    .setLore(List.of(
                            MiniMessage.miniMessage().deserialize(
                                    "<i><gradient:#AD3434:#D73D4C>ᴋɪᴄᴋ ᴛʜᴇ ᴘ</gradient><gradient:#D73D4C:#DD1818>ʟᴀʏᴇʀ</gradient></i>"
                            )
                    ))
                    .build();


            ItemStack ban = new MakeItem(Material.BARRIER)
                    .setName(MiniMessage.miniMessage().deserialize(
                            "<i><b><gradient:#AD3434:#AD3434>ʙ</gradient><gradient:#AD3434:#C7D7ED>ᴀ</gradient><gradient:#D73D4C:#DD1818>ɴ</gradient></b></i>"
                    ))
                    .setLore(List.of(
                            MiniMessage.miniMessage().deserialize(
                                    "<i><gradient:#AD3434:#D73D4C>ʙᴀɴ ᴛʜᴇ ᴘ</gradient><gradient:#D73D4C:#DD1818>ʟᴀʏᴇʀ</gradient></i>"
                            )
                    ))
                    .build();

            ItemStack inv = new MakeItem(Material.CHEST)
                    .setName(MiniMessage.miniMessage().deserialize(
                            "<i><b><gradient:#AD3434:#D73D4C>ᴠɪᴇᴡ ɪɴᴠᴇ</gradient><gradient:#D73D4C:#DD1818>ɴᴛᴏʀʏ</gradient></b></i>"
                    ))
                    .setLore(List.of(
                            MiniMessage.miniMessage().deserialize(
                                    "<i><gradient:#AD3434:#D73D4C>ᴠɪᴇᴡ ɪɴᴠᴇɴᴛᴏʀʏ ᴏꜰ ᴛ</gradient><gradient:#D73D4C:#DD1818>ʜᴇ ᴘʟᴀʏᴇʀ</gradient></i>"
                            )
                    ))
                    .build();

            ItemStack tpTo = new MakeItem(Material.ENDER_PEARL)
                    .setName(MiniMessage.miniMessage().deserialize(
                            "<i><b><gradient:#AD3434:#D73D4C>ᴛᴇʟᴇᴘ</gradient><gradient:#D73D4C:#DD1818>ᴏʀᴛ</gradient></b></i>"
                    ))
                    .setLore(List.of(
                            MiniMessage.miniMessage().deserialize(
                                    "<i><gradient:#AD3434:#D73D4C>ᴛᴇʟᴇᴘᴏʀᴛ ᴛᴏ ᴛʜᴇ</gradient><gradient:#D73D4C:#DD1818> ᴘʟᴀʏᴇʀ</gradient></i>"
                            )
                    ))
                    .build();

            ItemStack tpHere = new MakeItem(Material.ENDER_EYE)
                    .setName(MiniMessage.miniMessage().deserialize(
                            "<i><b><gradient:#AD3434:#D73D4C>ᴛᴇʟᴇᴘᴏʀᴛ </gradient><gradient:#D73D4C:#DD1818>ʜᴇʀᴇ</gradient></b></i>"
                    ))
                    .setLore(List.of(
                            MiniMessage.miniMessage().deserialize(
                                    "<i><gradient:#AD3434:#D73D4C>ᴛᴇʟᴇᴘᴏʀᴛ ᴛʜᴇ ᴘʟᴀ</gradient><gradient:#D73D4C:#DD1818>ʏᴇʀ ʜᴇʀᴇ</gradient></i>"
                            )
                    ))
                    .build();

            ItemStack history = new MakeItem(Material.CLOCK)
                    .setName(MiniMessage.miniMessage().deserialize(
                            "<i><b><gradient:#FD8B46:#53772D>ʜɪѕᴛᴏʀ</gradient><gradient:#53772D:#A469A2>ʏ</gradient></b></i>"
                    ))
                    .setLore(List.of(
                            MiniMessage.miniMessage().deserialize(
                                    "<i><b><gradient:#FD8B46:#53772D>ᴠɪᴇᴡ ᴘʟᴀʏᴇʀѕ ʜɪѕᴛ</gradient><gradient:#53772D:#A469A2>ᴏʀʏ</gradient></b></i>"
                            )
                    ))
                    .build();




            ItemStack head = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta headMeta = (SkullMeta) head.getItemMeta();
            headMeta.setOwningPlayer(player);
            head.setItemMeta(headMeta);

            int[] fillerslots = {
                    0, 1, 2, 3, 4, 5, 6, 7, 8,
                    9,  17,
                    18, 26,
                    27, 35,
                    36, 44,
                    45, 53
            };

            for(int slot : fillerslots) {
                managementgui.setItem(slot, filler);
            }

            managementgui.setItem(20, ban);
            managementgui.setItem(24, kick);
            managementgui.setItem(31, inv);
            managementgui.setItem(38, tpTo);
            managementgui.setItem(40, head);
            managementgui.setItem(42, tpHere);
            managementgui.setItem(43, history);

        }

        return managementgui;
    }
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        UUID targetUUID =  managePlayers.getInstance().getTargetPlayer().get(event.getWhoClicked().getUniqueId());
        Player target = Bukkit.getPlayer(targetUUID);
        if(!(event.getWhoClicked() instanceof Player)) {
            return;
        }



        if(!event.getView().getTitle().equals("§8Management")) {
            return;
        }
        event.setCancelled(true);



        ItemStack clicked = event.getCurrentItem();
        Player player = (Player) event.getWhoClicked();

        if(clicked == null || clicked.getType() == Material.AIR) {
            return;
        }

        if (clicked.getType() == Material.BARRIER) {
            if (!player.hasPermission("manage.ban")) {
                player.sendMessage("§cYou don't have permission to ban!");
                return;
            }
            player.openInventory(managePlayers.getInstance().getBanGUI().getInventory(player));
        }

        if (clicked.getType() == Material.ENDER_EYE) {

            if (!player.hasPermission("manage.tphere")) {
                player.sendMessage("§cYou cant teleport here!");
                return;
            }

            if (target == null) {
                player.sendMessage("§cYou cant tp here offline players!");
                return;
            }

            target.teleport(player.getLocation());
            player.sendMessage("§aTeleported here!");
        }

        if (clicked.getType() == Material.ENDER_PEARL) {
            if (!player.hasPermission("manage.tp")) {
                player.sendMessage("§cYou cant teleport to players!");
                return;
            }

            if (target == null) {
                player.sendMessage("§cYou cant tp to offline players!");
                return;
            }

            player.teleport(target.getLocation());
            player.sendMessage("§aTeleported!");
        }

        if (clicked.getType() == Material.PLAYER_HEAD) {
            player.sendMessage("§3You are managing " + target.getName());
            return;
        }

        if  (clicked.getType() == Material.BRUSH) {
            if (!player.hasPermission("manage.kick")) {
                player.sendMessage("§cYou cant kick players!");
                return;
            }

            if (target == null) {
                player.sendMessage("§cYou cant kick offline players!");
                return;
            }

            target.kick(Component.text("§3You have been kicked by §c§ka§r§8console§c§ka"));
            player.sendMessage("§akicked!");
        }

        if (clicked.getType() == Material.CLOCK) {
            try {
                LogsPlayerHistory gui = new LogsPlayerHistory();
                player.openInventory(gui.getInventory(targetUUID));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        if (clicked.getType() == Material.CHEST) {

            if (!player.hasPermission("manage.seeInv")) {
                player.sendMessage("§cYou don't have permission to open this inventory!");
                return;
            }

            invGUI invgui = new invGUI();
            player.openInventory(invgui.getInventory(player));
        }
    }
}