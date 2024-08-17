//Please completely ignore all code and files located in the folder named “test”.
// This folder contains tests and code that are no longer relevant to our current project and should 
// not be considered in the development or review of our work.

package com.example.colorname

import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

class ColorName : JavaPlugin() {
    private lateinit var config: FileConfiguration

    override fun onEnable() {
        loadConfig()
        registerCommands()
    }

    override fun onDisable() {
        saveConfig()
    }

    private fun loadConfig() {
        saveDefaultConfig()
        config = getConfig()
    }

    private fun registerCommands() {
        getCommand("colorname")?.setExecutor(ColorNameCommand(this))
    }

    class ColorNameCommand(private val plugin: ColorName) : CommandExecutor {
        override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
            if (sender !is Player) {
                sender.sendMessage("${ChatColor.RED}This command can only be executed by players.")
                return true
            }

            if (!sender.hasPermission("colorname.use")) {
                sender.sendMessage("${ChatColor.RED}You don't have permission to use this command.")
                return true
            }

            if (args.isEmpty()) {
                sender.sendMessage("${ChatColor.RED}Usage: /colorname <color>")
                return true
            }

            val colorArg = args[0].toLowerCase()
            val color = getColor(colorArg)

            if (color == null) {
                sender.sendMessage("${ChatColor.RED}Invalid color. Use a color name or a valid Minecraft color code (0-9, a-f).")
                return true
            }

            val coloredName = "${color}${sender.name}${ChatColor.RESET}"
            sender.setDisplayName(coloredName)
            sender.sendMessage("${ChatColor.GREEN}Your name color has been changed to ${color}${color.name}${ChatColor.GREEN}!")

            return true
        }

        private fun getColor(colorArg: String): ChatColor? {
            // Check if the argument is a valid color name
            val colorByName = ChatColor.values().find { it.name.equals(colorArg, true) }
            if (colorByName != null) return colorByName

            // Check if the argument is a valid color code
            val colorByCode = ChatColor.getByChar(colorArg)
            if (colorByCode != null && colorByCode.isColor) return colorByCode

            return null
        }
    }
}
