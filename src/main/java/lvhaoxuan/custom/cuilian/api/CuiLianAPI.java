package lvhaoxuan.custom.cuilian.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lvhaoxuan.custom.cuilian.NewCustomCuiLianPro;
import lvhaoxuan.custom.cuilian.message.Message;
import lvhaoxuan.custom.cuilian.object.Level;
import lvhaoxuan.custom.cuilian.object.ProtectRune;
import lvhaoxuan.custom.cuilian.object.Stone;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.kyori.adventure.title.TitlePart;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.Material;

public class CuiLianAPI {

    public static boolean hasOffHand;

    static {
        hasOffHand = NewCustomCuiLianPro.judgeOffHand;
        if (hasOffHand) {
            try {
                EntityEquipment.class.getMethod("getItemInOffHand");
            } catch (NoSuchMethodException | SecurityException ex) {
                hasOffHand = false;
            }
        }
    }

    public static boolean canCuiLian(ItemStack item) {
        if (item != null && item.getType() != Material.AIR) {
            for (NewCustomCuiLianPro.ItemType type : NewCustomCuiLianPro.types) {
                if (type.type == item.getType()) {
                    return true;
                }
            }
        }
        return false;
    }

    public static ItemStack cuilian(Stone stone, ItemStack item, Player p) {
        if (canCuiLian(item)) {
            Level basicLevelObj = Level.byItemStack(item);
            int basicLevel = (basicLevelObj != null ? basicLevelObj.value : 0);
            Level toLevel;
            Random rnd = new Random();
            double probability = rnd.nextInt(100) + 1;
            boolean success = probability <= stone.chance.get(Level.levels.get(basicLevel + stone.riseLevel));
            //String sendMessage = null;
            Component sendMessage = null;
            if (success) {
                toLevel = Level.levels.get(basicLevel + stone.riseLevel);
                item = setItemLevel(item, toLevel);

                Component lvText = toLevel.lore.getFirst();
                p.sendTitlePart(TitlePart.TITLE, Component.text("淬炼成功", NamedTextColor.GREEN));
                p.sendTitlePart(TitlePart.SUBTITLE, lvText);

                //sendMessage = Message.SUCCESS.replace("%s", PlainTextComponentSerializer.plainText().serialize(toLevel.lore.getFirst()));
                sendMessage = Component.text(Message.SUCCESS).replaceText(builder -> builder.matchLiteral("%s").replacement(toLevel.lore.getFirst()));
                if (toLevel.value >= 5) {
                    Bukkit.getServer().sendMessage(Component.text(Message.SERVER_SUCCESS).replaceText(builder -> builder.matchLiteral("%p").replacement(p.displayName())
                            .matchLiteral("%d").replacement(stone.item.getItemMeta().displayName())
                            .matchLiteral("%s").replacement(toLevel.lore.getFirst())));
                    //Bukkit.getServer().sendMessage(Component.text(Message.SERVER_SUCCESS.replace("%p", PlainTextComponentSerializer.plainText().serialize(p.displayName())).replace("%d", PlainTextComponentSerializer.plainText().serialize(stone.item.getItemMeta().displayName())).replace("%s", PlainTextComponentSerializer.plainText().serialize(toLevel.lore.getFirst()))));
                }
            } else {
                int dropLevel = stone.dropLevel.toInteger();
                Level protectRune = Level.byProtectRune(item);
                if (protectRune != null) {
                    if (protectRune.value <= basicLevel) {
                        if (basicLevel - protectRune.value <= dropLevel) {
                            dropLevel = basicLevel - protectRune.value != 0 ? rnd.nextInt(basicLevel - protectRune.value ) + 1 : 0;
                        }
                        toLevel = Level.levels.get(basicLevel - dropLevel);
                        item = setItemLevel(item, toLevel);
                        int finalDropLevel = dropLevel;
                        sendMessage = Component.text(Message.CUILIAN_FAIL_PROTECT_RUNE).replaceText(builder -> builder.matchLiteral("%s").replacement(toLevel.lore.getFirst()).
                                matchLiteral("%d").replacement(String.valueOf(finalDropLevel)));
                        //sendMessage = Message.CUILIAN_FAIL_PROTECT_RUNE.replace("%s", PlainTextComponentSerializer.plainText().serialize(toLevel.lore.getFirst())).replace("%d", String.valueOf(dropLevel));
                    } else {
                        toLevel = Level.levels.get(basicLevel - dropLevel);
                        item = setItemLevel(item, toLevel);

                        //String lvText = PlainTextComponentSerializer.plainText().serialize(toLevel.lore.getFirst());
                        Component lvText = toLevel.lore.getFirst();
                        p.sendTitlePart(TitlePart.TITLE, Component.text("淬炼失败", NamedTextColor.RED));
                        p.sendTitlePart(TitlePart.SUBTITLE, lvText);

                        int finalDropLevel1 = dropLevel;
                        sendMessage = Component.text(Message.CUILIAN_FAIL).replaceText(builder -> builder.matchLiteral("%s").replacement(lvText)
                        .matchLiteral("%d").replacement(String.valueOf(finalDropLevel1)));
                        //sendMessage = Message.CUILIAN_FAIL.replace("%s", lvText).replace("%d", String.valueOf(dropLevel));
                    }
                } else {
                    toLevel = Level.levels.get(basicLevel - dropLevel);
                    item = setItemLevel(item, toLevel);
                    Component lvText = toLevel.lore.getFirst();
                    //String lvText = toLevel != null ? PlainTextComponentSerializer.plainText().serialize(toLevel.lore.getFirst()) : "§c§l淬炼消失";

                    p.sendTitlePart(TitlePart.TITLE, Component.text("淬炼失败", NamedTextColor.RED));
                    p.sendTitlePart(TitlePart.SUBTITLE, lvText);
                    int finalDropLevel2 = dropLevel;
                    sendMessage = Component.text(Message.CUILIAN_FAIL).replaceText(builder -> builder.matchLiteral("%s").replacement(lvText).matchLiteral("%d").replacement(String.valueOf(finalDropLevel2)));
                    //sendMessage = Message.CUILIAN_FAIL.replace("%s", lvText).replace("%d", String.valueOf(dropLevel));
                }
            }
            if (p != null) {
                p.sendMessage(sendMessage);
            }
        }
        return item;
    }

