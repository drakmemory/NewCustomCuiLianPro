package lvhaoxuan.custom.cuilian.gui;

import java.util.HashMap;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class GuiManager implements Listener {
    public static HashMap<String, Gui> guis = new HashMap();

    public GuiManager() {
    }

    @EventHandler
    public void InventoryClickEvent(InventoryClickEvent e) {
        Player player = (Player)e.getWhoClicked();
        String name = player.getName();
        if (guis.containsKey(name)) {
            Gui gui = guis.get(name);
            gui.click(e);
        }

    }

    @EventHandler
    public void InventoryCloseEvent(InventoryCloseEvent e) {
        Player player = (Player)e.getPlayer();
        String name = player.getName();
        if (guis.containsKey(name)) {
            Gui gui = guis.get(name);
            if (gui.close(e)) {
                guis.remove(name);
            }
        }

    }
}
