package lvhaoxuan.custom.cuilian.listener;

import java.util.HashMap;
import lvhaoxuan.custom.cuilian.api.CuiLianAPI;
import lvhaoxuan.custom.cuilian.message.Message;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.event.EventPriority;
import lvhaoxuan.custom.cuilian.object.Level;
import lvhaoxuan.custom.cuilian.object.ProtectRune;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.meta.ItemMeta;

public class ProtectRuneListener implements Listener {

    public static HashMap<String, ItemMeta> userMap = new HashMap<>();
    public static HashMap<String, ProtectRune> protectRuneMap = new HashMap<>();

    @EventHandler(priority = EventPriority.MONITOR)
    public void InventoryClickEvent(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        if (e.getRawSlot() >= 0 && e.isRightClick() && (e.getInventory().getType() != InventoryType.CRAFTING || e.getInventory().getType() != InventoryType.PLAYER)) {
            ItemStack item = e.getCurrentItem();
            ItemMeta meta = item.getItemMeta();
            if (userMap.containsKey(p.getName())) {
                if (e.getInventory().getType() != InventoryType.PLAYER && e.getInventory().getType() != InventoryType.CRAFTING) {
                    p.sendMessage(Message.MUST_ADD_IN_BAG);
                    return;
                }
                if (item.getType() == Material.AIR) {
                    cancelWithMessage(p, Message.CANCEL_ADD);
                    e.setCancelled(true);
                    return;
                }
                ProtectRune protectRune = protectRuneMap.get(p.getName());
                Level level = Level.byItemStack(item);
                int basicLevel = (level != null ? level.value : 0);
                boolean hasItem = false;
                Inventory inventory = p.getInventory();
                for (ItemStack i : inventory.getContents()) {
                    if (i != null && i.getType() == protectRune.item.getType()) {
                        hasItem = true;
                    }
                }
                if (!CuiLianAPI.canCuiLian(item) || !hasItem || !(basicLevel >= protectRune.level.value)) {
                    cancelWithMessage(p, Message.CAN_NOT_ADD);
                    e.setCancelled(true);
                    return;
                }
                e.setCurrentItem(CuiLianAPI.addProtectRune(item, protectRune));
                p.sendMessage(Component.text(Message.ADD_SUCCESS).replaceText(builder->builder.matchLiteral("%s").replacement(protectRune.lore)));
                //p.sendMessage(Message.ADD_SUCCESS.replace("%s", PlainTextComponentSerializer.plainText().serialize(protectRune.lore)));
                ItemStack clone = protectRune.item;
                clone.setAmount(1);
                int count = clone.getAmount();
                for (ItemStack invItem : inventory.getContents()) {
                    if (count <= 0) break;
                    if (invItem != null && invItem.isSimilar(clone)) {
                        int invItemAmount = invItem.getAmount();
                        if (invItemAmount > count) {
                            invItem.setAmount(invItemAmount - count);
                            count = 0;
                        }else {
                            count -= invItemAmount;
                            inventory.removeItem(invItem);
                        }
                    }
                }
                userMap.remove(p.getName());
                protectRuneMap.remove(p.getName());
                p.closeInventory();
                e.setCancelled(true);
            } else if (item.getType() != Material.AIR) {
                ProtectRune protectRune = ProtectRune.byItemStack(item);
                if (protectRune != null) {
                    ItemStack clone = protectRune.item;
                    clone.setAmount(1);
                    boolean hasItem = false;
                    Inventory inventory = p.getInventory();
                    for (ItemStack i : inventory.getContents()) {
                        if (i != null && i.getType() == protectRune.item.getType()) {
                            hasItem = true;
                        }
                    }
                    if (!hasItem) {
                        cancelWithMessage(p, Message.MUST_ADD_IN_BAG);
                        e.setCancelled(true);
                        return;
                    }
                    for (String str : Message.ADD_MESSAGE) {
                        p.sendMessage(Component.text(str).replaceText(builder->builder.matchLiteral("%s").replacement(protectRune.lore)));
                        //p.sendMessage(str.replace("%s", PlainTextComponentSerializer.plainText().serialize(protectRune.lore)));
                    }
                    userMap.put(p.getName(), meta);
                    protectRuneMap.put(p.getName(), protectRune);
                    p.closeInventory();
                    e.setCancelled(true);
                }
            }
        }
    }

    public void cancelWithMessage(Player p, String msg) {
        p.closeInventory();
        p.sendMessage(msg);
        userMap.remove(p.getName());
        protectRuneMap.remove(p.getName());
    }
}
