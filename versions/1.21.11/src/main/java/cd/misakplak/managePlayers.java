package cd.misakplak;

import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.*;

public final class managePlayers extends JavaPlugin {

    private static managePlayers instance;
    private PlayerData playerData;
    private FIleEventSaving fileEventSaving;
    private LogsPlayerHistory logsPlayerHistory;


    @Override
    public void onEnable() {
        instance = this;


        try {
            playerData = new PlayerData(this);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        getLogger().info("management ready :)");


        fileEventSaving = new FIleEventSaving();
        logsPlayerHistory = new LogsPlayerHistory();


        getServer().getPluginManager().registerEvents(fileEventSaving, this);
        saveDefaultConfig();

        getServer().getPluginManager().registerEvents(logsPlayerHistory, this);

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
                try {
                    fileEventSaving.saveSnapshot(player.getUniqueId(), player);
                } catch (InvalidConfigurationException e) {
                    throw new RuntimeException(e);
                }
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

    public PlayerData getPlayerData() {
        return playerData;
    }

    public LogsPlayerHistory getLogsPlayerHistory() {
        return logsPlayerHistory;
    }

    /*
     *history page storing...
     * hashmaps and getters
     * for them
     */

    private final Map<UUID, Integer> historyPage = new HashMap<>();
    private final Map<UUID, Integer> chatPage = new HashMap<>();
    private final Map<UUID, Integer> commandsPage = new HashMap<>();

    public Map<UUID, Integer> getHistoryPage() {
        return historyPage;
    }

    public Map<UUID, Integer> getCommandsPage() {
        return commandsPage;
    }

    public Map<UUID, Integer> getChatPage() {
        return chatPage;
    }
}
