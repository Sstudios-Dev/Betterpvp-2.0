package manager;

import github.betterpvp.BetterPvP;
import org.bukkit.configuration.file.FileConfiguration;

public class ConfigManager {

    private static FileConfiguration config;

    public static void setupConfig(BetterPvP betterPvP){
        ConfigManager.config = betterPvP.getConfig();
        betterPvP.saveDefaultConfig();
    }
}