package cd.misakplak;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.*;

public final class managePlayers extends JavaPlugin {

    private static managePlayers instance;


    @Override
    public void onEnable() {
        instance = this;

        getLogger().info("management ready :)");

        try {
            fileEventSaving = new FIleEventSaving(this);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        getServer().getPluginManager().registerEvents(fileEventSaving, this);

        try {
            getServer().getPluginManager().registerEvents(new LogsPlayerHistory(this), this);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        getCommand("manage").setExecutor(new managementCommand());
        getServer().getPluginManager().registerEvents(new managementGUI(), this);
        getServer().getPluginManager().registerEvents(new banGUI(), this);
        getServer().getPluginManager().registerEvents(new invGUI(), this);

        new Metrics(this, 32551);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        for (Player player : Bukkit.getOnlinePlayers()) {
            try {
                fileEventSaving.saveSnapshot(player.getUniqueId(), player);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        getLogger().info("management disabled :(");

        instance = null;
    }


    public static managePlayers getInstance() {
        return instance;
    }


    private Map<UUID, UUID> targetPlayer = new HashMap<>();

    public Map<UUID, UUID> getTargetPlayer() {
        return targetPlayer;
    }

    private final Map<UUID, String> currentSessions = new HashMap<>();

    private final banGUI banGUI = new banGUI();

    public banGUI getBanGUI() {
        return banGUI;
    }

    public Map<UUID, String> getCurrentSessions() {
        return currentSessions;
    }

    private FIleEventSaving fileEventSaving;



}
