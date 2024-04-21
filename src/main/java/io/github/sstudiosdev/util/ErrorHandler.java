package io.github.sstudiosdev.util;

import io.github.sstudiosdev.BetterPvP;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ErrorHandler implements Thread.UncaughtExceptionHandler {

    private final Plugin plugin;
    private final BetterPvP betterPvP;
    private final boolean errorHandlerEnabled;

    public ErrorHandler(Plugin plugin) {
        this.plugin = plugin;
        this.betterPvP = (BetterPvP) plugin;
        this.errorHandlerEnabled = Boolean.parseBoolean(betterPvP.getMainConfig().getString("error-handler"));
    }


    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
        if (errorHandlerEnabled) {
            // Log the error
            logError(throwable);

            // Notify players about the error
            notifyPlayers(throwable);

            // Prevent the plugin from being disabled
            thread.interrupt();
        }
    }

    private void logError(Throwable throwable) {
        try {
            // Create or append to the error log file
            File logFile = new File(plugin.getDataFolder(), "error.log");
            if (!logFile.exists()) {
                logFile.createNewFile();
            }

            // Write the error details to the log file
            FileWriter fw = new FileWriter(logFile, true);
            PrintWriter pw = new PrintWriter(fw);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String timestamp = sdf.format(new Date());
            pw.println("[" + timestamp + "] Unhandled exception in plugin " + plugin.getName());

            // Include the error message and class/method/line where the exception occurred
            pw.println("Error message: " + throwable.getMessage());
            for (StackTraceElement element : throwable.getStackTrace()) {
                pw.println("  at " + element.getClassName() + "." + element.getMethodName() +
                        "(" + element.getFileName() + ":" + element.getLineNumber() + ")");
            }
            pw.println();
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void notifyPlayers(Throwable throwable) {
        // Notify online players about the error
        String errorMessage = ChatColor.RED + "An unexpected error occurred in the plugin. Please contact the server administrator.";
        for (org.bukkit.entity.Player player : Bukkit.getOnlinePlayers()) {
            if (player.isOp()) {
                player.sendMessage(errorMessage);
            }
        }

        // Log the error message to the console
        plugin.getLogger().severe(errorMessage);

        // Log the stack trace to the console
        plugin.getLogger().severe(throwable.getMessage());
        for (StackTraceElement element : throwable.getStackTrace()) {
            plugin.getLogger().severe(element.toString());
        }
    }
}
