package de.leontheking24.discordbot.Commands.BotCommands.RoleAssignment;

import de.leontheking24.discordbot.DiscordBot;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;

public class ReactionListener extends ListenerAdapter {

    @Override
    public void onMessageReactionAdd(@Nonnull MessageReactionAddEvent event) {
        long serverId = event.getGuild().getIdLong();
        ReactRoleManager reactRoleManager = DiscordBot.getServerManager(serverId).getRoleAssignmentManager();

        if(!event.getUser().isBot()) {
            long messageID = event.getMessageIdLong();
            if(reactRoleManager.roleMessageExists(messageID)) {
                if(reactRoleManager.getReactFromMessage(messageID).contains(event.getReactionEmote().getName())) {
                    long roleID = reactRoleManager.getRoleFromMessageAndReact(messageID, event.getReactionEmote().getName());
                    for(Role role : event.getGuild().getRoles()) {
                        if(role.getIdLong() == roleID) {
                            event.getGuild().addRoleToMember(event.getMember(), role).queue();
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onMessageReactionRemove(@Nonnull MessageReactionRemoveEvent event) {
        long serverId = event.getGuild().getIdLong();
        ReactRoleManager reactRoleManager = DiscordBot.getServerManager(serverId).getRoleAssignmentManager();
        if(!event.getUser().isBot()) {
            long messageID = event.getMessageIdLong();
            if(reactRoleManager.roleMessageExists(messageID)) {
                if(reactRoleManager.getReactFromMessage(messageID).contains(event.getReactionEmote().getName())) {
                    if(reactRoleManager.isRoleRemovable(messageID, event.getReactionEmote().getName())) {
                        long roleID = reactRoleManager.getRoleFromMessageAndReact(messageID, event.getReactionEmote().getName());
                        for(Role role : event.getGuild().getRoles()) {
                            if(role.getIdLong() == roleID) {
                                event.getGuild().removeRoleFromMember(event.getMember(), role).queue();
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onMessageDelete(@Nonnull MessageDeleteEvent event) {
        long serverId = event.getGuild().getIdLong();
        ReactRoleManager reactRoleManager = DiscordBot.getServerManager(serverId).getRoleAssignmentManager();
        if(reactRoleManager.roleMessageExists(event.getMessageIdLong())) {
            reactRoleManager.removeRoleMessage(event.getMessageIdLong());
        }
    }
    
}
