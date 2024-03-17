package io.github.sstudiosdev.command;

import io.github.sstudiosdev.BetterPvP;
import io.github.sstudiosdev.util.ChatColorUtil;
import io.github.sstudiosdev.util.command.BaseCommand;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.plugin.PluginManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PvPWorldCommand extends BaseCommand implements Listener {
    private final BetterPvP betterPvP;
    private final Map<String, Boolean> pvpWorlds = new HashMap<>();

    public PvPWorldCommand(final BetterPvP betterPvP) {
        super("pvpworld", new ArrayList<>(), "", true);
        this.betterPvP = betterPvP;

        PluginManager pluginManager = betterPvP.getServer().getPluginManager();
        pluginManager.registerEvents(this, betterPvP);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 2 && (args[0].equalsIgnoreCase("on") || args[0].equalsIgnoreCase("off"))) {
            if (hasPermission(sender)) {
                World world = betterPvP.getServer().getWorld(args[1]);
                if (world != null) {
                    pvpWorlds.put(world.getName(), args[0].equalsIgnoreCase("on"));
                    sender.sendMessage(ChatColorUtil.colorize(BetterPvP.prefix + " PvP is now " + args[0].toLowerCase() + " in world " + world.getName()));
                } else {
                    sender.sendMessage(ChatColor.RED + "World not found: " + args[1]);
                }
            } else {
                sendNoPermissionMessage(sender);
            }
        } else {
            sender.sendMessage(ChatColor.RED + "Usage: /pvpworld <on/off> <world>");
        }
    }

    private boolean hasPermission(CommandSender sender) {
        return sender.hasPermission("betterpvp.pvpworld") || sender instanceof ConsoleCommandSender || sender.isOp();
    }

    private void sendNoPermissionMessage(CommandSender sender) {
        String noPermissionMessage = betterPvP.getMainConfig().getString("no-permission");
        noPermissionMessage = noPermissionMessage.replace("%player_name%", sender.getName());
        sender.sendMessage(ChatColorUtil.colorize(BetterPvP.prefix + " " + noPermissionMessage));
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            World world = player.getWorld();
            if (pvpWorlds.containsKey(world.getName()) && !pvpWorlds.get(world.getName())) {
                event.setCancelled(true);
            }
        }
    }
}
