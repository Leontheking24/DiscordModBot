package de.leontheking24.discordbot.Config;

import de.leontheking24.discordbot.DiscordBot;
import de.leontheking24.discordbot.ServerManager;

import java.io.*;
import java.util.Properties;
import java.util.logging.Level;

public class FileGenerator {

    private final ServerManager serverManager;

    public FileGenerator(ServerManager serverManager) {
        this.serverManager = serverManager;
    }

    public File generateFile() {
        String path = "I:\\Users\\LeonA\\DiscordBot\\src\\main\\resources\\Config.properties";
        File file = new File(path);

        try(OutputStream outputStream = new FileOutputStream(path)) {
            Properties toWrite = serverManager.getConfigManager().getConfigProperties();
            if(!Boolean.parseBoolean(toWrite.getProperty("isUsingOwnDatabase"))) {
                toWrite.remove("databaseDriver");
                toWrite.remove("databaseServer");
                toWrite.remove("databasePort");
                toWrite.remove("databaseName");
                toWrite.remove("databaseUsername");
                toWrite.remove("databasePassword");
            }
            toWrite.store(outputStream, null);

        } catch (IOException e) {
            DiscordBot.getLogger().log(Level.WARNING, e.getMessage());
        }
        return file;
    }
}
