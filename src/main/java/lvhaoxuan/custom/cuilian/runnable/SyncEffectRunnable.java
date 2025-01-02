package lvhaoxuan.custom.cuilian.runnable;

import github.saukiya.sxattribute.SXAttribute;
import github.saukiya.sxattribute.data.attribute.SXAttributeData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;
import lvhaoxuan.custom.cuilian.NewCustomCuiLianPro;
import lvhaoxuan.custom.cuilian.api.CuiLianAPI;
import lvhaoxuan.custom.cuilian.message.Message;
import lvhaoxuan.custom.cuilian.object.Level;
import lvhaoxuan.custom.cuilian.util.ParamGroup;
import lvhaoxuan.custom.cuilian.util.ReflectUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.entity.Player;

public class SyncEffectRunnable implements Runnable {

    public static HashMap<UUID, Level> tmpMap = new HashMap<>();

    @Override
    public void run() {
        for (LivingEntity le : getEntities()) {
            try {
                sync(le);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(SyncEffectRunnable.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            }
        }
    }

    public void sync(LivingEntity le) throws ClassNotFoundException {
        Level minLevel = CuiLianAPI.getMinLevel(le, le.getEquipment());
        if (minLevel != null && minLevel.suitEffect != null) {
            for (String potionStr : minLevel.suitEffect.potionEffect) {
                String[] args = potionStr.split(" ");
                String potion = args[0];
                int level = Integer.parseInt(args[1]);
                Bukkit.getScheduler().callSyncMethod(NewCustomCuiLianPro.ins, () -> {
                    le.addPotionEffect(new PotionEffect(PotionEffectType.getByName(potion), 20, level));
                    return null;
                });
            }
            if (NewCustomCuiLianPro.sxv2Enable && le instanceof Player) {
                SXAttributeData data = (SXAttributeData) ReflectUtil.doMethod(SXAttribute.getApi(), "getLoreData", new ParamGroup(null, LivingEntity.class), new ParamGroup(null, Class.forName("github.saukiya.sxattribute.data.condition.SXConditionType")), new ParamGroup(minLevel.suitEffect.attribute));
                SXAttribute.getApi().setEntityAPIData(SyncEffectRunnable.class, le.getUniqueId(), data);
            }
            if (NewCustomCuiLianPro.sxv3Enable && le instanceof Player) {
                SXAttributeData data = (SXAttributeData) ReflectUtil.doMethod(SXAttribute.getApi(), "loadListData", new ParamGroup(minLevel.suitEffect.attribute));
                SXAttribute.getApi().setEntityAPIData(SyncEffectRunnable.class, le.getUniqueId(), data);
            }
            if (!tmpMap.containsKey(le.getUniqueId())) {
                le.sendMessage(Component.text(Message.ENABLE_SUIT_EFFECT).replaceText(builder -> builder.matchLiteral("%s").replacement(minLevel.lore.getFirst())));
                //le.sendMessage(Message.ENABLE_SUIT_EFFECT.replace("%s", PlainTextComponentSerializer.plainText().serialize(minLevel.lore.getFirst()));
                tmpMap.put(le.getUniqueId(), minLevel);
            } else {
                Level level = tmpMap.get(le.getUniqueId());
                if (level != minLevel) {
                    le.sendMessage(Component.text(Message.DISENABLE_SUIT_EFFECT).replaceText(builder -> builder.matchLiteral("%s").replacement(level.lore.getFirst())));
                    //le.sendMessage(Message.DISENABLE_SUIT_EFFECT.replace("%s", PlainTextComponentSerializer.plainText().serialize(level.lore.getFirst())));
                    tmpMap.remove(le.getUniqueId());
                }
            }
        } else if (tmpMap.containsKey(le.getUniqueId())) {
            le.sendMessage(Component.text(Message.DISENABLE_SUIT_EFFECT).replaceText(builder -> builder.matchLiteral("%s").replacement(tmpMap.get(le.getUniqueId()).lore.getFirst())));
            //le.sendMessage(Message.DISENABLE_SUIT_EFFECT.replace("%s", PlainTextComponentSerializer.plainText().serialize(tmpMap.get(le.getUniqueId()).lore.getFirst())));
            tmpMap.remove(le.getUniqueId());
            if ((NewCustomCuiLianPro.sxv2Enable || NewCustomCuiLianPro.sxv3Enable) && le instanceof Player) {
                SXAttribute.getApi().removeEntityAPIData(SyncEffectRunnable.class, le.getUniqueId());
            }
        }
    }

    public static List<LivingEntity> getEntities() {
        List<LivingEntity> entities = new ArrayList<>();
        for (World world : Bukkit.getWorlds()) {
            if (!NewCustomCuiLianPro.otherEntitySuitEffect) {
                entities.addAll(world.getPlayers());
            } else {
                entities.addAll(world.getLivingEntities());
            }
        }
        return entities;
    }
}
