package io.github.sstudiosdev.command;

import io.github.sstudiosdev.BetterPvP;
import io.github.sstudiosdev.util.ChatColorUtil;
import io.github.sstudiosdev.util.command.BaseCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

import java.util.ArrayList;

public class BetterPvPNoPvPCommand extends BaseCommand {
    private final BetterPvP betterPvP;

    public BetterPvPNoPvPCommand(final BetterPvP betterPvP) {
        // Establecer el nombre del comando y sus permisos
        super("nopvp", new ArrayList<>(), "betterpvp.nopvp", true);
        this.betterPvP = betterPvP;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        // Verificar si se proporcionó el argumento "on" o "off"
        if (args.length == 1 && (args[0].equalsIgnoreCase("on") || args[0].equalsIgnoreCase("off"))) {
            // Verificar permisos del jugador
            if (hasPermission(sender)) {
                // Enviar mensaje de éxito
                sender.sendMessage(ChatColorUtil.colorize(BetterPvP.prefix + " &aPvP is now " + args[0] + "."));
            } else {
                // Enviar mensaje de falta de permisos
                sendNoPermissionMessage(sender);
            }
        } else {
            // Mensaje de uso incorrecto
            sender.sendMessage(ChatColor.RED + "Usage: /nopvp <on/off>");
        }
    }

    /**
     * Verifica si el jugador tiene los permisos necesarios.
     *
     * @param sender El remitente del comando.
     * @return True si el jugador tiene permisos, false de lo contrario.
     */
    private boolean hasPermission(CommandSender sender) {
        return sender.hasPermission("betterpvp.nopvp") || sender instanceof ConsoleCommandSender || sender.isOp();
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