package cd.misakplak;

import org.bukkit.GameMode;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class PlayerData {

    private final File file;
    private final YamlConfiguration yaml;

    public PlayerData(JavaPlugin plugin) throws IOException {

        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }

        file = new File(plugin.getDataFolder(), "PlayerData.yml");

        if (!file.exists()) {
            file.createNewFile();
        }

        yaml = YamlConfiguration.loadConfiguration(file);
    }

    public void set(String path, Object value) throws IOException {
        yaml.set(path, value);
    }

    public String getString(String path) {
        return yaml.getString(path);
    }
    public List<String> getStringList(String path) {
        return yaml.getStringList(path);
    }

    public int getInt(String path) {
        return yaml.getInt(path);
    }

    public double getDouble(String path) {
        return yaml.getDouble(path);
    }

    public boolean getBoolean(String path) {
        return yaml.getBoolean(path);
    }

    public ItemStack[] getInventory(String path) throws IOException, InvalidConfigurationException {
        List<?> list = yaml.getList(path);

        if (list == null) {
            return new ItemStack[41];
        }


        return list.toArray(new ItemStack[0]);
    }

    public ItemStack getItem(String path) {
        return yaml.getItemStack(path);
    }

    public GameMode getGameMode(String path) {
        String value = yaml.getString(path);
        return value == null ? null : GameMode.valueOf(value);
    }

    public ConfigurationSection getSection(String path) {
        return yaml.getConfigurationSection(path);
    }

    public YamlConfiguration getConfig() {
        return yaml;
    }

    public void save() throws IOException {
        yaml.save(file);
        try {
            reload();
        } catch (InvalidConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    public void reload() throws IOException, InvalidConfigurationException {
        yaml.load(file);
    }
}