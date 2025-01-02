package lvhaoxuan.custom.cuilian.object;

import java.util.*;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Stone {

    public static HashMap<String, Stone> stones = new HashMap<>();
    public static Random rnd = new Random();
    public Map<Level, Double> chance;
    public ItemStack item;
    public String id;
    public LevelDrop dropLevel;
    public int riseLevel;

    public Stone(ItemStack item, String id, LevelDrop dropLevel, int riseLevel, Map<Level, Double> chance) {
        this.item = item;
        this.id = id;
        this.dropLevel = dropLevel;
        this.riseLevel = riseLevel;
        this.chance = chance;
    }

    public static Stone byItemStack(ItemStack item) {
        if (item != null && item.getType() != Material.AIR) {
            for (Stone stone : stones.values()) {
                if (stone.item.isSimilar(item)) {
                    return stone;
                }
            }
        }
        return null;
    }

    public static Stone deserialize(YamlConfiguration config, String path) {
        HashMap<Level, Double> map = new HashMap<>();
        ConfigurationSection cs = config.getConfigurationSection(path + ".Chance");
        if (cs != null) {
            for (String key : cs.getKeys(false)) {
                Level level = Level.levels.get(Integer.parseInt(key));
                if (level != null) {
                    map.put(level, config.getDouble(path + ".Chance." + key));
                }
            }
        }
        ItemStack ret = null;
        if (config.isConfigurationSection(path)) {
            String type = config.getString(path + ".Type");
            String name = config.getString(path + ".Name");
            List<Component> lore = new ArrayList<>();
            for (String str : config.getStringList(path + ".Lore")) {
                Component component = Component.text(str);
                lore.add(component);
            }
            ret = new ItemStack(Material.getMaterial(type.toUpperCase()));

            ItemMeta meta = ret.getItemMeta();
            meta.displayName(Component.text(name));
            meta.lore(lore);
            ret.setItemMeta(meta);
        }
        return new Stone(ret,
                path,
                new LevelDrop(config.getString(path + ".dropLevel")),
                config.getInt(path + ".riseLevel"),
                map);
    }

    public static class LevelDrop {

        public int min;
        public int max;

        public LevelDrop(String dropStr) {
            if (dropStr.contains(" ")) {
                String[] args = dropStr.split(" ");
                init(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
            } else {
                init(Integer.parseInt(dropStr));
            }
        }

        public void init(int drop) {
            this.min = drop;
            this.max = drop;
        }

        public void init(int up, int down) {
            this.min = Math.min(up, down);
            this.max = Math.max(up, down);
        }

        public int toInteger() {
            return min == max ? min : min + rnd.nextInt(max - min) + 1;
        }
    }
}
