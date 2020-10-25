package de.leontheking24.discordbot.Logger;

import de.leontheking24.discordbot.DiscordBot;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;

public class Logger {

    private final String path;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
    private final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
    private final java.util.logging.Logger defaultLogger = java.util.logging.Logger.getLogger("DiscordBot");

    public Logger(String path) {
        this.path = path;
    }

    public void log(Level level, String message, boolean writeInConsole) {
        writeIntoFile(level, message, new Date());
        if(writeInConsole) defaultLogger.log(level, message);
    }

    public void log(Level level, String message) {
        writeIntoFile(level, message, new Date());
        defaultLogger.log(level, message);
    }

    private File getFile(Date date) {
        File file = new File(path + dateFormat.format(date) + ".log");
        if(!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                DiscordBot.getLogger().log(Level.WARNING, e.getMessage());
            }
        }
        return file;
    }

    private void writeIntoFile(Level level, String message, Date date) {
        try {
            Files.write(Paths.get(getFile(date).toURI()), ("[" + level.getName() + "] (" + timeFormat.format(date) + "): " + message + "\n").getBytes(), StandardOpenOption.APPEND);
        } catch (IOException e) {
            DiscordBot.getLogger().log(Level.WARNING, e.getMessage());
        }
    }

}
