package io.github.sstudiosdev.command;

import io.github.sstudiosdev.BetterPvP;
import io.github.sstudiosdev.util.ChatColorUtil;
import io.github.sstudiosdev.util.command.BaseCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BtrMain extends BaseCommand {
    private final BetterPvP betterPvP;

    public BtrMain(final BetterPvP betterPvP) {
        // Set the command name and permissions
        super("btr", new ArrayList<>(), "betterpvp.main", true);
        this.betterPvP = betterPvP;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 2 && args[0].equalsIgnoreCase("locate")) {
            String language = args[1].toLowerCase();
            String link = "";

            switch (language) {
                case "es":
                    link = "https://github.com/Sstudios-Dev/Betterpvp-languages/blob/main/betterpvp/languages/config-Es.yml";
                    break;
                case "eng":
                    link = "https://github.com/Sstudios-Dev/Betterpvp-languages/blob/main/betterpvp/languages/config-Eng.yml";
                    break;
                case "ca":
                    link = "https://github.com/Sstudios-Dev/Betterpvp-languages/blob/main/betterpvp/languages/config-Ca.yml";
                    break;
                case "jp":
                    link = "https://github.com/Sstudios-Dev/Betterpvp-languages/blob/main/betterpvp/languages/config-Jp.yml";
                    break;
                case "ru":
                    link = "https://github.com/Sstudios-Dev/Betterpvp-languages/blob/main/betterpvp/languages/config-Ru.yml";
                    break;
                default:
                    String InvalidLenguaje = betterPvP.getMainConfig().getString("invalid-language");
                    sender.sendMessage(ChatColorUtil.colorize(BetterPvP.prefix + " " + InvalidLenguaje));
                    return;
            }
            String GithubRepository = betterPvP.getMainConfig().getString("github-repository");
            GithubRepository = GithubRepository.replace("%link%", link);
            sender.sendMessage(ChatColorUtil.colorize(BetterPvP.prefix + " " + GithubRepository));
        } else {
            sender.sendMessage(ChatColorUtil.colorize(BetterPvP.prefix + " &cUsage: /btr locate <language_code>"));
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        List<String> completions = new ArrayList<>();
        if (args.length == 1 && "locate".startsWith(args[0])) {
            completions.add("locate");
        } else if (args.length == 2 && args[0].equalsIgnoreCase("locate")) {
            completions.addAll(Arrays.asList("es", "eng", "ca", "jp", "ru"));
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
