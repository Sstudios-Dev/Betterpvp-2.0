package io.github.sstudiosdev.util.constructors;

import io.github.sstudiosdev.util.ParseUtil;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Clase utilitaria para manejar archivos de configuración YAML en Bukkit/Spigot.
 */
@Getter
public class Config {

    // Archivo de configuración
    private final File file;

    // Configuración de Bukkit asociada al archivo
    private final YamlConfiguration bukkitConfiguration;

    /**
     * Constructor que inicializa el archivo y la configuración de Bukkit.
     *
     * @param plugin    Instancia del plugin JavaPlugin.
     * @param fileName  Nombre del archivo de configuración (sin la extensión .yml).
     */
    public Config(final JavaPlugin plugin, final String fileName) {
        this.file = initializeFile(plugin, fileName);
        this.bukkitConfiguration = YamlConfiguration.loadConfiguration(file);
    }

    /**
     * Inicializa el archivo de configuración.
     *
     * @param plugin    Instancia del plugin JavaPlugin.
     * @param fileName  Nombre del archivo de configuración (sin la extensión .yml).
     * @return          El archivo de configuración inicializado.
     */
    private File initializeFile(final JavaPlugin plugin, final String fileName) {
        final File pluginDataFolder = plugin.getDataFolder();
        if (!pluginDataFolder.exists()) {
            pluginDataFolder.mkdir();
        }

        final File configFile = new File(pluginDataFolder, fileName + ".yml");
        if (!configFile.exists()) {
            try {
                // Guarda el archivo de configuración predeterminado del plugin si no existe
                plugin.saveResource(fileName + ".yml", false);
            } catch (final Exception e) {
                e.printStackTrace();
            }
        }
        return configFile;
    }

    /**
     * Verifica si la configuración contiene una clave específica.
     *
     * @param path  La clave a verificar.
     * @return      true si la clave existe, false si no.
     */
    public boolean contains(final String path) {
        return bukkitConfiguration.contains(path);
    }

    /**
     * Inserta un valor en la configuración.
     *
     * @param path   La clave donde se insertará el valor.
     * @param value  El valor a insertar.
     */
    public void insert(final String path, final Object value) {
        bukkitConfiguration.set(path, value);
        save();
    }

    /**
     * Obtiene el valor asociado a una clave en la configuración.
     *
     * @param path  La clave a consultar.
     * @return      El valor asociado a la clave.
     */
    public Object get(final String path) {
        return bukkitConfiguration.get(path, "");
    }

    /**
     * Obtiene un valor entero asociado a una clave en la configuración.
     *
     * @param path  La clave a consultar.
     * @return      El valor entero asociado a la clave.
     */
    public int getInt(final String path) {
        return bukkitConfiguration.getInt(path, 0);
    }

    /**
     * Obtiene un valor decimal asociado a una clave en la configuración.
     *
     * @param path  La clave a consultar.
     * @return      El valor decimal asociado a la clave.
     */
    public double getDouble(final String path) {
        return bukkitConfiguration.getDouble(path, 0.0);
    }

    /**
     * Obtiene un valor largo asociado a una clave en la configuración.
     *
     * @param path  La clave a consultar.
     * @return      El valor largo asociado a la clave.
     */
    public long getLong(final String path) {
        return bukkitConfiguration.getLong(path, 0L);
    }

    /**
     * Obtiene un valor decimal (flotante) asociado a una clave en la configuración.
     *
     * @param path  La clave a consultar.
     * @return      El valor decimal (flotante) asociado a la clave.
     */
    public float getFloat(final String path) {
        return (float) bukkitConfiguration.getDouble(path, 0F);
    }

    /**
     * Obtiene un valor booleano asociado a una clave en la configuración.
     *
     * @param path  La clave a consultar.
     * @return      El valor booleano asociado a la clave.
     */
    public boolean getBoolean(final String path) {
        return bukkitConfiguration.getBoolean(path, false);
    }

