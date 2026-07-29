package cd.misakplak;

import org.bukkit.GameMode;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class PlayerData {

    private final File folder;
    private final Map<UUID, YamlConfiguration> configs = new HashMap<>();



    public PlayerData(JavaPlugin plugin) throws IOException {
        folder = new File(plugin.getDataFolder(), "PlayerData");

        if (!folder.exists()) {
            folder.mkdirs();
        }
    }

    private File getFile(UUID uuid) {
        return new File(folder, uuid + ".yaml");
    }

    public YamlConfiguration getConfig(UUID uuid) throws IOException {
        if (configs.containsKey(uuid)) {
            return configs.get(uuid);
        }

        File file = getFile(uuid);

        if (!file.exists()) {
            file.createNewFile();
        }

        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
        configs.put(uuid, yaml);

        return yaml;
    }

    public void set(UUID uuid, String path, Object value) throws IOException {
        File file = getFile(uuid);
        YamlConfiguration yaml = getConfig(uuid);
        yaml.set(path, value);
        yaml.save(file);
    }

    public String getString(UUID uuid, String path) throws IOException {
        return getConfig(uuid).getString(path);
    }
    public List<String> getStringList(UUID uuid, String path) throws IOException {
        return getConfig(uuid).getStringList(path);
    }

    public int getInt(UUID uuid, String path) throws IOException {
        return getConfig(uuid).getInt(path);
    }

    public double getDouble(UUID uuid, String path) throws IOException {
        return getConfig(uuid).getDouble(path);
    }

    public boolean getBoolean(UUID uuid, String path) throws IOException {
        return getConfig(uuid).getBoolean(path);
    }

    public ItemStack[] getInventory(UUID uuid, String path) throws IOException, InvalidConfigurationException {
        List<?> list = getConfig(uuid).getList(path);

        if (list == null) {
            return new ItemStack[41];
        }


        return list.toArray(new ItemStack[0]);
    }

    public ItemStack getItem(UUID uuid, String path) throws IOException{
        return getConfig(uuid).getItemStack(path);
    }

    public GameMode getGameMode(UUID uuid, String path) throws IOException {
        String value = getConfig(uuid).getString(path);
        return value == null ? null : GameMode.valueOf(value);
    }

    public ConfigurationSection getSection(UUID uuid, String path) throws IOException {
        return getConfig(uuid).getConfigurationSection(path);
    }


    public void unload(UUID uuid) {
        configs.remove(uuid);
    }


    public void save(UUID uuid) throws IOException {
        getConfig(uuid).save(getFile(uuid));
    }

    public void reload(UUID uuid) throws IOException, InvalidConfigurationException {
        YamlConfiguration yaml = getConfig(uuid);
        yaml.load(getFile(uuid));
    }

    public void remove(UUID uuid, String path) throws IOException {

        File file = getFile(uuid);
        YamlConfiguration yaml = getConfig(uuid);


        yaml.set(path, null);
        yaml.save(file);
    }
}