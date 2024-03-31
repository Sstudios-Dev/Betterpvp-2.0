package com.example.helloworld

import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

class HelloWorld : JavaPlugin() {

    override fun onEnable() {
        // Register the "/hello" command
        getCommand("hello")?.setExecutor(HelloCommand())
    }

    inner class HelloCommand : CommandExecutor {
        override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
            // Check if the command was executed by a player
            if (sender is Player) {
                // Check if the player has permission to use the command
                if (sender.hasPermission("helloworld.greet")) {
                    // Check if any arguments were provided
                    if (args.isNotEmpty()) {
                        val message = args.joinToString(" ")
                        val formattedMessage = formatMessage(message)
                        sender.sendMessage(formattedMessage)
                    } else {
                        sender.sendMessage("Usage: /hello <message>")
                    }
                } else {
                    sender.sendMessage("You do not have permission to use this command.")
                }
            } else {
                sender.sendMessage("This command can only be executed by players.")
            }
            return true
        }

        private fun formatMessage(message: String): String {
            // Apply color and format to the message
            return ChatColor.translateAlternateColorCodes('&', message)
        }
    }
}