    /**
     * Obtiene una cadena de texto asociada a una clave en la configuración.
     * Además, realiza la traducción de códigos de color en la cadena.
     *
     * @param path  La clave a consultar.
     * @return      La cadena de texto asociada a la clave, con códigos de color traducidos.
     */
    public String getString(final String path) {
        return ChatColor.translateAlternateColorCodes('&', bukkitConfiguration.getString(path, ""));
    }

    /**
     * Obtiene una lista de cadenas de texto asociadas a una clave en la configuración.
     * Además, realiza la traducción de códigos de color en cada cadena.
     *
     * @param path  La clave a consultar.
     * @return      La lista de cadenas de texto asociada a la clave, con códigos de color traducidos.
     */
    public List<String> getStringList(final String path) {
        return bukkitConfiguration.getStringList(path)
                .stream()
                .map(s -> ChatColor.translateAlternateColorCodes('&', s))
                .collect(Collectors.toList());
    }

    /**
     * Obtiene una lista de cadenas de texto asociadas a una clave en la configuración.
     * Además, realiza la traducción de códigos de color en cada cadena.
     * Si la clave no existe y se especifica el chequeo, devuelve una lista vacía.
     *
     * @param path   La clave a consultar.
     * @param check  Si se debe verificar la existencia de la clave.
     * @return       La lista de cadenas de texto asociada a la clave, con códigos de color traducidos.
     */
    public List<String> getStringList(final String path, final boolean check) {
        if (check && !bukkitConfiguration.contains(path)) {
            return new ArrayList<>();
        }

        return getStringList(path);
    }

    /**
     * Obtiene una sección de configuración asociada a una clave en la configuración.
     *
     * @param path  La clave a consultar.
     * @return      La sección de configuración asociada a la clave.
     */
    public ConfigurationSection getSection(final String path) {
        return bukkitConfiguration.getConfigurationSection(path);
    }

    /**
     * Carga la configuración desde el archivo.
     */
    public void load() {
        try {
            bukkitConfiguration.load(file);
        } catch (final IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    /**
     * Guarda la configuración en el archivo.
     */
    public void save() {
        try {
            bukkitConfiguration.save(file);
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Obtiene una ubicación (Location) asociada a una clave en la configuración.
     *
     * @param path  La clave a consultar.
     * @return      La ubicación (Location) asociada a la clave.
     */
    public Location getLocation(final String path) {
        return ParseUtil.tryParseLocation(this.getString(path));
    }

    /**
     * Obtiene una lista de ubicaciones (Location) asociadas a una clave en la configuración.
     *
     * @param path  La clave a consultar.
     * @return      La lista de ubicaciones (Location) asociada a la clave.
     */
    public List<Location> getLocationList(final String path) {
        return getStringList(path)
                .stream()
                .map(ParseUtil::tryParseLocation)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * Inserta una ubicación (Location) en la configuración.
     *
     * @param path      La clave donde se insertará la ubicación.
     * @param location  La ubicación (Location) a insertar.
     */
    public void insertLocation(final String path, final Location location) {
        insert(path, location.getWorld().getName() + "," + location.getBlockX() + "," + location.getBlockY() + "," + location.getBlockZ());
    }

    /**
     * Inserta una lista de ubicaciones (Location) en la configuración.
     *
     * @param path               La clave donde se insertará la lista de ubicaciones.
     * @param locationToWrite    La lista de ubicaciones (Location) a insertar.
     */
    public void insertLocationList(final String path, final List<Location> locationToWrite) {
        List<String> locationToAdd = locationToWrite.stream()
                .map(locationAdd -> locationAdd.getWorld().getName() + "," + locationAdd.getBlockX() + "," + locationAdd.getBlockY() + "," + locationAdd.getBlockZ())
                .collect(Collectors.toList());

        insert(path, locationToAdd);
    }
}
