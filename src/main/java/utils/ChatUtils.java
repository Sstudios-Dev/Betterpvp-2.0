package utils;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatUtils {

    public static String getColoredMessage(String message) {
        if (message == null || message.isEmpty()) {
            return "";
        }

        if (Bukkit.getVersion().contains("1.18")) {
            Pattern pattern = Pattern.compile("#[a-fA-F0-9]{6}");
            Matcher matcher = pattern.matcher(message);

            while (matcher.find()) {
                String color = message.substring(matcher.start(), matcher.end());
                ChatColor chatColor = ChatColor.of(color);

                if (chatColor != null) {
                    message = message.replace(color, chatColor.toString());
                }

                matcher = pattern.matcher(message);
            }
        }

        return ChatColor.translateAlternateColorCodes('&', message);
    }
}
