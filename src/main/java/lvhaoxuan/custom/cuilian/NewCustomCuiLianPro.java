package lvhaoxuan.custom.cuilian;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

import lvhaoxuan.custom.cuilian.commander.Commander;
import lvhaoxuan.custom.cuilian.message.Message;
import lvhaoxuan.custom.cuilian.listener.FurnaceListener;
import lvhaoxuan.custom.cuilian.listener.ProtectRuneListener;
import lvhaoxuan.custom.cuilian.loader.Loader;
import lvhaoxuan.custom.cuilian.metrics.Metrics;
import lvhaoxuan.custom.cuilian.movelevel.MoveLevelHandle;
import lvhaoxuan.custom.cuilian.runnable.ScriptRunnable;
import lvhaoxuan.custom.cuilian.runnable.SyncEffectRunnable;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.BlastingRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.plugin.java.JavaPlugin;

public final class NewCustomCuiLianPro extends JavaPlugin {

    public static boolean debug;
    public static String PROTECT_RUNE_JUDGE;
    public static String LEVEL_JUDGE;
    public static NewCustomCuiLianPro ins;
    public static HashMap<Material, String> typesInBag = new HashMap<>();
    public static List<ItemType> types = new ArrayList<>();
    public static boolean otherEntitySuitEffect;
    public static boolean judgeOffHand;
    public static int displayNameFormat;
    public static List<String> replaceLore;
    public static boolean apEnable = false;
    public static boolean sxv2Enable = false;
    public static boolean sxv3Enable = false;

    @Override
    public void onEnable() {
        ins = this;
        new Metrics(this, 7315);
        this.getServer().getConsoleSender().sendMessage("§7[§e" + this.getName() + "§7]§a作者anyi(暗忆)|QQ1239124460");
        if (this.getServer().getPluginManager().getPlugin("AttributePlus") != null) {
            this.getServer().getConsoleSender().sendMessage("§7[§e" + this.getName() + "§7]§a检测到AttributePlus插件，属性模块加载");
            apEnable = true;
        }
        if (this.getServer().getPluginManager().getPlugin("SX-Attribute") != null) {
            if (Objects.requireNonNull(this.getServer().getPluginManager().getPlugin("SX-Attribute")).getDescription().getVersion().startsWith("2")) {
                sxv2Enable = true;
                this.getServer().getConsoleSender().sendMessage("§7[§e" + this.getName() + "§7]§a检测到SX-AttributeV2.X插件，属性模块加载");
            } else if (Objects.requireNonNull(this.getServer().getPluginManager().getPlugin("SX-Attribute")).getDescription().getVersion().startsWith("3")) {
                this.getServer().getConsoleSender().sendMessage("§7[§e" + this.getName() + "§7]§a检测到SX-AttributeV3.X插件，属性模块加载");
                sxv3Enable = true;
            }else if (Objects.requireNonNull(this.getServer().getPluginManager().getPlugin("SX-Attribute")).getDescription().getVersion().startsWith("4")) {
                this.getServer().getConsoleSender().sendMessage("§7[§e" + this.getName() + "§7]§a检测到SX-AttributeV4.X插件，属性模块加载");
                sxv3Enable = true;
            }
        }
        enableConfig();
        Objects.requireNonNull(this.getServer().getPluginCommand("cuilian")).setExecutor(new Commander());
        this.getServer().getPluginManager().registerEvents(new FurnaceListener(), this);
        this.getServer().getPluginManager().registerEvents(new ProtectRuneListener(), this);
        setRecipe();
        Bukkit.getScheduler().runTaskTimerAsynchronously(NewCustomCuiLianPro.ins, new ScriptRunnable(), 0, 2);
        Bukkit.getScheduler().runTaskTimerAsynchronously(NewCustomCuiLianPro.ins, new SyncEffectRunnable(), 0, 10);
    }

    public static void enableConfig() {
        Message.loadMessages();
        Loader.loadConfig();
        Loader.loadItems();
        Loader.loadLevels();
        Loader.loadStones();
        MoveLevelHandle.init();
    }

    public static void setRecipe() {
        for (ItemType type : types) {
            for (int durability = 0; durability <= type.type.getMaxDurability(); durability++) {
                NamespacedKey key = new NamespacedKey(ins, "custom_smelting_" + type.type + "_" + durability);
                RecipeChoice.MaterialChoice input = new RecipeChoice.MaterialChoice(Objects.requireNonNull(Material.getMaterial(type.type.name())));
                BlastingRecipe recipe = new BlastingRecipe(key, type.toItemStack(), input, 0.7f, 200);
                try {
                    ins.getServer().addRecipe(recipe);
                } catch (IllegalStateException ignored) {
                }
            }
        }
    }

    public static class ItemType {

        public String typeInBag;
        public String baseType;
        public Material type;

        public ItemType(String typeInBag, String baseType) {
            this.typeInBag = typeInBag;
            this.baseType = baseType;
            if (baseType.contains(":")) {
                String[] args = baseType.split(":");
                String strType = args[0];
                String strData = args[1];
                Pattern pattern = Pattern.compile("[0-9]*");
                if (pattern.matcher(strType).matches()) {
                    throw new IllegalArgumentException("NewCustomCuiLianPro#ItemType 不允许数字");
                }
                type = Material.getMaterial(strType);
                int data = Integer.parseInt(strData);
            } else {
                type = Material.getMaterial(baseType);
            }
            if (type == null) {
                throw new IllegalArgumentException("NewCustomCuiLianPro#ItemType 无法获取 items.yml 中的："+baseType);
            }
        }

        public ItemStack toItemStack() {
            return new ItemStack(type);
        }
    }
}
