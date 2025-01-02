package lvhaoxuan.custom.cuilian.object;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ProtectRune {

    public ItemStack item;
    public Component lore;
    public Level level;

    public ProtectRune(ItemStack item, Component lore) {
        this.item = item;
        this.lore = lore;
    }

    public static ProtectRune byItemStack(ItemStack item) {
        if (item != null && item.getType() != Material.AIR) {
            for (Level level : Level.levels.values()) {
                if (level.protectRune != null && level.protectRune.item.isSimilar(item)) {
                    return level.protectRune;
                }
            }
        }
        return null;
    }

    public static ProtectRune deserialize(YamlConfiguration config, String path) {
        if (config.get(path) == null) {
            return null;
        }
        ItemStack ret = null;
        if (config.isConfigurationSection(path)) {
            String type = config.getString(path + ".Type");
            int data = config.getInt(path + ".Data");
            String name = config.getString(path + ".Name");
            List<Component> lore = new ArrayList<>();
            for (String str : config.getStringList(path + ".Lore")) {
                Component component = Component.text(str);
                lore.add(component);
            }
            ret = new ItemStack(Material.getMaterial(type.toUpperCase()), 1, (short)data);

            ItemMeta meta = ret.getItemMeta();
            meta.displayName(Component.text(name));
            meta.lore(lore);
            ret.setItemMeta(meta);
        }
        return new ProtectRune(ret,
                Component.text(config.getString(path + ".AddLore")));
    }
}
