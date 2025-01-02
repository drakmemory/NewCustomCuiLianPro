package lvhaoxuan.custom.cuilian.loader;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Logger;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.jetbrains.annotations.Nullable;
import lvhaoxuan.custom.cuilian.NewCustomCuiLianPro;
import lvhaoxuan.custom.cuilian.NewCustomCuiLianPro.ItemType;
import lvhaoxuan.custom.cuilian.movelevel.MoveLevelHandle;
import lvhaoxuan.custom.cuilian.object.Level;
import lvhaoxuan.custom.cuilian.object.Stone;
import lvhaoxuan.custom.cuilian.object.SuitEffect;
import org.bukkit.configuration.file.YamlConfiguration;

public class Loader {

    public static void loadLevels() {
        Level.levels.clear();
        if (!NewCustomCuiLianPro.ins.getDataFolder().exists()) {
            NewCustomCuiLianPro.ins.getDataFolder().mkdir();
        }
        File file = new File(NewCustomCuiLianPro.ins.getDataFolder(), "cuilian.yml");
        if (!file.exists()) {
            NewCustomCuiLianPro.ins.saveResource("cuilian.yml", true);
        }
        try {
            InputStreamReader reader = new InputStreamReader(new FileInputStream(file), "UTF-8");
            YamlConfiguration config = YamlConfiguration.loadConfiguration(reader);
            for (String key : config.getKeys(false)) {
                Level level = Level.deserialize(config, key);
                Level.levels.put(level.value, level);
            }
        } catch (FileNotFoundException | UnsupportedEncodingException ex) {
        }
    }

    public static void loadStones() {
        Stone.stones.clear();
        if (!NewCustomCuiLianPro.ins.getDataFolder().exists()) {
            NewCustomCuiLianPro.ins.getDataFolder().mkdir();
        }
        File file = new File(NewCustomCuiLianPro.ins.getDataFolder(), "stone.yml");
        if (!file.exists()) {
            NewCustomCuiLianPro.ins.saveResource("stone.yml", true);
        }
        try {
            InputStreamReader reader = new InputStreamReader(new FileInputStream(file), "UTF-8");
            YamlConfiguration config = YamlConfiguration.loadConfiguration(reader);
            for (String key : config.getKeys(false)) {
                Stone stone = Stone.deserialize(config, key);
                Stone.stones.put(key, stone);
            }
        } catch (FileNotFoundException | UnsupportedEncodingException ex) {
        }
    }

    public static void loadItems() {
        NewCustomCuiLianPro.types.clear();
        if (!NewCustomCuiLianPro.ins.getDataFolder().exists()) {
            NewCustomCuiLianPro.ins.getDataFolder().mkdir();
        }
        File file = new File(NewCustomCuiLianPro.ins.getDataFolder(), "items.yml");
        if (!file.exists()) {
            NewCustomCuiLianPro.ins.saveResource("items.yml", true);
        }
        try {
            InputStreamReader reader = new InputStreamReader(new FileInputStream(file), "UTF-8");
            YamlConfiguration config = YamlConfiguration.loadConfiguration(reader);
            for (String key : config.getKeys(false)) {
                for (String strType : config.getStringList(key)) {
                    ItemType type = new ItemType(key, strType);
                    NewCustomCuiLianPro.types.add(type);
                    NewCustomCuiLianPro.typesInBag.put(type.type, type.typeInBag);
                }
            }
        } catch (FileNotFoundException | UnsupportedEncodingException ex) {
        }
    }

