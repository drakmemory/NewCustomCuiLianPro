package lvhaoxuan.custom.cuilian.object;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import lvhaoxuan.custom.cuilian.NewCustomCuiLianPro;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Level {

    public static HashMap<Integer, Level> levels = new HashMap<>();
    public Integer value;
    public List<Component> lore;
    public HashMap<String, List<String>> attribute;
    public ProtectRune protectRune;
    public SuitEffect suitEffect;

    public Level(Integer value, List<Component> lore, HashMap<String, List<String>> attribute, ProtectRune protectRune, SuitEffect suitEffect) {
        this.value = value;
        this.lore = lore;
        this.attribute = attribute;
        this.protectRune = protectRune;
        this.suitEffect = suitEffect;
        if (protectRune != null) {
            protectRune.level = this;
        }
    }

    public static Level byItemStack(ItemStack item) {
        if (item != null && item.getType() != Material.AIR) {
            ItemMeta meta = item.getItemMeta();
            List<Component> lore = meta.hasLore() ? meta.lore() : new ArrayList<>();

            for (int i = 0; i < lore.size(); i++) {
                lore.set(i,lore.get(i).replaceText(builder->builder
                        .matchLiteral(NewCustomCuiLianPro.LEVEL_JUDGE).replacement("")
                        .matchLiteral(NewCustomCuiLianPro.PROTECT_RUNE_JUDGE).replacement("")));
                //NewCustomCuiLianPro.ins.getServer().getConsoleSender().sendMessage(lore.get(i));
            }
            for (Level level : levels.values()) {
               for (Component component : level.lore) {
                   for (Component comp : lore) {
                       if (PlainTextComponentSerializer.plainText().serialize(comp).equals(NewCustomCuiLianPro.LEVEL_JUDGE + PlainTextComponentSerializer.plainText().serialize(component))) {
                           return level;
                       }
                   }
               }
            }
        }
        return null;
    }

    public static Level byProtectRune(ItemStack item) {
        if (item != null && item.getType() != Material.AIR) {
            ItemMeta meta = item.getItemMeta();
            List<Component> lore = meta.lore() != null ? meta.lore() : new ArrayList<>();

            for (int i = 0; i < lore.size(); i++) {
                Component line = lore.get(i);
                //String line = PlainTextComponentSerializer.plainText().serialize(lore.get(i));
                lore.set(i,line.replaceText(builder -> builder.matchLiteral(NewCustomCuiLianPro.LEVEL_JUDGE).replacement("").matchLiteral(NewCustomCuiLianPro.PROTECT_RUNE_JUDGE).replacement("")));
                //lore.set(i,Component.text(line.replace(NewCustomCuiLianPro.LEVEL_JUDGE, "") .replace(NewCustomCuiLianPro.PROTECT_RUNE_JUDGE,"")));
            }
            for (Level level : levels.values()) {
                if (level.protectRune != null && lore.contains(level.protectRune.lore)) {
                    return level;
                }
            }
        }
        return null;
    }

    public static Level deserialize(YamlConfiguration config, String path) {
        HashMap<String, List<String>> map = new HashMap<>();
        map.put("Hand", new ArrayList<>());
        map.put("Helmet", new ArrayList<>());
        map.put("Chestplate", new ArrayList<>());
        map.put("Leggings", new ArrayList<>());
        map.put("Boots", new ArrayList<>());
        ConfigurationSection cs = config.getConfigurationSection(path + ".Attribute");
        if (cs != null) {
            for (String key : cs.getKeys(false)) {
                map.put(key, config.getStringList(path + ".Attribute." + key));
            }
        }
        Level new_level = new Level(Integer.parseInt(path),
                new ArrayList<>(),
                map,
                ProtectRune.deserialize(config, path + ".ProtectRune"),
                SuitEffect.deserialize(config, path + ".SuitEffect"));
        for(String ll : config.getStringList(path + ".Lore"))
        {
            new_level.lore.add(Component.text(ll));
        }
        return new_level;
    }
}