    public static ItemStack setItemLevel(ItemStack item, Level level) {
        if (canCuiLian(item)) {
            int basicLevel = (level != null ? level.value : 0);
            ItemMeta meta = item.getItemMeta();
            setDisplayName(item.getType(), meta, basicLevel);
            List<Component> lore = meta.hasLore() ? meta.lore() : new ArrayList<>();
            lore = replaceLore(lore);
            lore = cleanLevel(lore);
            lore = cleanProtectRune(lore);
            if (level != null) {
                if (!Message.UNDER_LINE.isEmpty()) {
                    lore.add(Component.text(Message.UNDER_LINE));
                }
                for (Component line : level.lore) {
                    lore.add(Component.text(NewCustomCuiLianPro.LEVEL_JUDGE).append(line));
                }
                for (String line : level.attribute.get(NewCustomCuiLianPro.typesInBag.get(item.getType()))) {
                    lore.add(Component.text(NewCustomCuiLianPro.LEVEL_JUDGE + getAttributeVaule(line)));
                }
            }
            Level protectRuneLevel = Level.byProtectRune(item);
            if (protectRuneLevel != null && protectRuneLevel.protectRune != null) {
                lore.add(Component.text(NewCustomCuiLianPro.PROTECT_RUNE_JUDGE + protectRuneLevel.protectRune.lore));
            }
            meta.lore(lore);
            item.setItemMeta(meta);
        }
        return item;
    }

    /**
     * 取出属性的随机数值
     * @param str
     * @return String
     */
    public static String getAttributeVaule(String str) {
        // 定义正则表达式以匹配颜色代码和数值范围
        Pattern pattern = Pattern.compile("(: [§&]#[a-zA-Z0-9]{6})([0-9]+)-([0-9]+)");
        Matcher matcher = pattern.matcher(str);
        if (!matcher.find()) {
            pattern = Pattern.compile("(: [§&][a-zA-Z0-9]{1})([0-9]+)-([0-9]+)");
            matcher = pattern.matcher(str);
            matcher.find();
        }
        int min = Integer.parseInt(matcher.group(2));
        int max = Integer.parseInt(matcher.group(3));
        String color = matcher.group(1);
        // 初始化随机数
        Random random = new Random();
        int att_num =  random.nextInt(max - min) + min;
        return str.substring(0,str.indexOf(":")) + color + att_num;
    }

    public static void setDisplayName(Material type, ItemMeta meta, int basicLevel) {
        if (NewCustomCuiLianPro.displayNameFormat == 1) {
            Component displayName = meta.hasDisplayName() ? meta.displayName() : Component.text(chineseDisplayName(type));
            //String displayName = meta.hasDisplayName() ? PlainTextComponentSerializer.plainText().serialize(meta.displayName()) : chineseDisplayName(type);
            displayName = displayName.replaceText(builder -> builder.matchLiteral("\\+[0-9]* ").replacement(""));
            displayName.append(Component.text("§f+" + basicLevel + " ")).append(displayName);
            meta.displayName(displayName);
        } else if (NewCustomCuiLianPro.displayNameFormat == 2) {
            //String displayName = meta.hasDisplayName() ? PlainTextComponentSerializer.plainText().serialize(meta.displayName()) : chineseDisplayName(type);
            Component displayName = meta.hasDisplayName() ? meta.displayName() :  Component.text(chineseDisplayName(type));
            displayName = displayName.replaceText(builder -> builder.matchLiteral("\\+[0-9]* ").replacement(""));
            displayName.append(Component.text(" +" + basicLevel));
            //displayName = displayName + " +" + basicLevel;
            meta.displayName(displayName);
        }
    }

