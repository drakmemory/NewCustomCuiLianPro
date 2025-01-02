package lvhaoxuan.custom.cuilian.runnable;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;
import lvhaoxuan.custom.cuilian.api.CuiLianAPI;
import lvhaoxuan.custom.cuilian.loader.Loader;
import lvhaoxuan.custom.cuilian.object.Level;
import lvhaoxuan.custom.cuilian.particle.ParticleModel;
import lvhaoxuan.custom.cuilian.particle.ParticleModel2D;
import lvhaoxuan.custom.cuilian.particle.ParticleModel3D;
import lvhaoxuan.custom.cuilian.particle.ParticleModelLine;
import lvhaoxuan.custom.cuilian.particle.ParticleUtil;
import org.bukkit.entity.LivingEntity;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;

public class ScriptRunnable implements Runnable {

    public static boolean enbaleScript = true;
    public static HashMap<UUID, HashMap<String, Scriptable>> bindingsMap = new HashMap<>();
    public static HashMap<String, Context> engineMap = new HashMap<>();

    @Override
    public void run() {
        List<LivingEntity> entities = SyncEffectRunnable.getEntities();
        for (LivingEntity le : entities) {
            sync(le);
        }
    }

    public void sync(LivingEntity le) {
        UUID uuid = le.getUniqueId();
        Level minLevel = CuiLianAPI.getMinLevel(le, le.getEquipment());
        if (minLevel != null && minLevel.suitEffect != null) {
            try {
                if (enbaleScript) {
                    for (String script : minLevel.suitEffect.script) {
                        if (!engineMap.containsKey(script)) {
                            engineMap.put(script, getDefaultScriptEngine(script));
                        }
                        Context engine = engineMap.get(script);
                        bindingsMap.putIfAbsent(uuid, new HashMap<>());
                        Scriptable bindings = engine.initStandardObjects(); // 初始化标准对象
                        if (bindingsMap.get(uuid).containsKey(script)) {
                            bindings.put("bindings", bindingsMap.get(uuid).get(script), bindings); // 添加绑定
                        }
                        Object invocable = bindings.get("onEffectTick", bindings); // 获取onEffectTick函数
                        if (invocable instanceof Function function) {
                            function.call(engine, bindings, bindings, new Object[]{le});
                        }
                        bindingsMap.get(uuid).put(script, bindings);
                    }
                }
            } catch (Exception ex) {
                Logger.getLogger(SyncEffectRunnable.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
                enbaleScript = false;
            }
        }
    }

    public Context getDefaultScriptEngine(String script) {
        // 创建一个 Rhino 上下文
        Context engine = Context.enter();
        try {
            Scriptable scope = engine.initStandardObjects();
            uploadScriptClass(engine, ParticleModel.class, scope);
            uploadScriptClass(engine, ParticleModel2D.class, scope);
            uploadScriptClass(engine, ParticleModel3D.class, scope);
            uploadScriptClass(engine, ParticleModelLine.class, scope);
            uploadScriptClass(engine, ParticleUtil.class, scope);
            // 执行脚本
            engine.evaluateString(scope, script, "script", 1, null);
        } catch (Exception ex) {
            Logger.getLogger(Loader.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } finally {
            Context.exit();
        }
        return engine;
    }

    public static void uploadScriptClass(Context engine, Class<?> clazz, Scriptable scope) {
        String totalName = clazz.getSimpleName() + "StaticClass";
        String staticClassName = clazz.getSimpleName();
        // 将类放入绑定
        scope.put(totalName, scope, clazz);
        // 在 JavaScript 中定义静态类
        engine.evaluateString(scope, "var " + staticClassName + " = " + totalName + ".static;", "script", 1, null);
    }
}
