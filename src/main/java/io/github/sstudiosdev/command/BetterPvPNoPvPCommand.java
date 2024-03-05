package io.github.sstudiosdev.command;

import io.github.sstudiosdev.BetterPvP;
import io.github.sstudiosdev.util.ChatColorUtil;
import io.github.sstudiosdev.util.command.BaseCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.plugin.PluginManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BetterPvPNoPvPCommand extends BaseCommand implements Listener {
    private final BetterPvP betterPvP;
    private final Map<Player, Long> cooldowns = new HashMap<>();

    private boolean pvpEnabled = true; // Estado predeterminado: PvP habilitado

    public BetterPvPNoPvPCommand(final BetterPvP betterPvP) {
        // Establecer el nombre del comando y sus permisos
        super("nopvp", new ArrayList<>(), "betterpvp.nopvp", true);
        this.betterPvP = betterPvP;

        // Registrar el evento para manejar el daño
        PluginManager pluginManager = betterPvP.getServer().getPluginManager();
        pluginManager.registerEvents(this, betterPvP);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        // Verificar si se proporcionó el argumento "on" o "off"
        if (args.length == 1 && (args[0].equalsIgnoreCase("on") || args[0].equalsIgnoreCase("off"))) {
            // Verificar permisos del jugador
            if (hasPermission(sender)) {
                // Verificar cooldown solo si se activa el PvP
                if (args[0].equalsIgnoreCase("on") && sender instanceof Player) {
                    Player player = (Player) sender;
                    long currentTime = System.currentTimeMillis();
                    long defaultCooldown = 60L; // Valor predeterminado en segundos
                    long cooldownTime = betterPvP.getMainConfig().getLong("cooldown.nopvp_cooldown", defaultCooldown) * 1000;
                    if (cooldowns.containsKey(player) && cooldowns.get(player) + cooldownTime > currentTime) {
                        String CooldownError = betterPvP.getMainConfig().getString("cooldown-error-message");
                        sender.sendMessage(ChatColorUtil.colorize(BetterPvP.prefix + " " + CooldownError));
                        return;
                    }
                    cooldowns.put(player, currentTime);
                }

                // Obtener el mensaje desde la configuración
                String pvpToggleMessage = betterPvP.getMainConfig().getString("pvptoggle");

                // Reemplazar "%status%" con el estado proporcionado en el comando
                pvpToggleMessage = pvpToggleMessage.replace("%status%", args[0]);

                // Actualizar el estado del PvP
                pvpEnabled = args[0].equalsIgnoreCase("on");

                // Enviar el mensaje de éxito
                sender.sendMessage(ChatColorUtil.colorize(BetterPvP.prefix + " " + pvpToggleMessage));
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

    // Manejar el evento de daño
    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            // Verificar si el PvP está desactivado
            if (!pvpEnabled && event.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
                // Cancelar el evento si el PvP está desactivado y la causa del daño es un ataque
                event.setCancelled(true);
            }
        }
    }
}
