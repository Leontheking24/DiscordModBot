package de.leontheking24.discordbot.Commands.BotCommands.UserCommands;

import de.leontheking24.discordbot.Commands.Base.Command;
import de.leontheking24.discordbot.Commands.Base.CommandType;
import de.leontheking24.discordbot.DiscordBot;
import de.leontheking24.discordbot.Logger.Logger;
import de.leontheking24.discordbot.ServerManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

import java.awt.*;
import java.util.Date;
import java.util.logging.Level;

public class InfoCommand extends Command {

    private final ServerManager serverManager;
    private final static String trigger = "CommandInfo";

    public InfoCommand(ServerManager serverManager) {
        super(serverManager, trigger, serverManager.getMessage("info_description"), "command.commandInfo", CommandType.Bot);
        this.serverManager = serverManager;
    }

    @Override
    public void executeCommand(TextChannel channel, Message message) {
        if(!isArgumentLengthZero(message)) {
            String[] args = getArguments(message);
            if(args.length == 1) {
                if(serverManager.getCommandManager().isCommandExists(args[0])) {
                    Command command = serverManager.getCommandManager().getCommand(args[0]);
                    EmbedBuilder info = new EmbedBuilder();
                    String permission = serverManager.getMessage("info_no_permission_needed");

                    info.setTitle(serverManager.getMessage("info_message_title")).setColor(Color.GREEN);
                    info.setDescription(serverManager.getMessage("info_message_description").replace("{Command}", command.getTrigger()));
                    info.addField(serverManager.getMessage("info_message_cmd_trigger"), command.getTrigger(), true);
                    info.addField(serverManager.getMessage("info_message_cmd_description"), command.getDescription(), true);
                    if(command.hasValue()) {
                        info.addField(serverManager.getMessage("info_message_cmd_value"), command.getValue(), false);
                    }
                    if(!command.hasValue()) {
                        info.addBlankField(true);
                    }
                    if(command.hasPermission()) {
                        permission = command.getPermission();
                    }
                    info.addField(serverManager.getMessage("info_message_cmd_permission"), permission, true);
                    info.addField(serverManager.getMessage("info_message_cmd_type"), command.getCommandType().name(), true);
                    if(command.hasInfo()) {
                        info.addField(serverManager.getMessage("info_message_cmd_info"), command.getInfo(), false);
                    }
                    info.setFooter(serverManager.getMessage("info_message_cmd_from"));
                    info.setTimestamp(new Date().toInstant());
                    channel.sendMessage(info.build()).queue();
                    return;
                }
                channel.sendMessage(DiscordBot.getUtils().createEmbed(serverManager.getMessage("info_error_title"), Color.RED, serverManager.getMessage("info_error_body").replace("{Command}", args[0]))).queue();
                return;
            }
        }
        channel.sendMessage(DiscordBot.getUtils().createEmbed(serverManager.getMessage("info_syntax_title"), Color.RED, serverManager.getMessage("info_syntax_body").
                replace("{prefix}", serverManager.getBotCommandPrefix()).replace("{trigger}", trigger))).queue();
    }
}
