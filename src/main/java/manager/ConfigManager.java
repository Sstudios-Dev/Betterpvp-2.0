/**
 * Class responsible for managing the configuration of the BetterPvP plugin.
 * This class handles the initialization and provides access to the plugin's configuration.
 */
package manager;

import github.betterpvp.BetterPvP;
import org.bukkit.configuration.file.FileConfiguration;

/**
 * Class providing methods to configure and access the plugin's configuration.
 */
public class ConfigManager {

    // Plugin configuration stored in a FileConfiguration object
    private static FileConfiguration config;

    /**
     * Configures the FileConfiguration instance for the BetterPvP plugin.
     * @param betterPvP Instance of the BetterPvP plugin.
     */
    public static void setupConfig(BetterPvP betterPvP) {
        // Set the plugin configuration with the default configuration if it does not exist
        ConfigManager.config = betterPvP.getConfig();
        betterPvP.saveDefaultConfig();
    }
}
