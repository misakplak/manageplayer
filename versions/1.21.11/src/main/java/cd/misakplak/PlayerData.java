package cd.misakplak;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
        yaml.save(file);
    }


    public String getString(String path){
        return yaml.getString(path);
    }

    public ItemStack[] getInventory(String path) {
        List<?> list = yaml.getList(path);

        if (list == null) {
            return new ItemStack[36];
        }

        return list.toArray(new ItemStack[0]);
    }

    public int getInt(String path){
        return yaml.getInt(path);
    }
    public boolean getBoolean(String path){
        return yaml.getBoolean(path);
    }
    public @Nullable ItemStack @NotNull [] getItemStack(String path){
        String item = yaml.getString(path);

        if(item == null) {
            return null;
        }

        return new ItemStack[]{ItemStack.of(Material.valueOf(item))};
    }
    public GameMode getGameMode(String path) {
        String value = yaml.getString(path);
        return value == null ? null : GameMode.valueOf(value);
    }

    public YamlConfiguration getConfig(){
        return yaml;
    }

    public void save() throws IOException {
        yaml.save(file);
    }

    public void reload() throws IOException {
        yaml.loadConfiguration(file);
    }
}
