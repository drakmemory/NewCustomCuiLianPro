package lvhaoxuan.custom.cuilian.movelevel;

import java.util.ArrayList;
import java.util.List;
import lvhaoxuan.custom.cuilian.api.CuiLianAPI;
import lvhaoxuan.custom.cuilian.loader.Loader;
import lvhaoxuan.custom.cuilian.object.Level;
import lvhaoxuan.custom.cuilian.gui.Gui;
import lvhaoxuan.custom.cuilian.gui.GuiButton;
import lvhaoxuan.custom.cuilian.gui.GuiSlot;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;

public class MoveLevelHandle {

    public static Context engine;
    public static String moveLevelInvTitle;
    public static boolean enableMoveLevel = true;

    public static void init() {
        engine = Loader.loadMoveLevelScript();
    }

    public static void open(Player p) {
        if (enableMoveLevel) {
            ItemStack background;
            try {
                background = Bukkit.getItemFactory().createItemStack("minecraft:stained_glass_pane");
                ItemMeta meta = background.getItemMeta();
                meta.displayName(Component.text(" "));
                background.setItemMeta(meta);
                background.displayName();
            } catch (Exception ex) {
                background = Bukkit.getItemFactory().createItemStack("minecraft:black_stained_glass_pane");
                ItemMeta meta = background.getItemMeta();
                meta.displayName(Component.text(" "));
                background.setItemMeta(meta);
            }
            Gui gui = new Gui(moveLevelInvTitle, 9, background);
            GuiSlot slot1 = new GuiSlot(2);
            gui.addSlot(slot1);
            GuiSlot slot2 = new GuiSlot(4);
            gui.addSlot(slot2);
            ItemStack item = new ItemStack(Material.ANVIL);
            ItemMeta meta = item.getItemMeta();
            meta.displayName(Component.text("§a淬炼移星"));
            List<Component> lore = new ArrayList<>();
            lore.add(Component.text("§7把左边装备所有等级移给右边装备。"));
            meta.lore(lore);
            item.setItemMeta(meta);
            GuiButton button = new GuiButton(6, item) {

                @Override
                public void click(InventoryClickEvent e) {
                    Inventory inv = e.getInventory();
                    ItemStack left = inv.getItem(2);
                    ItemStack right = inv.getItem(4);
                    if (CuiLianAPI.canCuiLian(left) && CuiLianAPI.canCuiLian(right)) {
                        try {
                            Level level1 = Level.byItemStack(left);
                            int leftLevel = (level1 != null ? level1.value : 0);
                            if (leftLevel != 0) {
                                Level level2 = Level.byItemStack(right);
                                int rightLevel = (level2 != null ? level2.value : 0);
                                // 使用Rhino引擎执行JavaScript代码
                                Context cx = Context.enter();
                                try{
                                    Scriptable scope = cx.initStandardObjects();
                                    Object invocable = scope.get("handle", scope);
                                    if (invocable instanceof Function function) {
                                        Object result = function.call(cx, scope, scope, new Object[] { leftLevel, rightLevel });
                                        int targetLevel = ((Double) Context.jsToJava(result, Double.class)).intValue();
                                        targetLevel = Math.min(targetLevel, Level.levels.size());
                                        Level targetLevelObj = Level.levels.get(targetLevel);
                                        inv.setItem(4, CuiLianAPI.setItemLevel(right, targetLevelObj));
                                        inv.setItem(2, CuiLianAPI.setItemLevel(left, null));
                                    }
                                } finally {
                                    Context.exit();
                                }
                            }
                        } catch (Exception ex) {
                            enableMoveLevel = false;
                        }
                    }
                }
            };
            gui.addButton(button);
            gui.open(p);
        }
    }
}
