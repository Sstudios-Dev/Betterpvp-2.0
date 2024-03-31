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
        // Establecer el nombre del comando y sus permisos
        super("betterpvp", new ArrayList<>(), "betterpvp.main", true);
        this.betterPvP = betterPvP;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        // Verificar si se proporcionó el argumento "reload"
        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            // Verificar permisos del jugador
            if (hasPermission(sender)) {
                // Recargar la configuración
                betterPvP.getMainConfig().load();

                // Enviar mensajes de éxito
                sender.sendMessage(ChatColorUtil.colorize(BetterPvP.prefix + " &aThe configuration file was reloaded."));
                sender.sendMessage(ChatColorUtil.colorize("&7(Some options only apply after the server has been restarted.)"));
            } else {
                // Enviar mensaje de falta de permisos
                sendNoPermissionMessage(sender);
            }
        } else if (args.length == 1 && args[0].equalsIgnoreCase("help")) {
            sender.sendMessage(ChatColorUtil.colorize("&6====== BetterPvP Help ======"));
            sender.sendMessage(ChatColorUtil.colorize("&6/betterpvp reload &7- Reload the configuration file"));
            sender.sendMessage(ChatColorUtil.colorize("&6/betterpvp help &7- Show this help message"));
        } else {
            // Mensaje de uso incorrecto
            sender.sendMessage(ChatColor.RED + "Usage: /betterpvp <command>");
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
     * Verifica si el jugador tiene los permisos necesarios.
     *
     * @param sender El remitente del comando.
     * @return True si el jugador tiene permisos, false de lo contrario.
     */
    private boolean hasPermission(CommandSender sender) {
        return sender.hasPermission("betterpvp.main") || sender instanceof ConsoleCommandSender || sender.isOp();
    }

    /**
     * Envia el mensaje de falta de permisos al jugador.
     *
     * @param sender El remitente del comando.
     */
    private void sendNoPermissionMessage(CommandSender sender) {
        // Obtener el mensaje desde la configuración, si no existe, usar uno por defecto
        String noPermissionMessage = betterPvP.getMainConfig().getString("no-permission");
        // Reemplazar "%player_name%" con el nombre del jugador
        noPermissionMessage = noPermissionMessage.replace("%player_name%", sender.getName());
        // Enviar el mensaje de falta de permisos
        sender.sendMessage(ChatColorUtil.colorize(BetterPvP.prefix + " " + noPermissionMessage));
    }
}
