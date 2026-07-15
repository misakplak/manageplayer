package cd.misakplak;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class managementCommand implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (!(sender instanceof Player p)) {
            return true;
        }

        Player sender1 = (Player) sender;
        Player player = Bukkit.getPlayer(args[0]);
        UUID targetUUID = player.getUniqueId();
        OfflinePlayer target = Bukkit.getOfflinePlayer(targetUUID);

        if (args.length == 0) {
            p.sendMessage("§cUsage: /manage <player>");
            return true;
        }



        if (target == null) {
            p.sendMessage("§cPlayer not found or offline");
            return true;
        }

        managementGUI gui = new managementGUI();
        p.openInventory(gui.getInventory(target, p));
        managePlayers.getInstance().getTargetPlayer().put(sender1.getUniqueId(), target.getUniqueId());
        return true;
    }
}
