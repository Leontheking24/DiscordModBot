package de.leontheking24.discordbot.Commands.Base;

import de.leontheking24.discordbot.DiscordBot;
import de.leontheking24.discordbot.Permissions.PermissionManager;
import de.leontheking24.discordbot.ServerManager;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;

public class CommandListener extends ListenerAdapter {

    private CommandManager commandManager;

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        long serverId = event.getGuild().getIdLong();
        ServerManager serverManager = DiscordBot.getServerManager(serverId);
        PermissionManager permissionManager = serverManager.getPermissionManager();
        commandManager = serverManager.getCommandManager();

        String[] args = event.getMessage().getContentRaw().split(" ");
        if(args[0].length() > 0 ) {
            String botPrefix = serverManager.getBotCommandPrefix();
            String userPrefix = serverManager.getUserCommandPrefix();
            String startBot = args[0].substring(botPrefix.length());
            String startUser = args[0].substring(userPrefix.length());
            String startMention = null;
            if(args.length > 1) {
                startMention = args[1];
            }

            Command command = getCommand(args, botPrefix, userPrefix, startBot, startUser, startMention);
            if(command != null) {
                if(command.hasPermission()) {
                    if(permissionManager.playerHasPermission(event.getAuthor().getIdLong(), command.getPermission())) {
                        command.executeCommand(event.getChannel(), event.getMessage());
                    } else {
                        event.getChannel().sendMessage(DiscordBot.getUtils().createEmbed(serverManager.getMessage("command_no_permission_head"), Color.RED,
                                serverManager.getMessage("command_no_permission_body"))).queue();
                    }
                } else {
                    command.executeCommand(event.getChannel(), event.getMessage());
                }
            }
        }
    }

    public Command getCommand(String[] args, String botPrefix, String userPrefix, String startBot, String startUser, String startMention) {
        if(args[0].equals("<@!750371933031235746>") || args[0].startsWith(botPrefix) || args[0].startsWith(userPrefix)) {
            if(startMention != null && commandManager.isCommandExists(startMention)) {
                return commandManager.getCommand(startMention);
            } else if(commandManager.isCommandExists(startBot)) {
                return commandManager.getCommand(startBot);
            } else if(commandManager.isCommandExists(startUser)) {
                return commandManager.getCommand(startUser);
            }
        }
        return null;
    }

}
