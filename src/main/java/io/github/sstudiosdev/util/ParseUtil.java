package io.github.sstudiosdev.util;

import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

@UtilityClass
public class ParseUtil {

    /**
     * Intenta analizar gramaticalmente un entero.
     * @param integerValue La cadena que se intentaranalizar.
     * @return El entero analizado o null si no se puede analizar.
     */
    public Integer tryParseInt(final String integerValue) {
        try {
            return Integer.parseInt(integerValue);
        } catch (final IllegalArgumentException e) {
            return null;
        }
    }

    /**
     * Intenta analizar gramaticalmente un double.
     * @param doubleValue La cadena que se intentaranalizar.
     * @return El double analizado o null si no se puede analizar.
     */
    public Double tryParseDouble(final String doubleValue) {
        try {
            return Double.parseDouble(doubleValue);
        } catch (final IllegalArgumentException e) {
            return null;
        }
    }

    /**
     * Intenta analizar gramaticalmente un float.
     * @param floatValue La cadena que se intentaranalizar.
     * @return El float analizado o null si no se puede analizar.
     */
    public Float tryParseFloat(String floatValue) {
        try {
            return Float.parseFloat(floatValue);
        } catch (final IllegalArgumentException e) {
            return null;
        }
    }

    /**
     * Intenta analizar gramaticalmente un long.
     * @param longValue La cadena que se intentaranalizar.
     * @return El long analizado o null si no se puede analizar.
     */
    public Long tryParseLong(final String longValue) {
        try {
            return Long.parseLong(longValue);
        } catch (final IllegalArgumentException e) {
            return null;
        }
    }

    /**
     * Intenta analizar gramaticalmente una ubicaci
     * @param locationValue La cadena que contiene la informacide la ubicaci
     * @return La ubicacianalizada o null si no se puede analizar.
     */
    public Location tryParseLocation(final String locationValue) {
        try {
            final String[] pathSplit = locationValue.split(",");

            final World world = Bukkit.getWorld(pathSplit[0]);
            if (world == null) {
                throw new NullPointerException("World is null!");
            }

            final Double blockX = tryParseDouble(pathSplit[1]);
            if (blockX == null) {
                throw new NullPointerException("Block X is null!");
            }

            final Double blockY = tryParseDouble(pathSplit[2]);
            if (blockY == null) {
                throw new NullPointerException("Block Y is null!");
            }

            final Double blockZ = tryParseDouble(pathSplit[3]);
            if (blockZ == null) {
                throw new NullPointerException("Block Z is null!");
            }

            return new Location(world, blockX, blockY, blockZ);
        } catch (final Exception e) {
            // Si ocurre algerror durante el andevuelve null
            return null;
        }
    }
}
