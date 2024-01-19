package commands;

import github.betterpvp.BetterPvP;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class nopvp implements CommandExecutor {

    private final BetterPvP plugin;

    public nopvp(BetterPvP plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("nopvp")) {
            if (args.length == 0) {
                sender.sendMessage("PvP está " + (plugin.isPvpEnabled() ? "activado" : "desactivado"));
                return true;
            } else if (args.length == 1) {
                if (args[0].equalsIgnoreCase("on")) {
                    plugin.setPvpEnabled(true);
                    sender.sendMessage("PvP activado.");
                } else if (args[0].equalsIgnoreCase("off")) {
                    plugin.setPvpEnabled(false);
                    sender.sendMessage("PvP desactivado.");
                } else {
                    sender.sendMessage("Uso incorrecto. /nopvp [on/off]");
                }
                return true;
            }
        }
        return false;
    }
}
