package io.github.sstudiosdev.vault;

import io.github.sstudiosdev.vault.economy.EconomyManager;
import lombok.Getter;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

/**
 * Clase que gestiona el hook con Vault y proporciona acceso a la instancia de Economy.
 */
public class VaultHookManager {

    @Getter
    private final EconomyManager economyManager;
    private Economy economy;

    /**
     * Constructor que establece automáticamente el hook con Vault al instanciar la clase.
     */
    public VaultHookManager() {
        // Ejecutar el método para establecer el hook con Vault
        setupVaultEconomy();

        this.economyManager = new EconomyManager(economy);
    }

    /**
     * Obtiene la instancia de Economy después de establecer el hook.
     *
     * @return Instancia de Economy si el hook se estableció con éxito, null si falló.
     */
    public Economy getEconomy() {
        return economy;
    }

    /**
     * Establece un hook con Vault y obtiene la instancia de Economy.
     *
     * @return True si el hook se estableció con éxito, false si falló.
     */
    public boolean setupVaultEconomy() {
        if (Bukkit.getServer().getPluginManager().getPlugin("Vault") == null) {
            // Vault no está instalado
            return false;
        }

        RegisteredServiceProvider<Economy> rsp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            // No se pudo obtener el proveedor de Economy
            return false;
        }

        economy = rsp.getProvider();
        return economy != null;
    }
}
