package io.github.sstudiosdev.command;

import io.github.sstudiosdev.BetterPvP;
import io.github.sstudiosdev.util.ChatColorUtil;
import io.github.sstudiosdev.util.command.BaseCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import java.util.List;

import java.util.ArrayList;

public class BetterPvPReloadCommand extends BaseCommand {
    private final BetterPvP betterPvP;

    public BetterPvPReloadCommand(final BetterPvP betterPvP) {
        // Set the command name and permissions
        super("betterpvp", new ArrayList<>(), "betterpvp.main", true);
        this.betterPvP = betterPvP;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        // Verificar si se proporcionó el argumento "reload"
        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            // Verify player permissions
            if (hasPermission(sender)) {
                // Reload configuration
                betterPvP.getMainConfig().load();

                // Enviar mensajes de éxito
                sender.sendMessage(ChatColorUtil.colorize(BetterPvP.prefix + " &aThe configuration file was reloaded."));
                sender.sendMessage(ChatColorUtil.colorize("&7(Some options only apply after the server has been restarted.)"));
            } else {
                // Send message of lack of permissions
                sendNoPermissionMessage(sender);
            }
        } else if (args.length == 1 && args[0].equalsIgnoreCase("help")) {
            sender.sendMessage(ChatColorUtil.colorize("&3====== BetterPvP Help ======"));
            sender.sendMessage(ChatColorUtil.colorize("&3/betterpvp reload &7- Reload the configuration file"));
            sender.sendMessage(ChatColorUtil.colorize("&f"));
            sender.sendMessage(ChatColorUtil.colorize("&3/betterpvp help &7- Show this help message"));
            sender.sendMessage(ChatColorUtil.colorize("&f"));
            sender.sendMessage(ChatColorUtil.colorize("&3/pvp <on/off> &7- activates and deactivates player pvp"));
            sender.sendMessage(ChatColorUtil.colorize("&f"));
            sender.sendMessage(ChatColorUtil.colorize("&3/pvpworld <on/off> <world> &7- disable and enable global pvp for all players in that world"));
        } else {
            // Incorrect use message
            sender.sendMessage(ChatColorUtil.colorize(BetterPvP.prefix + " &cUsage: /betterpvp <command>"));
        }
    }

    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            completions.add("reload");
            completions.add("help");
        }
        return completions;
    }

    /**
     * Verify if the player has the necessary permissions.
     *
     * @param sender The sender of the command.
     * @return True if the player has permissions, false otherwise.
     */
    private boolean hasPermission(CommandSender sender) {
        return sender.hasPermission("betterpvp.main") || sender instanceof ConsoleCommandSender || sender.isOp();
    }

    /**
     * Sends the lack of permissions message to the player.
     *
     * @param sender The sender of the command.
     */
    private void sendNoPermissionMessage(CommandSender sender) {
        // Get the message from the configuration, if it does not exist, use a default one.
        String noPermissionMessage = betterPvP.getMainConfig().getString("no-permission");
        noPermissionMessage = noPermissionMessage.replace("%player_name%", sender.getName());
        sender.sendMessage(ChatColorUtil.colorize(BetterPvP.prefix + " " + noPermissionMessage));
    }
}
