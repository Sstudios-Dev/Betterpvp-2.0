package io.github.sstudiosdev.util.command;

import lombok.Setter;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public abstract class BaseCommand extends BukkitCommand {

    private final boolean forPlayersOnly;
    @Setter
    private boolean executeAsync;

    /**
     * Constructor para BaseCommand.
     *
     * @param name           El nombre del comando.
     * @param aliases        Lista de alias del comando.
     * @param permission     Permiso requerido para ejecutar el comando.
     * @param forPlayersOnly Indica si el comando es solo para jugadores.
     */
    public BaseCommand(String name, List<String> aliases, String permission, boolean forPlayersOnly) {
        super(name);

        this.setAliases(aliases);
        this.setPermission(permission);
        this.forPlayersOnly = forPlayersOnly;
    }

    /**
     * Verifica si el remitente es un ConsoleCommandSender.
     *
     * @param sender El remitente del comando.
     * @return Verdadero si el remitente no es un ConsoleCommandSender, falso de lo contrario.
     */
    protected boolean checkConsoleSender(CommandSender sender) {
        if (sender instanceof ConsoleCommandSender) {
            sender.sendMessage(ChatColor.RED + "Players only");
            return false;
        }
        return true;
    }

    /**
     * Método principal para ejecutar el comando. Se encarga de realizar la ejecución de forma asíncrona si está habilitado.
     *
     * @param sender El remitente del comando.
     * @param label  La etiqueta del comando.
     * @param args   Los argumentos del comando.
     * @return Verdadero si la ejecución fue exitosa, falso de lo contrario.
     */
    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (this.forPlayersOnly && sender instanceof ConsoleCommandSender) {
            sender.sendMessage(ChatColor.RED + "Players only!");
            return true;
        }

        if (this.getPermission() != null && !sender.hasPermission(this.getPermission())) {
            sender.sendMessage(ChatColor.RED + "You don't have permissions!");
            return true;
        }

        if (this.executeAsync) {
            final JavaPlugin plugin = JavaPlugin.getProvidingPlugin(getClass());
            plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> this.execute(sender, args));
        } else {
            this.execute(sender, args);
        }

        return true;
    }

    /**
     * Método abstracto para ejecutar el comando.
     *
     * @param sender El remitente del comando.
     * @param args   Los argumentos del comando.
     */
    public abstract void execute(CommandSender sender, String[] args);

    /**
     * Verifica si el jugador está en línea.
     *
     * @param sender     El remitente del comando.
     * @param player     El jugador.
     * @param playerName El nombre del jugador.
     * @return Verdadero si el jugador está en línea, falso de lo contrario.
     */
    protected boolean checkPlayer(CommandSender sender, Player player, String playerName) {
        if (player == null) {
            sender.sendMessage(ChatColor.RED + "The player is not online!");
            return false;
        }
        return true;
    }

    /**
     * Verifica si una cadena es un número válido.
     *
     * @param sender El remitente del comando.
     * @param number La cadena que se va a comprobar.
     * @return Verdadero si la cadena es un número válido, falso de lo contrario.
     */
    protected boolean checkNumber(CommandSender sender, String number) {
        if (!NumberUtils.isNumber(number)) {
            sender.sendMessage(ChatColor.RED + "It is an invalid number.");
            return false;
        }
        return true;
    }

    /**
     * Verifica si el remitente tiene un permiso específico.
     *
     * @param sender     El remitente del comando.
     * @param permission El permiso requerido.
     * @return Verdadero si el remitente tiene el permiso, falso de lo contrario.
     */
    protected boolean checkPermission(CommandSender sender, String permission) {
        if (!sender.hasPermission(permission)) {
            sender.sendMessage(ChatColor.RED + "You don't have permissions!");
            return false;
        }
        return true;
    }
}
