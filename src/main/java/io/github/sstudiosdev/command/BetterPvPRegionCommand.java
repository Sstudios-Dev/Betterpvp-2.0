package io.github.sstudiosdev.command;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import io.github.sstudiosdev.BetterPvP;
import io.github.sstudiosdev.util.command.BaseCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import java.util.ArrayList;
import java.util.List;

public class BetterPvPRegionCommand extends BaseCommand {
    private final BetterPvP betterPvP;

    public BetterPvPRegionCommand(final BetterPvP betterPvP) {
        super("pvpregion", new ArrayList<>(), "betterpvp.pvpregion", true);
        this.betterPvP = betterPvP;

        PluginManager pluginManager = betterPvP.getServer().getPluginManager();
        pluginManager.registerEvents(this, betterPvP);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be used by players.");
            return;
        }

        Player player = (Player) sender;
        if (args.length < 2) {
            player.sendMessage(ChatColor.RED + "Usage: /pvpregion <on|off> <region>");
            return;
        }

        String regionName = args[1];
        RegionManager regionManager = WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(player.getWorld()));
        ProtectedRegion region = regionManager.getRegion(regionName);
        if (region == null) {
            player.sendMessage(ChatColor.RED + "Region not found.");
            return;
        }

        StateFlag pvpFlag = StateFlag.valueOf("pvp");
        if (pvpFlag == null) {
            player.sendMessage(ChatColor.RED + "PvP flag not found.");
            return;
        }

        boolean pvpEnabled = args[0].equalsIgnoreCase("on");
        region.setFlag(pvpFlag, pvpEnabled);
        player.sendMessage(ChatColor.GREEN + "PvP " + (pvpEnabled ? "enabled" : "disabled") + " in region '" + regionName + "'.");
    }

    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        if (args.length == 1) {
            List<String> completions = new ArrayList<>();
            completions.add("on");
            completions.add("off");
            return completions;
        } else if (args.length == 2 && sender instanceof Player) {
            Player player = (Player) sender;
            RegionManager regionManager = WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(player.getWorld()));
            List<String> regionNames = new ArrayList<>();
            for (ProtectedRegion region : regionManager.getRegions().values()) {
                regionNames.add(region.getId());
            }
            return regionNames;
        }
        return super.tabComplete(sender, alias, args);
    }

    private WorldGuardPlugin getWorldGuardPlugin() {
        Plugin worldGuard = betterPvP.getServer().getPluginManager().getPlugin("WorldGuard");
        if (worldGuard == null || !(worldGuard instanceof WorldGuardPlugin)) {
            return null;
        }
        return (WorldGuardPlugin) worldGuard;
    }
}
