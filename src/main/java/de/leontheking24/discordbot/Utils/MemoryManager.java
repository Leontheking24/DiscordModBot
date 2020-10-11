package de.leontheking24.discordbot.Utils;

import de.leontheking24.discordbot.DiscordBot;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;

public class MemoryManager {

    private final Runtime runtime;
    private final NumberFormat numberFormat = new DecimalFormat("#####,##0.0");

    public MemoryManager() {
        runtime = Runtime.getRuntime();
    }

    public void startMemoryScheduler() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.gc();
                DiscordBot.getLogger().log(Level.INFO, "\nUsed memory space: " + getUsedMemory() + " MB (" + getUsedMemoryPercentage() + "%)" +
                        "\nFree memory space: " + getFreeMemory() + " MB" +
                        "\nTotal memory space: " + getTotalMemory() + " MB");
            }
        }, 0, 1000*60);
    }

    public String getTotalMemory() {
        return numberFormat.format(runtime.totalMemory() / (1024.0*1024.0));
    }

    public String getFreeMemory() {
        return numberFormat.format(runtime.freeMemory() / (1024.0*1024.0));
    }

    public String getUsedMemory() {
        return numberFormat.format((runtime.totalMemory() - runtime.freeMemory()) / (1024.0*1024.0));
    }

    public String getUsedMemoryPercentage() {
        return numberFormat.format((runtime.totalMemory() - runtime.freeMemory()) / (1024.0*1024.0) / (runtime.totalMemory() / (1024.0*1024.0)) * 100);
    }
}
