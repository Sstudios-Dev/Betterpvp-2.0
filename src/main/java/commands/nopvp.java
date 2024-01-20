package commands;

import github.betterpvp.BetterPvP;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class nopvp implements CommandExecutor {

    private final BetterPvP plugin;
    private boolean noPvPEnabled;

    public nopvp(BetterPvP plugin) {
        this.plugin = plugin;
        this.noPvPEnabled = false;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Este comando solo puede ser ejecutado por un jugador.");
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("betterpvp.nopvp")) {
            player.sendMessage("No tienes permisos para ejecutar este comando.");
            return true;
        }

        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("on")) {
                noPvPEnabled = true;
                player.sendMessage("PvP deshabilitado.");
            } else if (args[0].equalsIgnoreCase("off")) {
                noPvPEnabled = false;
                player.sendMessage("PvP habilitado.");
            } else {
                player.sendMessage("Uso incorrecto. /nopvp on|off");
            }
        } else {
            player.sendMessage("Uso incorrecto. /nopvp on|off");
        }

        return true;
    }

}
