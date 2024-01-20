/**
 * Utility class for handling and formatting chat messages in the BetterPvP plugin.
 * Provides methods for coloring and formatting text for display in Minecraft chat.
 */
package utils;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class containing methods for formatting and coloring chat messages.
 */
public class ChatUtils {

    /**
     * Formats a message by translating color codes and supporting hexadecimal color codes for Minecraft chat.
     * @param message The message to be formatted.
     * @return The formatted message with color codes applied.
     */
    public static String getColoredMessage(String message) {
        // Check if the message is null or empty
        if (message == null || message.isEmpty()) {
            return "";
        }

        // Check if the server version is 1.18 or higher to support hexadecimal color codes
        if (Bukkit.getVersion().contains("1.16")) {
            // Define a pattern for matching hexadecimal color codes
            Pattern pattern = Pattern.compile("#[a-fA-F0-9]{6}");
            Matcher matcher = pattern.matcher(message);

            // Iterate through matches and replace them with ChatColor equivalents
            while (matcher.find()) {
                String color = message.substring(matcher.start(), matcher.end());
                ChatColor chatColor = ChatColor.of(color);

                // Check if ChatColor is valid, then replace the color code in the message
                if (chatColor != null) {
                    message = message.replace(color, chatColor.toString());
                }

                // Re-run the matcher to handle multiple occurrences in the message
                matcher = pattern.matcher(message);
            }
        }

        // Translate '&' color codes in the message and return the formatted message
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}
