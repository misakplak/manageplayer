package cd.misakplak;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class managementCommand implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player p)) {
            return true;
        }

        if (args.length < 1) {
            p.sendMessage("§cUsage: /manage <player>");
            return true;
        }

        Player target = Bukkit.getPlayerExact(args[0]);
        if (target == null) {
            p.sendMessage("§cPlayer not found or offline");
            return true;
        }

        managementGUI gui = new managementGUI();
        p.openInventory(gui.getInventory(target, p));
        managePlayers.getInstance().getTargetPlayer().put(p.getUniqueId(), target.getUniqueId());
        return true;
    }
}
