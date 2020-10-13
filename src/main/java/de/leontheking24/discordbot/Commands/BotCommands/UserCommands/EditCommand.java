package de.leontheking24.discordbot.Commands.BotCommands.UserCommands;

import de.leontheking24.discordbot.Commands.Base.Command;
import de.leontheking24.discordbot.Commands.Base.CommandManager;
import de.leontheking24.discordbot.Commands.Base.CommandType;
import de.leontheking24.discordbot.DiscordBot;
import de.leontheking24.discordbot.ServerManager;
import de.leontheking24.discordbot.Utils.Utils;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

import java.awt.*;

public class EditCommand extends Command {

    private final ServerManager serverManager;
    private final CommandManager commandManager;
    private final Utils utils = DiscordBot.getUtils();
    private static final String trigger = "EditCommand";
    private final String permissionBase = "command.user.";

    public EditCommand(ServerManager serverManager) {
        super(serverManager, trigger, serverManager.getMessage("editcommand_description"), "command.editCommand", CommandType.Bot);
        this.serverManager = serverManager;
        this.commandManager = serverManager.getCommandManager();
    }

    @Override
    public void executeCommand(TextChannel channel, Message message) {
        if(!isArgumentLengthZero(message)) {
            String[] arguments = message.getContentRaw().substring(serverManager.getBotCommandPrefix().length() + trigger.length() + 1).split("\n");
            if(arguments.length >= 3) {
                String trigger = arguments[0];
                if(commandManager.isCommandExists(trigger)) {
                    Command command = serverManager.getCommandManager().getCommand(trigger);
                    if(command.getCommandType().equals(CommandType.User)) {
                        command.setValue(arguments[1]);
                        command.setDescription(arguments[2]);

                        if(arguments.length == 4) {
                            command.setPermission(permissionBase + arguments[3]);
                        }
                        serverManager.getCommandManager().updateCommand(command);
                        channel.sendMessage(utils.createEmbed(serverManager.getMessage("editcommand_success_title"), Color.GREEN, serverManager.getMessage("editcommand_success_body").replace("{trigger}", command.getTrigger()))).queue();

                    } else {
                        channel.sendMessage(utils.createEmbed(serverManager.getMessage("editcommand_type_title"), Color.RED, serverManager.getMessage("editcommand_type_body"))).queue();
                        return;
                    }

                } else {
                    channel.sendMessage(utils.createEmbed(serverManager.getMessage("editcommand_notfound_title"), Color.RED, serverManager.getMessage("editcommand_notfound_body").replace("{trigger}", trigger))).queue();
                    return;
                }
                return;
            }
        }
        channel.sendMessage(utils.createEmbed(serverManager.getMessage("editcommand_syntax_title"), Color.RED, serverManager.getMessage("editcommand_syntax_body").replace("{prefix}", serverManager.getBotCommandPrefix()).replace("{trigger}", trigger))).queue();
    }
}
