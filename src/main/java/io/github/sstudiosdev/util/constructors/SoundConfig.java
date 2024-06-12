package io.github.sstudiosdev.util.constructors;

import lombok.Getter;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Clase utilitaria para manejar el archivo de configuración sound.yml en Bukkit/Spigot.
 */
@Getter
public class SoundConfig {

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
    public SoundConfig(final JavaPlugin plugin, final String fileName) {
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
     * Obtiene un sonido (Sound) asociado a una clave en la configuración.
     *
     * @param path  La clave a consultar.
     * @return      El sonido (Sound) asociado a la clave.
     */
    public Sound getSound(final String path) {
        String soundName = bukkitConfiguration.getString(path, "");
        return Sound.valueOf(soundName.toUpperCase());
    }

    /**
     * Obtiene una lista de sonidos (Sound) asociados a una clave en la configuración.
     *
     * @param path  La clave a consultar.
     * @return      La lista de sonidos (Sound) asociada a la clave.
     */
    public List<Sound> getSoundList(final String path) {
        return bukkitConfiguration.getStringList(path)
                .stream()
                .map(soundName -> Sound.valueOf(soundName.toUpperCase()))
                .collect(Collectors.toList());
    }

    public void saveSound(final String path, final Sound sound) {
        bukkitConfiguration.set(path, sound.name());
        save();
    }

    public void saveSoundList(final String path, final List<Sound> sounds) {
        List<String> soundNames = sounds.stream().map(Sound::name).collect(Collectors.toList());
        bukkitConfiguration.set(path, soundNames);
        save();
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
}
