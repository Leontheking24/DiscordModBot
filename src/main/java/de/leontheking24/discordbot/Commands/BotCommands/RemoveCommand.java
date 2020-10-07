package de.leontheking24.discordbot.Commands.BotCommands;

import de.leontheking24.discordbot.Commands.Base.Command;
import de.leontheking24.discordbot.Commands.Base.CommandManager;
import de.leontheking24.discordbot.Commands.Base.CommandType;
import de.leontheking24.discordbot.ServerManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;

import java.awt.*;

public class RemoveCommand extends Command {

    private final ServerManager serverManager;
    private final static String trigger = "RemoveCommand";
    private final CommandManager commandManager;

    public RemoveCommand(ServerManager serverManager) {
        super(serverManager, trigger, serverManager.getMessage("removecommand_description"), "command.removeCommand", CommandType.Bot);
        this.serverManager = serverManager;
        this.commandManager = serverManager.getCommandManager();
    }

    @Override
    public void executeCommand(TextChannel channel, Message message) {
        if(!isArgumentLengthZero(message)) {
            String[] args = getArguments(message);
            if(args.length == 1) {
                String trigger = args[0];
                if(commandManager.isCommandExists(trigger)) {
                    Command command = commandManager.getCommand(trigger);
                    if(command.getCommandType().equals(CommandType.User)) {
                        commandManager.deleteCommand(command);
                        channel.sendMessage(createMessage(serverManager.getMessage("removecommand_success_title"), Color.GREEN,
                                serverManager.getMessage("removecommand_success_body").replace("{Command}", command.getTrigger()))).queue();
                        return;
                    }
                    channel.sendMessage(createMessage(serverManager.getMessage("removecommand_error_title"), Color.RED, serverManager.getMessage("removecommand_error_body"))).queue();
                    return;
                }
                channel.sendMessage(createMessage(serverManager.getMessage("removecommand_didntfound_title"), Color.RED, serverManager.getMessage("removecommand_didntfound_body").replace("{Command}", trigger))).queue();
                return;
            }
        }
        channel.sendMessage(createMessage(serverManager.getMessage("removecommand_syntax_title"), Color.RED,
                serverManager.getMessage("removecommand_syntax_body").replace("{prefix}", serverManager.getBotCommandPrefix()).replace("{trigger}", trigger))).queue();
    }

    public MessageEmbed createMessage(String title, Color color, String message) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(color).setTitle(title).setDescription(message);
        return embedBuilder.build();
    }
}
