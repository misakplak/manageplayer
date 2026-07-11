package cd.misakplak;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public final class managePlayers extends JavaPlugin {

    private static managePlayers instance;
    public static managePlayers getInstance() {
        return instance;
    }


    private Map<UUID, UUID> targetPlayer = new HashMap<>();

    public Map<UUID, UUID> getTargetPlayer() {
        return targetPlayer;
    }

    private final banGUI banGUI = new banGUI();

    public banGUI getBanGUI() {
        return banGUI;
    }




    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;
        getLogger().info("management ready :)");
        getCommand("manage").setExecutor(new managementCommand());
        getServer().getPluginManager().registerEvents(new managementGUI(), this);
        getServer().getPluginManager().registerEvents(new banGUI(), this);
        getServer().getPluginManager().registerEvents(new invGUI(), this);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("management disabled :(");
        instance = null;
    }
}
