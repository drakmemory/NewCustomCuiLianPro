package lvhaoxuan.custom.cuilian.commander;

import java.util.Map;
import lvhaoxuan.custom.cuilian.NewCustomCuiLianPro;
import lvhaoxuan.custom.cuilian.api.CuiLianAPI;
import lvhaoxuan.custom.cuilian.movelevel.MoveLevelHandle;
import lvhaoxuan.custom.cuilian.object.Level;
import lvhaoxuan.custom.cuilian.object.Stone;
import lvhaoxuan.custom.cuilian.runnable.ScriptRunnable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.command.CommandSender;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Zombie;
import org.jetbrains.annotations.NotNull;

public class Commander implements CommandExecutor {

    public static String head = "§c[§6" + NewCustomCuiLianPro.ins.getName() + NewCustomCuiLianPro.ins.getDescription().getVersion() + "§c]§e";

    @Override
    public boolean onCommand(@NotNull CommandSender sender, org.bukkit.command.@NotNull Command command, @NotNull String label, String[] args) {
        if (args.length == 1 && args[0].equalsIgnoreCase("moveLevel")) {
            if (sender instanceof Player) {
                MoveLevelHandle.open((Player) sender);
            }
        } else {
            if (!sender.isOp()) {
                sender.sendMessage(head + "不是OP");
            } else if (args.length == 0) {
                sender.sendMessage("§c§m§l  §6§m§l  §e§m§l  §a§m§l  §b§m§l  §e§l淬炼§b§m§l  §a§m§l  §e§m§l  §6§m§l  §c§m§l  ");
                sender.sendMessage("§c§l▏   §c/cuilian set [等级]");
                sender.sendMessage("§c§l▏   §c/cuilian moveLevel");
                if (sender instanceof Player) {
                    Component tc = Component.text("§c§l▏   §c/cuilian stone [淬炼石ID] [数量] [玩家]")
                            .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/cuilian stones"))
                            .hoverEvent(HoverEvent.hoverEvent(HoverEvent.Action.SHOW_TEXT, Component.text("§c查看所有淬炼石")));
                    sender.sendMessage(tc);
                } else {
                    sender.sendMessage("§c§l▏   §c/cuilian stone [淬炼石ID] [数量] [玩家]");
                }
                if (sender instanceof Player) {
                    Component tc = Component.text("§c§l▏   §c/cuilian protect [保护符等级] [数量] [玩家]")
                            .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/cuilian protects"))
                                    .hoverEvent(HoverEvent.hoverEvent(HoverEvent.Action.SHOW_TEXT,Component.text("§c查看所有保护符")));
                    sender.sendMessage(tc);
                } else {
                    sender.sendMessage("§c§l▏   §c/cuilian protect [保护符等级] [数量] [玩家]");
                }
                sender.sendMessage("§c§l▏   §c/cuilian reload");
                sender.sendMessage("§c§m§l  §6§m§l  §e§m§l  §a§m§l  §b§m§l  §e§l淬炼§b§m§l  §a§m§l  §e§m§l  §6§m§l  §c§m§l  ");
            } else if (args.length == 1 && args[0].equals("stones")) {
                sender.sendMessage("§c§m§l  §6§m§l  §e§m§l  §a§m§l  §b§m§l  §e§l淬炼§b§m§l  §a§m§l  §e§m§l  §6§m§l  §c§m§l  ");
                for (Map.Entry<String, Stone> entry : Stone.stones.entrySet()) {
                    if (sender instanceof Player) {
                        Component tc = Component.text(entry.getKey() + " - " + entry.getValue().item.getItemMeta().displayName())
                                        .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/cuilian stone " + entry.getKey() + " 1 " + sender.getName()))
                                                .hoverEvent(HoverEvent.hoverEvent(HoverEvent.Action.SHOW_TEXT, Component.text("§c获取淬炼石")));
                        sender.sendMessage(tc);
                    } else {
                        sender.sendMessage(entry.getKey() + " - " + entry.getValue().item.getItemMeta().displayName());
                    }
                }
                sender.sendMessage("§c§m§l  §6§m§l  §e§m§l  §a§m§l  §b§m§l  §e§l淬炼§b§m§l  §a§m§l  §e§m§l  §6§m§l  §c§m§l  ");
            } else if (args.length == 1 && args[0].equals("protects")) {
                sender.sendMessage("§c§m§l  §6§m§l  §e§m§l  §a§m§l  §b§m§l  §e§l淬炼§b§m§l  §a§m§l  §e§m§l  §6§m§l  §c§m§l  ");
                for (Map.Entry<Integer, Level> entry : Level.levels.entrySet()) {
                    if (entry.getValue().protectRune != null) {
                        if (sender instanceof Player) {
                            Component tc = Component.text(entry.getKey() + " - " + entry.getValue().protectRune.item.getItemMeta().displayName())
                                            .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/cuilian protect " + entry.getKey() + " 1 " + sender.getName()))
                                                    .hoverEvent(HoverEvent.hoverEvent(HoverEvent.Action.SHOW_TEXT, Component.text("§c获取保护符")));
                            sender.sendMessage(tc);
                        } else {
                            sender.sendMessage(entry.getKey() + " - " + entry.getValue().protectRune.item.getItemMeta().displayName());
                        }
                    }
                }
                sender.sendMessage("§c§m§l  §6§m§l  §e§m§l  §a§m§l  §b§m§l  §e§l淬炼§b§m§l  §a§m§l  §e§m§l  §6§m§l  §c§m§l  ");
            } else if (args.length == 1 && args[0].equals("reload")) {
                ScriptRunnable.enbaleScript = false;
                NewCustomCuiLianPro.enableConfig();
                ScriptRunnable.enbaleScript = true;
                sender.sendMessage(head + "重载成功");
            } else if (args.length == 1 && args[0].equals("test")) {
                Player p = (Player) sender;
                Zombie zombie = (Zombie) p.getWorld().spawnEntity(p.getLocation(), EntityType.ZOMBIE);
                ItemStack item = new ItemStack(Material.DIAMOND_SWORD);
                item = CuiLianAPI.setItemLevel(item, Level.levels.get(18));
                zombie.getEquipment().setItemInMainHand(item);
                zombie.getEquipment().setItemInOffHand(item);
                item = new ItemStack(Material.DIAMOND_HELMET);
                item = CuiLianAPI.setItemLevel(item, Level.levels.get(18));
                zombie.getEquipment().setHelmet(item);
                item = new ItemStack(Material.DIAMOND_CHESTPLATE);
                item = CuiLianAPI.setItemLevel(item, Level.levels.get(18));
                zombie.getEquipment().setChestplate(item);
                item = new ItemStack(Material.DIAMOND_LEGGINGS);
                item = CuiLianAPI.setItemLevel(item, Level.levels.get(18));
                zombie.getEquipment().setLeggings(item);
                item = new ItemStack(Material.DIAMOND_BOOTS);
                item = CuiLianAPI.setItemLevel(item, Level.levels.get(18));
                zombie.getEquipment().setBoots(item);
            } else if (args.length == 2 && args[0].equals("set")) {
                Player p = (Player) sender;
                p.getEquipment().setItemInMainHand(CuiLianAPI.setItemLevel(p.getEquipment().getItemInMainHand(), Level.levels.get(Integer.parseInt(args[1]))));
            } else if (args.length == 4 && args[0].equals("stone")) {
                Player p = Bukkit.getPlayer(args[3]);
                if (p != null) {
                    Stone stone = Stone.stones.get(args[1]);
                    int amount = Integer.parseInt(args[2]);
                    if (stone != null) {
                        ItemStack item = stone.item;
                        item.setAmount(amount);
                        p.getInventory().addItem(item);
                        sender.sendMessage(head + "获取成功");
                    } else {
                        sender.sendMessage(head + "不存在的淬炼石");
                    }
                } else {
                    sender.sendMessage(head + "§c玩家不在线");
                }
            } else if (args.length == 4 && args[0].equals("protect")) {
                Player p = Bukkit.getPlayer(args[3]);
                if (p != null) {
                    Level level = Level.levels.get(Integer.parseInt(args[1]));
                    int amount = Integer.parseInt(args[2]);
                    if (level != null && level.protectRune != null) {
                        ItemStack item = level.protectRune.item;
                        item.setAmount(amount);
                        p.getInventory().addItem(item);
                        sender.sendMessage(head + "获取成功");
                    } else {
                        sender.sendMessage(head + "不存在的保护符");
                    }
                } else {
                    sender.sendMessage(head + "§c玩家不在线");
                }
            }
        }
        return true;
    }
}
