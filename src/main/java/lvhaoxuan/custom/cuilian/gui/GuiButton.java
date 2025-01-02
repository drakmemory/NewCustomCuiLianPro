package lvhaoxuan.custom.cuilian.gui;


import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class GuiButton {
    public int slot;
    public ItemStack item;

    public GuiButton(int slot, ItemStack item) {
        this.slot = slot;
        this.item = item;
    }

    public void click(InventoryClickEvent e) {
    }
}
