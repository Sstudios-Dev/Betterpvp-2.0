package com.example.colorname

import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

class colorname : JavaPlugin() {

    override fun onEnable() {
        // Register the "/colorname" command
        getCommand("colorname")?.setExecutor(ColorNameCommand())
    }

    class ColorNameCommand : CommandExecutor {
        override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
            // Check if the command was executed by a player
            if (sender is Player) {
                // Check if the player provided an argument
                if (args.isNotEmpty()) {
                    // Get the first argument as the color code
                    val colorCode = args[0]
                    // Check if the color code is valid
                    if (ChatColor.getByChar(colorCode) != null) {
                        // Change the player's name color
                        sender.setDisplayName("${ChatColor.getByChar(colorCode)}${sender.name}")
                        sender.sendMessage("Your name color has been changed!")
                    } else {
                        sender.sendMessage("Invalid color code. Use a valid Minecraft color code (0-9, a-f).")
                    }
                } else {
                    sender.sendMessage("Usage: /colorname <colorcode>")
                }
                return true
            }
            return false
        }
    }
}
