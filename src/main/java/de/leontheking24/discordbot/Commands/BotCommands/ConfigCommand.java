package de.leontheking24.discordbot.Commands.BotCommands;

import de.leontheking24.discordbot.Commands.Base.Command;
import de.leontheking24.discordbot.Commands.Base.CommandType;
import de.leontheking24.discordbot.Config.ConfigManager;
import de.leontheking24.discordbot.DiscordBot;
import de.leontheking24.discordbot.ServerManager;
import de.leontheking24.discordbot.Utils.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;

import java.awt.*;
import java.io.*;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;

public class ConfigCommand extends Command {

    private final ServerManager serverManager;
    private final ConfigManager configManager;
    private final Utils utils = DiscordBot.getUtils();
    private final static String trigger = "config";

    public ConfigCommand(ServerManager serverManager) {
        super(serverManager, trigger, serverManager.getMessage("config_description"), "command.config", CommandType.Bot);
        this.serverManager = serverManager;
        this.configManager = serverManager.getConfigManager();
    }

    @Override
    public void executeCommand(TextChannel channel, Message message) {
        if(!isArgumentLengthZero(message)) {
            String[] args = getArguments(message);
            if(args.length > 0) {
                if(args[0].equalsIgnoreCase("set")) {
                    set(channel, args);
                    return;

                } else if(args[0].equalsIgnoreCase("get")) {
                    get(channel, args);
                    return;

                } else if(args[0].equalsIgnoreCase("file")) {
                    file(message);
                    return;

                } else if(args[0].equalsIgnoreCase("upload")) {
                    upload(channel, message);
                    return;

                } else {
                    channel.sendMessage(helpOverview()).queue();
                    return;
                }
            }
        }
        channel.sendMessage(helpOverview()).queue();
    }

    private MessageEmbed helpOverview() {
        EmbedBuilder help = new EmbedBuilder();
        help.setTitle(serverManager.getMessage("config_help_title")).setColor(Color.MAGENTA);
        help.setDescription(serverManager.getMessage("config_help_description"));
        help.addField(serverManager.getMessage("config_help_set"), "> " + serverManager.getBotCommandPrefix() + trigger + " set <Key> <Value>", false);
        help.addField(serverManager.getMessage("config_help_get"), "> " + serverManager.getBotCommandPrefix() + trigger + " get <Key>", false);
        help.addField(serverManager.getMessage("config_help_file"), "> " + serverManager.getBotCommandPrefix() + trigger + " file", false);
        help.addField(serverManager.getMessage("config_help_upload"), "> " + serverManager.getBotCommandPrefix() + trigger + " upload", false);

        return help.build();
    }

    private void set(TextChannel channel, String[] args) {
        if(args.length == 3) {
            if(configManager.isConfigExists(args[1])) {
                if(configManager.isStringCorrectType(args[2], configManager.getDataTypeClass(args[1]))) {
                    configManager.updateConfig(args[1], args[2]);
                    channel.sendMessage(utils.createEmbed(serverManager.getMessage("config_set_success_title"), Color.GREEN,
                            serverManager.getMessage("config_set_success_body").replace("{key}", args[1]).replace("{value}", args[2]))).queue();
                } else {
                    try {
                        channel.sendMessage(utils.createEmbed(serverManager.getMessage("config_set_wrongformat_title"), Color.RED,
                                serverManager.getMessage("config_set_wrongformat_body").replace("{key}", args[1]).replace("{type}",
                                        Class.forName(configManager.getDataTypeClass(args[1])).getSimpleName()))).queue();
                    } catch (ClassNotFoundException e) {
                        DiscordBot.getLogger().log(Level.WARNING, e.getMessage());
                    }
                }
            } else {
                channel.sendMessage(utils.createEmbed(serverManager.getMessage("config_set_notfound_title"), Color.RED,
                        serverManager.getMessage("config_set_notfound_body").replace("{key}", args[1]))).queue();
            }
        }
        channel.sendMessage(utils.createEmbed(serverManager.getMessage("config_set_syntax_title"), Color.RED,
                serverManager.getMessage("config_set_syntax_body").replace("{prefix}", serverManager.getBotCommandPrefix()).replace("{trigger}", trigger))).queue();
    }

    private void get(TextChannel channel, String[] args) {
        if(args.length == 2) {
            if(configManager.isConfigExists(args[1])) {
                EmbedBuilder configValue = new EmbedBuilder();
                configValue.setColor(Color.GREEN).setTitle(serverManager.getMessage("config_get_success_title"));
                configValue.addField(serverManager.getMessage("config_get_success_key"), args[1],true);
                configValue.addField(serverManager.getMessage("config_get_success_value"), configManager.getConfig(args[1]), true);
                try {
                    configValue.addField(serverManager.getMessage("config_get_success_type"), Class.forName(configManager.getDataTypeClass(args[1])).getSimpleName(), true);
                } catch (ClassNotFoundException e) {
                    DiscordBot.getLogger().log(Level.WARNING, e.getMessage());
                }

                channel.sendMessage(configValue.build()).queue();
            } else {
                channel.sendMessage(utils.createEmbed(serverManager.getMessage("config_get_notfound_title"), Color.RED,
                        serverManager.getMessage("config_get_notfound_body").replace("{key}", args[1]))).queue();
            }

        } else {
            channel.sendMessage(utils.createEmbed(serverManager.getMessage("config_get_syntax_title"), Color.RED,
                    serverManager.getMessage("config_get_syntax_body").replace("{prefix}", serverManager.getBotCommandPrefix()).replace("{trigger}", trigger))).queue();
        }
    }

    private void file(Message message) {
        message.getAuthor().openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage(serverManager.getMessage("config_file")).addFile(
                serverManager.getFileGenerator().generateFile()).queue());
    }

    private void upload(TextChannel channel, Message message) {
        if(message.getAttachments().size() == 1) {
            if(message.getAttachments().get(0).getFileName().equals("Config.properties")) {
                try(InputStream inputStream = new FileInputStream(message.getAttachments().get(0).downloadToFile().get())) {
                    Properties properties = new Properties();
                    properties.load(inputStream);

                    for(Object object : properties.keySet()) {
                        if(configManager.isConfigExists(object.toString())) {
                            if(configManager.isStringCorrectType(properties.get(object).toString(), configManager.getDataTypeClass(object.toString()))) {
                                configManager.updateConfig(object.toString(), properties.get(object).toString());
                            }
                        }
                    }

                    channel.sendMessage(utils.createEmbed(serverManager.getMessage("config_upload_success_title"), Color.GREEN,
                            serverManager.getMessage("config_upload_success_body"))).queue();

                } catch (IOException | ExecutionException | InterruptedException e) {
                    DiscordBot.getLogger().log(Level.WARNING, e.getMessage());
                }


            } else {
                channel.sendMessage(utils.createEmbed(serverManager.getMessage("config_upload_wrong_title"), Color.RED,
                        serverManager.getMessage("config_upload_wrong_body"))).queue();
            }

        } else {
            channel.sendMessage(utils.createEmbed(serverManager.getMessage("config_upload_missing_title"), Color.RED, serverManager.getMessage("config_upload_missing_body"))).queue();
        }
    }
}
