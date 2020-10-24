package de.leontheking24.discordbot.Commands.BotCommands;

import de.leontheking24.discordbot.Commands.Base.Command;
import de.leontheking24.discordbot.Commands.Base.CommandType;
import de.leontheking24.discordbot.DiscordBot;
import de.leontheking24.discordbot.ServerManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.Date;

public class HelpCommand extends Command {

    private final static String trigger = "help";
    private final ServerManager serverManager;

    public HelpCommand(ServerManager serverManager) {
        super(serverManager, trigger, serverManager.getMessage("help_description"), "command.help", CommandType.Bot);
        this.serverManager = serverManager;
    }

    @Override
    public void executeCommand(TextChannel channel, Message message) {
        EmbedBuilder info = new EmbedBuilder();
        info.setColor(0xD631B5);
        info.addField(serverManager.getMessage("help_command_title"), serverManager.getMessage("help_command_body"), false);
        info.addField(serverManager.getMessage("help_config_title"), serverManager.getMessage("help_config_body"), false);
        info.addField(serverManager.getMessage("help_help_title"), serverManager.getMessage("help_help_body"), false);
        info.addField(serverManager.getMessage("help_note_title"), serverManager.getMessage("help_note_body"), false);
        info.addField("➤ " + serverManager.getMessage("help_add_title"), serverManager.getMessage("help_add_body"), false);
        info.addField(serverManager.getMessage("help_author_title"), serverManager.getMessage("help_author_body"), true);
        info.addField(serverManager.getMessage("help_version"), DiscordBot.version,  true);
        info.setTimestamp(new Date().toInstant());

        channel.sendMessage(info.build()).queue();
    }
}
