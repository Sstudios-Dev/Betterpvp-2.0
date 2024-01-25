package io.github.sstudiosdev.vault.economy;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

public class EconomyManager {

    private final Economy economy;

    public EconomyManager(Economy economy) {
        this.economy = economy;
    }

    /**
     * Deposita una cantidad de dinero en la cuenta de un jugador.
     *
     * @param player Jugador al que se le depositará el dinero.
     * @param amount Cantidad de dinero a depositar.
     */
    public void depositMoney(Player player, double amount) {
        economy.depositPlayer(player, amount);
    }

    /**
     * Obtiene el saldo de un jugador.
     *
     * @param player Jugador del que se consultará el saldo.
     * @return El saldo del jugador.
     */
    public double getBalance(Player player) {
        return economy.getBalance(player);
    }

    /**
     * Retira una cantidad de dinero de la cuenta de un jugador.
     *
     * @param player Jugador del que se retirará el dinero.
     * @param amount Cantidad de dinero a retirar.
     * @return True si la operación se realizó con éxito, false si no hay suficiente dinero.
     */
    public boolean withdrawMoney(Player player, double amount) {
        if (economy.has(player, amount)) {
            economy.withdrawPlayer(player, amount);
            return true;
        }
        return false;
    }

    /**
     * Transfiere una cantidad de dinero de un jugador a otro.
     *
     * @param sender    Jugador que envía el dinero.
     * @param recipient Jugador que recibe el dinero.
     * @param amount    Cantidad de dinero a transferir.
     * @return True si la transferencia se realizó con éxito, false si no hay suficiente dinero.
     */
    public boolean transferMoney(Player sender, Player recipient, double amount) {
        if (withdrawMoney(sender, amount)) {
            depositMoney(recipient, amount);
            return true;
        }
        return false;
    }

    /**
     * Obtiene una lista de jugadores con los saldos más altos.
     *
     * @param limit Número máximo de jugadores en la lista.
     * @return Lista de jugadores con los saldos más altos.
     */
    public List<Player> getTopBalances(int limit) {
        return economy.getBanks().stream()
                .sorted((bank1, bank2) -> Double.compare(economy.getBalance(bank2), economy.getBalance(bank1)))
                .limit(limit)
                .map(bank -> Bukkit.getServer().getPlayer(bank))
                .filter(player -> player != null && player.isOnline())
                .collect(Collectors.toList());
    }

    /**
     * Realiza una apuesta con un jugador.
     *
     * @param player Jugador que realiza la apuesta.
     * @param amount Cantidad de dinero a apostar.
     * @return True si la apuesta fue exitosa, false si no hay suficiente dinero.
     */
    public boolean gamble(Player player, double amount) {
        if (withdrawMoney(player, amount)) {
            // Lógica de apuesta (puedes personalizar según tus necesidades)
            double randomValue = Math.random();
            if (randomValue < 0.5) {
                // El jugador gana
                depositMoney(player, amount * 2);
                return true;
            } else {
                // El jugador pierde
                return false;
            }
        }
        return false;
    }

    /**
     * Verifica si un jugador tiene al menos cierta cantidad de dinero.
     *
     * @param player Jugador a verificar.
     * @param amount Cantidad mínima de dinero requerida.
     * @return True si el jugador tiene al menos la cantidad especificada, false de lo contrario.
     */
    public boolean hasMinimumBalance(Player player, double amount) {
        return economy.has(player, amount);
    }
    
}
