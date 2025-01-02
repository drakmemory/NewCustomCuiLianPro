package lvhaoxuan.custom.cuilian.gui;

import lvhaoxuan.custom.cuilian.NewCustomCuiLianPro;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class Gui {
    public Player player;
    public ItemStack background;
    public GuiButton[] buttons;
    public GuiSlot[] slots;
    public Inventory inv;
    public String title;
    public int size;
    public int taskID;

    public Gui(String title, int size) {
        this.title = title;
        this.size = size;
        this.buttons = new GuiButton[size];
        this.slots = new GuiSlot[size];
        this.inv = Bukkit.createInventory(null, size, title);
    }

    public Gui(String title, int size, ItemStack background) {
        this(title, size);
        this.background = background;

        for(int i = 0; i < size; ++i) {
            this.inv.setItem(i, background);
        }

    }

    public void open(Player player) {
        this.player = player;
        player.openInventory(this.inv);
        GuiManager.guis.put(player.getName(), this);
        this.taskID = Bukkit.getScheduler().runTaskTimer(NewCustomCuiLianPro.ins, () -> this.tick(), 2L, 2L).getTaskId();
    }

    public void click(InventoryClickEvent e) {
        if (e.getInventory() != null && e.getView().getTitle().equals(this.title)) {
            int slot = e.getRawSlot();
            if (slot >= 0 && slot < this.size) {
                GuiButton button = this.buttons[slot];
                if (button != null) {
                    button.click(e);
                }

                e.setCancelled(true);
                GuiSlot guiSlot = this.slots[slot];
                if (guiSlot != null) {
                    e.setCancelled(false);
                }
            }
        }

    }

    public boolean close(InventoryCloseEvent e) {
        if (e.getInventory() != null && e.getView().getTitle().equals(this.title)) {
            for(GuiSlot slot : this.slots) {
                if (slot != null) {
                    ItemStack item = this.inv.getItem(slot.slot);
                    if (item != null && item.getType() != Material.AIR) {
                        e.getPlayer().getInventory().addItem(new ItemStack[]{item});
                        this.inv.setItem(slot.slot, (ItemStack)null);
                    }
                }
            }

            Bukkit.getScheduler().cancelTask(this.taskID);
            return true;
        } else {
            return false;
        }
    }

    public void close0() {
        for(GuiSlot slot : this.slots) {
            if (slot != null && this.player != null) {
                ItemStack item = this.inv.getItem(slot.slot);
                if (item != null && item.getType() != Material.AIR) {
                    this.player.getInventory().addItem(new ItemStack[]{item});
                    this.inv.setItem(slot.slot, (ItemStack)null);
                }
            }
        }

        Bukkit.getScheduler().cancelTask(this.taskID);
    }

    public void tick() {
    }

    public void addButton(GuiButton button) {
        this.buttons[button.slot] = button;
        this.slots[button.slot] = null;
        this.inv.setItem(button.slot, button.item);
    }

    public void addSlot(GuiSlot slot) {
        this.slots[slot.slot] = slot;
        this.buttons[slot.slot] = null;
        this.inv.setItem(slot.slot, (ItemStack)null);
    }

    public void set(int slot, ItemStack item) {
        this.inv.setItem(slot, item);
    }
}
