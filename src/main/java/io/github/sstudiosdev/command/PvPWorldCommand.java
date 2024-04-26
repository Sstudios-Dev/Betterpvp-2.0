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
    private final List<String> enabledWorlds;

    public PvPWorldCommand(final BetterPvP betterPvP) {
        super("pvpworld", new ArrayList<>(), "betterpvp.pvpworld", true);
        this.betterPvP = betterPvP;
        this.enabledWorlds = betterPvP.getMainConfig().getStringList("pvpworld-disabled-worlds");

        PluginManager pluginManager = betterPvP.getServer().getPluginManager();
        pluginManager.registerEvents(this, betterPvP);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 2 && (args[0].equalsIgnoreCase("on") || args[0].equalsIgnoreCase("off"))) {
            if (hasPermission(sender)) {
                World world = betterPvP.getServer().getWorld(args[1]);
                if (world != null) {
                    if (!enabledWorlds.contains(world.getName())) {
                        pvpWorlds.put(world.getName(), args[0].equalsIgnoreCase("on"));
                        String message = args[0].equalsIgnoreCase("on") ?
                                betterPvP.getMainConfig().getString("pvpworld-enabled") :
                                betterPvP.getMainConfig().getString("pvpworld-disabled");
                        message = message.replace("%bt-world%", world.getName());
                        sender.sendMessage(ChatColorUtil.colorize(BetterPvP.prefix + " " + message));
                    } else {
                        String CommandDisabledWorld = betterPvP.getMainConfig().getString("command-disabled-world");
                        sender.sendMessage(ChatColorUtil.colorize(BetterPvP.prefix + " " + CommandDisabledWorld));
                    }
                } else {
                    sendWorldNoFound(sender, args[1]);
                }
            } else {
                sendNoPermissionMessage(sender);
            }
        } else {
            sender.sendMessage(ChatColorUtil.colorize(BetterPvP.prefix + " Usage: /pvpworld <on/off> <world>"));
        }
    }

    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            completions.add("on");
            completions.add("off");
        } else if (args.length == 2) {
            for (World world : betterPvP.getServer().getWorlds()) {
                completions.add(world.getName());
            }
        }
        return completions;
    }

    private boolean hasPermission(CommandSender sender) {
        return sender.hasPermission("betterpvp.pvpworld") || sender instanceof ConsoleCommandSender || sender.isOp();
    }

    private void sendNoPermissionMessage(CommandSender sender) {
        String noPermissionMessage = betterPvP.getMainConfig().getString("no-permission");
        noPermissionMessage = noPermissionMessage.replace("%player_name%", sender.getName());
        sender.sendMessage(ChatColorUtil.colorize(BetterPvP.prefix + " " + noPermissionMessage));
    }

    private void sendWorldNoFound(CommandSender sender, String worldName) {
        String worldNotFound = betterPvP.getMainConfig().getString("world-not-found");
        worldNotFound = worldNotFound.replace("%bt-world%", worldName);
        sender.sendMessage(ChatColorUtil.colorize(BetterPvP.prefix + " " + worldNotFound));
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