    public static ItemStack addProtectRune(ItemStack item, ProtectRune protectRune) {
        if (item != null && item.getType() != Material.AIR) {
            if (protectRune != null) {
                ItemMeta meta = item.getItemMeta();
                List<Component> lore = meta.hasLore() ? meta.lore() : new ArrayList<>();
                lore = cleanProtectRune(lore);
                lore.add(Component.text(NewCustomCuiLianPro.PROTECT_RUNE_JUDGE + protectRune.lore));
                meta.lore(lore);
                item.setItemMeta(meta);
            }
        }
        return item;
    }

    public static List<Component> cleanLevel(List<Component> lore) {
        lore.removeIf(line -> PlainTextComponentSerializer.plainText().serialize(line).contains(NewCustomCuiLianPro.LEVEL_JUDGE) || PlainTextComponentSerializer.plainText().serialize(line).equals(Message.UNDER_LINE));
        return lore;
    }

    public static List<Component> cleanProtectRune(List<Component> lore) {
        lore.removeIf(line -> line.contains(Component.text(NewCustomCuiLianPro.PROTECT_RUNE_JUDGE)));
        return lore;
    }

    public static List<Component> replaceLore(List<Component> lore) {
        for (int i = 0; i < lore.size(); i++) {
            Component line = lore.get(i);
            for (String replace : NewCustomCuiLianPro.replaceLore) {
                if (line.contains(Component.text(replace))) {
                    lore.remove(i);
                }
            }
        }
        return lore;
    }

    public static Level getMinLevel(LivingEntity entity, EntityEquipment equipment) {
        int ret = -1;
        for (ItemStack item : equipment.getArmorContents()) {
            Level level = Level.byItemStack(item);
            int basicLevel = (level != null ? level.value : 0);
            ret = (ret == -1 ? basicLevel : Math.min(ret, basicLevel));
        }
        ItemStack item;
        try {
            item = entity.getEquipment().getItemInMainHand();
        } catch (NoSuchMethodError var2) {
            item = entity.getEquipment().getItemInHand();
        }
        Level level = Level.byItemStack(item);
        int basicLevel = (level != null ? level.value : 0);
        ret = (ret == -1 ? basicLevel : Math.min(ret, basicLevel));
        if (hasOffHand) {
            try {
                item = entity.getEquipment().getItemInOffHand();
            } catch (NoSuchMethodError var2) {
                item = new ItemStack(Material.AIR);
            }
            level = Level.byItemStack(item);
            basicLevel = (level != null ? level.value : 0);
            ret = (ret == -1 ? basicLevel : Math.min(ret, basicLevel));
        }
        return Level.levels.get(ret);
    }

    public static String chineseDisplayName(Material type) {
        return switch (type) {
            case BOW -> "§f弓";
            case IRON_SWORD -> "§f铁剑";
            case WOODEN_SWORD -> "§f木剑";
            case STONE_SWORD -> "§f石剑";
            case DIAMOND_SWORD -> "§f钻石剑";
            case GOLDEN_SWORD -> "§f金剑";
            case LEATHER_HELMET -> "§f皮头盔";
            case LEATHER_CHESTPLATE -> "§f皮胸甲";
            case LEATHER_LEGGINGS -> "§f皮护腿";
            case LEATHER_BOOTS -> "§f皮靴子";
            case CHAINMAIL_HELMET -> "§f锁链头盔";
            case CHAINMAIL_CHESTPLATE -> "§f锁链胸甲";
            case CHAINMAIL_LEGGINGS -> "§f锁链护腿";
            case CHAINMAIL_BOOTS -> "§f锁链靴子";
            case IRON_HELMET -> "§f铁头盔";
            case IRON_CHESTPLATE -> "§f铁胸甲";
            case IRON_LEGGINGS -> "§f铁护腿";
            case IRON_BOOTS -> "§f铁靴子";
            case DIAMOND_HELMET -> "§f钻石头盔";
            case DIAMOND_CHESTPLATE -> "§f钻石胸甲";
            case DIAMOND_LEGGINGS -> "§f钻石护腿";
            case DIAMOND_BOOTS -> "§f钻石靴子";
            case GOLDEN_HELMET -> "§f金头盔";
            case GOLDEN_CHESTPLATE -> "§f金胸甲";
            case GOLDEN_LEGGINGS -> "§f金护腿";
            case GOLDEN_BOOTS -> "§f金靴子";
            default -> type.name();
        };
    }
}
