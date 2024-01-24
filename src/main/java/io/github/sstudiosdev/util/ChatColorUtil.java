package io.github.sstudiosdev.util;

import net.md_5.bungee.api.ChatColor;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Clase utilitaria que contiene métodos para formatear y colorear mensajes de chat.
 */
public class ChatColorUtil {

    private static final Pattern HEX_COLOR_PATTERN = Pattern.compile("#[a-fA-F0-9]{6}");

    /**
     * Descolorea un mensaje eliminando los códigos de colores de Minecraft.
     *
     * @param message El mensaje a descolorear.
     * @return El mensaje descoloreado.
     */
    public static String decolorize(String message) {
        return ChatColor.stripColor(message);
    }

    /**
     * Cuenta cuántos códigos de colores hay en un mensaje.
     *
     * @param message El mensaje a analizar.
     * @return La cantidad de códigos de colores en el mensaje.
     */
    public static int countColors(String message) {
        return StringUtils.countMatches(message, String.valueOf(ChatColor.COLOR_CHAR));
    }

    /**
     * Aplica colores a una lista de mensajes.
     *
     * @param messages La lista de mensajes a colorear.
     * @return Una lista de mensajes coloreados.
     */
    public static List<String> colorizeList(List<String> messages) {
        List<String> coloredMessages = new ArrayList<>();
        messages.forEach(message -> coloredMessages.add(colorize(message)));
        return coloredMessages;
    }

    /**
     * Aplica colores a un array de mensajes.
     *
     * @param messages El array de mensajes a colorear.
     * @return Un array de mensajes coloreados.
     */
    public static String[] colorizeArray(String... messages) {
        List<String> coloredMessages = new ArrayList<>();
        for (String message : messages) {
            coloredMessages.add(colorize(message));
        }
        return coloredMessages.toArray(new String[0]);
    }

    /**
     * Aplica colores y formatos a un mensaje.
     *
     * @param message El mensaje a colorear.
     * @return El mensaje coloreado y formateado.
     */
    public static String colorize(String message) {
        // Verifica si el mensaje es nulo o vacío
        if (message.isEmpty()) {
            return message;
        }

        // Verifica si la versión del servidor es 1.18 o superior para admitir códigos de colores hexadecimales
        if (isVersion1_18OrHigher()) {
            message = replaceHexColors(message);
        }

        // Traduce los códigos de color '&' en el mensaje y devuelve el mensaje formateado
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    /**
     * Verifica si la versión del servidor es 1.18 o superior.
     *
     * @return true si la versión es 1.18 o superior, false en caso contrario.
     */
    private static boolean isVersion1_18OrHigher() {
        return Bukkit.getVersion().contains("1.18");
    }

    /**
     * Reemplaza los colores hexadecimales en un mensaje con equivalentes ChatColor.
     *
     * @param message El mensaje con colores hexadecimales.
     * @return El mensaje con colores hexadecimales reemplazados.
     */
    private static String replaceHexColors(String message) {
        Matcher matcher = HEX_COLOR_PATTERN.matcher(message);

        // StringBuilder para construir el mensaje modificado
        StringBuilder modifiedMessage = new StringBuilder();

        // Índice de inicio de la coincidencia anterior
        int lastEnd = 0;

        // Itera sobre las coincidencias de colores hexadecimales
        while (matcher.find()) {
            // Añade la parte del mensaje antes de la coincidencia de color hexadecimal
            modifiedMessage.append(message.substring(lastEnd, matcher.start()));

            // Obtiene el color hexadecimal
            String hexColor = message.substring(matcher.start(), matcher.end());

            // Traduce el color hexadecimal manualmente (ajusta según tus necesidades)
            ChatColor chatColor = translateHexColor(hexColor);

            // Añade el equivalente ChatColor al mensaje modificado
            modifiedMessage.append((chatColor != null) ? chatColor.toString() : hexColor);

            // Actualiza el índice de inicio de la próxima iteración
            lastEnd = matcher.end();
        }

        // Añade la parte restante del mensaje después de la última coincidencia de color hexadecimal
        modifiedMessage.append(message.substring(lastEnd));

        return modifiedMessage.toString();
    }

    // Método para traducir colores hexadecimales manualmente (ajústalo según tus necesidades)
    private static ChatColor translateHexColor(String hexColor) {
        // Ejemplo básico: Traducción de un color hexadecimal al color ChatColor
        // Aquí puedes implementar una lógica más avanzada si es necesario
        return ChatColor.getByChar(hexColor.charAt(1));
    }

}
