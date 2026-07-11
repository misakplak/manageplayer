package cd.misakplak;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class invGUI implements Listener {

    private Map<UUID, UUID> playersOnInv = new HashMap<>();



    public Inventory getInventory(Player player) {
        playersOnInv.put(player.getUniqueId(), player.getUniqueId());
        Bukkit.getPlayer(playersOnInv.get(player.getUniqueId())).sendMessage("invsee");
        Inventory gui = Bukkit.createInventory(player, 54, "§cInvSee");

        UUID targetUUID = managePlayers.getInstance().getTargetPlayer().get(player.getUniqueId());
        Player target = Bukkit.getPlayer(targetUUID);

        ItemStack filler = new MakeItem(Material.LIGHT_GRAY_STAINED_GLASS_PANE)
                .setName("")
                .build();

        ItemStack level = new MakeItem(Material.EXPERIENCE_BOTTLE)
                .setName(MiniMessage.miniMessage().deserialize("<i><b><gradient:#CBA981:#FD8B46><shadow:#4D743E:1><i><b>ʟ</b></i></shadow><shadow:#51655E:1><i><b>ᴇ</b></i></shadow><shadow:#55567D:1><i><b>ᴠ</b></i></shadow><shadow:#59479D:1><i><b>ᴇ</b></i></shadow><shadow:#5D38BC:1><i><b>ʟ</b></i></shadow></gradient></b></i>"))
                .setLore(Collections.singletonList(MiniMessage.miniMessage().deserialize("<i><b><gradient:#CBA981:#FD8B46><shadow:#4D743E:1><i><b>x</b></i></shadow><shadow:#5D38BC:1><i><b>ᴘ </b></i></shadow></gradient></b></i>" + target.getLevel())))
                .build();

        ItemStack gameMode = new MakeItem(Material.IRON_DOOR)
                .setName(MiniMessage.miniMessage().deserialize("<i><b><gradient:#CBA981:#FD8B46><shadow:#4D743E:1><i><b>ɢ</b></i></shadow><shadow:#4F6B50:1><i><b>ᴀ</b></i></shadow><shadow:#526362:1><i><b>ᴍ</b></i></shadow><shadow:#545A74:1><i><b>ᴇ</b></i></shadow><shadow:#565286:1><i><b>ᴍ</b></i></shadow><shadow:#584998:1><i><b>ᴏ</b></i></shadow><shadow:#5B41AA:1><i><b>ᴅ</b></i></shadow><shadow:#5D38BC:1><i><b>ᴇ </b></i></shadow></gradient></b></i>"))
                .setLore(Collections.singletonList(MiniMessage.miniMessage().deserialize("<i><b><gradient:#CBA981:#FD8B46><shadow:#4D743E:1><i><b>ɢ</b></i></shadow><shadow:#5D38BC:1><i><b>ᴍ </b></i></shadow></gradient></b></i>"  + target.getGameMode().toString().toLowerCase())))
                .build();

        ItemStack health = new MakeItem(Material.ENCHANTED_GOLDEN_APPLE)
                .setName(MiniMessage.miniMessage().deserialize("<i><b><gradient:#CBA981:#FD8B46><shadow:#4D743E:1><i><b>ʜ</b></i></shadow><shadow:#506857:1><i><b>ᴇ</b></i></shadow><shadow:#535C70:1><i><b>ᴀ</b></i></shadow><shadow:#57508A:1><i><b>ʟ</b></i></shadow><shadow:#5A44A3:1><i><b>ᴛ</b></i></shadow><shadow:#5D38BC:1><i><b>ʜ</b></i></shadow></gradient></b></i>"))
                .setLore(Collections.singletonList(MiniMessage.miniMessage().deserialize("<i><b><gradient:#CBA981:#FD8B46><shadow:#4D743E:1><i><b>ʜ</b></i></shadow><shadow:#506857:1><i><b>ᴇ</b></i></shadow><shadow:#535C70:1><i><b>ᴀ</b></i></shadow><shadow:#57508A:1><i><b>ʀ</b></i></shadow><shadow:#5A44A3:1><i><b>ᴛ</b></i></shadow><shadow:#5D38BC:1><i><b>ѕ </b></i></shadow></gradient></b></i>" + target.getHealth())))
                .build();


        ItemStack hunger = new MakeItem(Material.COOKED_BEEF)
                .setName(MiniMessage.miniMessage().deserialize("<i><b><gradient:#CBA981:#FD8B46><shadow:#4D743E:1><i><b>ʜ</b></i></shadow><shadow:#506857:1><i><b>ᴜ</b></i></shadow><shadow:#535C70:1><i><b>ɴ</b></i></shadow><shadow:#57508A:1><i><b>ɢ</b></i></shadow><shadow:#5A44A3:1><i><b>ᴇ</b></i></shadow><shadow:#5D38BC:1><i><b>ʀ</b></i></shadow></gradient></b></i>"))
                .setLore(Collections.singletonList(MiniMessage.miniMessage().deserialize("<i><b><gradient:#CBA981:#FD8B46><shadow:#4D743E:1><i><b>ꜰ</b></i></shadow><shadow:#526068:1><i><b>ᴏ</b></i></shadow><shadow:#584C92:1><i><b>ᴏ</b></i></shadow><shadow:#5D38BC:1><i><b>ᴅ </b></i></shadow></gradient></b></i>" + target.getFoodLevel())))
                .build();


        int[] slots = {
                4,
                9, 10, 11, 12, 14, 15, 16, 17
        };

        for (int slot : slots) {
            gui.setItem(slot, filler);
        }


        Bukkit.getScheduler().runTaskTimer(
                managePlayers.getInstance(),
                () -> {

                    for (int i = 9; i <= 35; i++) {
                        ItemStack item = player.getInventory().getItem(i);

                        if (item != null) {
                            gui.setItem(18 + (i -9), item.clone());
                        }
                    }

                    for (int i = 0; i <= 8; i++) { //hotbar (work pls pls)

                        ItemStack item = player.getInventory().getItem(i);


                        if (item != null) {
                            gui.setItem(45 + i, item.clone());
                        }
                    }
                },
                0L,
                3L
        );

        ItemStack helmet = target.getInventory().getHelmet();
        ItemStack chestplate = target.getInventory().getChestplate();
        ItemStack leggings = target.getInventory().getLeggings();
        ItemStack boots = target.getInventory().getBoots();
        gui.setItem(0, helmet);
        gui.setItem(1, chestplate);
        gui.setItem(2, leggings);
        gui.setItem(3, boots);
        gui.setItem(5, level);
        gui.setItem(6, hunger);
        gui.setItem(7, health);
        gui.setItem(8, gameMode);
        gui.setItem(13, target.getInventory().getItemInOffHand());



        return gui;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {

        if (!event.getView().getTitle().equals("§cInvSee")) {
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
    }
    
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {

        Player player = (Player) event.getPlayer();

        if (playersOnInv.containsKey(player.getUniqueId())) {
            player.sendMessage("closed!");
            playersOnInv.remove(player.getUniqueId());
        }
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {

        Player player = (Player) event.getPlayer();

        if (playersOnInv.containsKey(player.getUniqueId())) {
            playersOnInv.remove(player.getUniqueId());
        }
    }
}
