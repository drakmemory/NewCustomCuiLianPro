package lvhaoxuan.custom.cuilian.gui;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class PageGui extends Gui {
    public List<Object> objs = new ArrayList();
    public lvhaoxuan.custom.cuilian.gui.GuiButton before;
    public lvhaoxuan.custom.cuilian.gui.GuiButton next;
    public int head;
    public int tail;
    public int page;

    public PageGui(String title, int size, List objs, lvhaoxuan.custom.cuilian.gui.GuiButton before, lvhaoxuan.custom.cuilian.gui.GuiButton next, int head, int tail) {
        super(title, size);
        this.objs = objs;
        this.before = before;
        this.next = next;
        this.head = head;
        this.tail = tail;
    }

    private void open(final Player player, final int page) {
        if (page >= 0) {
            if (Math.ceil((double)this.objs.size() * (double)1.0F / (double)(this.tail - this.head)) <= (double)page) {
                this.open(player, page - 1);
            } else {
                this.player = player;
                this.page = page;
                this.inv.clear();

                for(lvhaoxuan.custom.cuilian.gui.GuiButton button : this.buttons) {
                    if (button != null) {
                        this.inv.setItem(button.slot, button.item);
                    }
                }

                for(GuiSlot slot : this.slots) {
                    if (slot != null) {
                        this.inv.setItem(slot.slot, (ItemStack)null);
                    }
                }

                for(int i = this.head; i < this.tail; ++i) {
                    if (this.objs.size() > page * (this.tail - this.head) + i) {
                        this.inv.setItem(i, this.handleItem(this.objs.get(page * (this.tail - this.head) + i)));
                    }
                }

                this.before = new lvhaoxuan.custom.cuilian.gui.GuiButton(this.before.slot, this.before.item) {
                    public void click(InventoryClickEvent e) {
                        lvhaoxuan.custom.cuilian.gui.PageGui.this.open(player, page - 1);
                    }
                };
                this.next = new GuiButton(this.next.slot, this.next.item) {
                    public void click(InventoryClickEvent e) {
                        lvhaoxuan.custom.cuilian.gui.PageGui.this.open(player, page + 1);
                    }
                };
                this.addButton(this.before);
                this.addButton(this.next);
                player.openInventory(this.inv);
                GuiManager.guis.put(player.getName(), this);
            }
        }
    }

    public void click(InventoryClickEvent e) {
        super.click(e);
        if (e.getInventory() != null && e.getView().getTitle().equals(this.title)) {
            int slot = e.getRawSlot();
            if (slot >= this.head && slot < this.tail) {
                ItemStack item = e.getInventory().getItem(slot);
                if (item != null) {
                    Object obj = this.objs.get(this.page * (this.tail - this.head) + slot);
                    this.onClick(obj, e);
                }
            }
        }

    }

    public void onClick(Object obj, InventoryClickEvent e) {
    }

    public void open(Player player) {
        this.open(player, 0);
    }

    public ItemStack handleItem(Object obj) {
        return null;
    }
}