    public static void loadConfig() {
        if (!NewCustomCuiLianPro.ins.getDataFolder().exists()) {
            NewCustomCuiLianPro.ins.getDataFolder().mkdir();
        }
        File file = new File(NewCustomCuiLianPro.ins.getDataFolder(), "config.yml");
        if (!file.exists()) {
            NewCustomCuiLianPro.ins.saveResource("config.yml", true);
        }
        try {
            InputStreamReader reader = new InputStreamReader(new FileInputStream(file), "UTF-8");
            YamlConfiguration config = YamlConfiguration.loadConfiguration(reader);
            NewCustomCuiLianPro.debug = config.getBoolean("debug");
            NewCustomCuiLianPro.otherEntitySuitEffect = config.getBoolean("OtherEntitySuitEffect");
            NewCustomCuiLianPro.PROTECT_RUNE_JUDGE = config.getString("PROTECT_RUNE_JUDGE");
            NewCustomCuiLianPro.LEVEL_JUDGE = config.getString("LEVEL_JUDGE");
            MoveLevelHandle.moveLevelInvTitle = config.getString("MoveLevelInvTitle");
            NewCustomCuiLianPro.judgeOffHand = config.getBoolean("JudgeOffHand");
            NewCustomCuiLianPro.displayNameFormat = config.getInt("DisplayNameFormat");
            NewCustomCuiLianPro.replaceLore = config.getStringList("ReplaceLore");
        } catch (FileNotFoundException | UnsupportedEncodingException ex) {
        }
    }

    public static String loadSuitEffectScriptStr(String name) {
        if (!NewCustomCuiLianPro.ins.getDataFolder().exists()) {
            NewCustomCuiLianPro.ins.getDataFolder().mkdir();
        }
        File folder = new File(NewCustomCuiLianPro.ins.getDataFolder(), "script");
        if (!folder.exists()) {
            folder.mkdir();
        }
        File file = new File(folder, name);
        if (!file.exists()) {
            try {
                file.createNewFile();
                try {
                    OutputStream out = new FileOutputStream(file);

                    try {
                        out.write(SuitEffect.defaultScript.getBytes());
                    } catch (Throwable var6) {
                        try {
                            out.close();
                        } catch (Throwable var5) {
                            var6.addSuppressed(var5);
                        }

                        throw var6;
                    }

                    out.close();
                } catch (IOException ex) {
                    if (NewCustomCuiLianPro.debug) {
                        Logger.getLogger(NewCustomCuiLianPro.ins.getName()).log(java.util.logging.Level.SEVERE, (String)null, ex);
                    }
                }
            } catch (IOException ignored) {
            }
        }
        StringBuffer buf = null;
        BufferedReader breader = null;

        try {
            breader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            buf = new StringBuffer();

            while(breader.ready()) {
                buf.append((char)breader.read());
            }

            breader.close();
        } catch (IOException ex) {
            if (NewCustomCuiLianPro.debug) {
                Logger.getLogger(NewCustomCuiLianPro.ins.getName()).log(java.util.logging.Level.SEVERE, (String)null, ex);
            }
        }
        return buf.toString();
    }

    public static Context loadMoveLevelScript() {
        if (!NewCustomCuiLianPro.ins.getDataFolder().exists()) {
            NewCustomCuiLianPro.ins.getDataFolder().mkdir();
        }
        File file = new File(NewCustomCuiLianPro.ins.getDataFolder(), "movelevelscript.js");
        if (!file.exists()) {
            NewCustomCuiLianPro.ins.saveResource("movelevelscript.js", true);
        }
        return loadScript(file);
    }

    public static @Nullable Context loadScript(File file) {
        if (file.exists()) {
            try {
                // 创建 Rhino 上下文
                Context context = Context.enter();

                // 初始化标准对象（如 Object, Array 等）
                Scriptable scope = context.initStandardObjects();

                // 读取脚本文件内容
                String script = new String(Files.readAllBytes(Paths.get(file.toURI())));

                // 评估脚本
                context.evaluateString(scope, script, file.getName(), 1, null);
                return context;
            } catch (IOException ex) {
                Logger.getLogger(Loader.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            } finally{
                Context.exit();
            }
        } else {
            Logger.getLogger(Loader.class.getName()).log(java.util.logging.Level.SEVERE, "脚本文件不存在: " + file.getAbsolutePath());
        }
        return null;
    }
}
