package commands;

import github.betterpvp.BetterPvP;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import utils.ChatUtils;

import java.io.File;

public class reload implements CommandExecutor {
    private final BetterPvP plugin;

    public reload(BetterPvP plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            if (sender.hasPermission("betterpvp.reload") || sender instanceof ConsoleCommandSender || sender.isOp()) {
                reloadConfig();
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[&3BetterPvP&8] &7» &aThe configuration file was reloaded."));
                sender.sendMessage(ChatUtils.getColoredMessage("&7(Some options only apply after the server has been restarted.)"));
            } else {
                // Obtén el mensaje desde la configuración
                String noPermissionMessage = plugin.getConfig().getString("no-permission");
                // Si no se encuentra el mensaje en la configuración, usa uno por defecto
                if (noPermissionMessage == null) {
                    noPermissionMessage = "&cSorry, but you %player_name%, do not have permission to execute this command.";
                }
                // Reemplaza "%player%" con el nombre del jugador
                noPermissionMessage = noPermissionMessage.replace("%player_name%", sender.getName());
                // Envía el mensaje de falta de permisos
                sender.sendMessage(ChatUtils.getColoredMessage(BetterPvP.prefix + " " + noPermissionMessage));
            }
            return true;
        }
        return false;
    }

    private void reloadConfig() {
        File configFile = new File(plugin.getDataFolder(), "config.yml");

        if (!configFile.exists()) {
            // Si el archivo no existe, carga la configuración por defecto
            plugin.saveDefaultConfig();
        }

        plugin.reloadConfig();
    }
}