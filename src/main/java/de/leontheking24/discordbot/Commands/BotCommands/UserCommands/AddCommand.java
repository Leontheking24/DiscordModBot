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

public class AddCommand extends Command {

    private final ServerManager serverManager;
    private final static String trigger = "AddCommand";
    private final CommandManager commandManager;
    private final Utils utils = DiscordBot.getUtils();
    private final String permissionBase = "command.user.";

    public AddCommand(ServerManager serverManager) {
        super(serverManager, trigger, serverManager.getMessage("addcommand_description"), "command.addCommand", CommandType.Bot);
        this.serverManager = serverManager;
        commandManager = serverManager.getCommandManager();
    }

    @Override
    public void executeCommand(TextChannel channel, Message message) {
        if(!isArgumentLengthZero(message)) {
            String[] arguments = message.getContentRaw().substring(serverManager.getBotCommandPrefix().length() + trigger.length() + 1).split(commandManager.getSeparateKey());

            if(arguments.length >= 3) {
                String trigger = arguments[0];
                if(!commandManager.isCommandExists(trigger)) {
                    String value = arguments[1];
                    String description = arguments[2];
                    String permission = null;
                    if(arguments.length == 4) {
                        permission = permissionBase + arguments[3];
                    }
                    Command command = new Command(serverManager, trigger, value, description, permission, CommandType.User);
                    commandManager.addCommand(command);
                    serverManager.getCommandSqlManager().addCommand(command);
                    channel.sendMessage(utils.createEmbed(serverManager.getMessage("addcommand_success_title"), Color.GREEN,
                            serverManager.getMessage("addcommand_success_message").replace("{Command}", trigger))).queue();
                    return;
                }
                channel.sendMessage(utils.createEmbed(serverManager.getMessage("addcommand_duplicate_title"), Color.RED,
                            serverManager.getMessage("addcommand_duplicate_message").replace("{Command}", trigger))).queue();
                return;
            }
        }
        channel.sendMessage(utils.createEmbed(serverManager.getMessage("addcommand_syntax_title"), Color.RED,
                serverManager.getMessage("addcommand_syntax_message").replace("{prefix}", serverManager.getBotCommandPrefix()).replace("{trigger}", trigger).replace("{separate}", commandManager.getSeparateKey()))).queue();
    }
}
