package com.example.helloworld

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

    class HelloCommand : CommandExecutor {
        override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
            // Check if the command was executed by a player
            if (sender is Player) {
                // Send a "Hello World" message to the player
                sender.sendMessage("Hello World!")
                return true
            }
            return false
        }
    }
}
