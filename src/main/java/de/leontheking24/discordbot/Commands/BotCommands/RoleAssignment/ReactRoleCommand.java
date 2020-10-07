package de.leontheking24.discordbot.Commands.BotCommands.RoleAssignment;

import com.vdurmont.emoji.EmojiParser;
import de.leontheking24.discordbot.Commands.Base.Command;
import de.leontheking24.discordbot.Commands.Base.CommandType;
import de.leontheking24.discordbot.DiscordBot;
import de.leontheking24.discordbot.ServerManager;
import de.leontheking24.discordbot.Utils.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;

import java.awt.*;
import java.util.List;

public class ReactRoleCommand extends Command {

    private ServerManager serverManager;
    private final static String trigger = "ReactRole";
    private ReactRoleManager reactRoleManager;
    private final Utils utils = DiscordBot.getUtils();

    public ReactRoleCommand(ServerManager serverManager) {
        super(serverManager, trigger, serverManager.getMessage("reactrole_description"), "command.reactrole", CommandType.Bot);
        this.serverManager = serverManager;
        reactRoleManager = serverManager.getRoleAssignmentManager();
    }

    @Override
    public void executeCommand(TextChannel channel, Message message) {
        String[] args = message.getContentRaw().split(" ");

        if(args.length >= 3 && args.length <= 6) {
            List<TextChannel> channels = message.getMentionedChannels();
            List<Role> roles = message.getMentionedRoles();
            List<String> emojis = EmojiParser.extractEmojis(message.getContentRaw());
            boolean toRemove = searchForRemove(args);
            boolean toEdit = searchForEdit(args);

            TextChannel sendChannel = channel;
            if(toRemove) {
                remove(channel, message, emojis, sendChannel);

            } else if(toEdit) {
                edit(channel, message, sendChannel);

            } else if(roles.size() > 0 && emojis.size() > 0) {
                Role role = roles.get(0);
                String emoji = emojis.get(0);
                final long messageID = utils.searchForMessageID(message.getContentRaw().split(" "));
                boolean isRemovable = searchForBoolean(message.getContentRaw().split(" "));

                if(channels.size() > 0) {
                    sendChannel = channels.get(0);
                }
                message.delete().queue();

                if(messageID != 0) {
                    sendChannel.retrieveMessageById(messageID).queue((botMessage -> {
                        botMessage.addReaction(emoji).queue();
                        reactRoleManager.addReactRole(messageID, role.getIdLong(), emoji, isRemovable);
                    }));

                } else {
                    sendChannel.sendMessage(utils.createEmbed(serverManager.getMessage("reactrole_success_title"), Color.MAGENTA,
                            serverManager.getMessage("reactrole_success_body").replace("{Role}", role.getName()))).queue(botMessage -> {
                        botMessage.addReaction(emoji).queue();
                        reactRoleManager.addReactRole(botMessage.getIdLong(), role.getIdLong(), emoji, isRemovable);
                    });
                }
            }
        } else {
            channel.sendMessage(new EmbedBuilder().setColor(Color.MAGENTA).setTitle(serverManager.getMessage("reactrole_help_title"))
                    .addField(serverManager.getMessage("reactrole_help_required_title"), serverManager.getMessage("reactrole_help_required_body"), true)
                    .addField(serverManager.getMessage("reactrole_help_possible_title"), serverManager.getMessage("reactrole_help_possible_body"), false).build()).queue();
        }
    }

    public void remove(TextChannel channel, Message message, List<String> emojis, TextChannel sendChannel) {
        final long messageID = utils.searchForMessageID(message.getContentRaw().split(" "));
        if(messageID != 0) {
            if(emojis.size() > 0) {
                sendChannel.retrieveMessageById(messageID).queue((botMessage -> {
                    for(String emoji : emojis) {
                        if(reactRoleManager.getReactFromMessage(messageID).contains(emoji)) {
                            botMessage.removeReaction(emoji).queue();
                            reactRoleManager.removeOneReactRole(messageID, emoji);
                        }
                    }
                    botMessage.delete().queue();
                }));

            } else {
                sendChannel.retrieveMessageById(messageID).queue((botMessage -> {
                    for(String emoji : reactRoleManager.getReactFromMessage(messageID)) {
                        botMessage.removeReaction(emoji).queue();
                    }
                    reactRoleManager.removeRoleMessage(messageID);
                    botMessage.delete().queue();
                }));
            }

        } else {
            channel.sendMessage(utils.createEmbed(serverManager.getMessage("reactrole_remove_error_title"), Color.RED,
                    serverManager.getMessage("reactrole_remove_error_body"))).queue();
        }
    }

    public void edit(TextChannel channel, Message message, TextChannel sendChannel) {
        final long messageID = utils.searchForMessageID(message.getContentRaw().split(" "));
        if(messageID != 0) {

        }
    }

    public boolean searchForEdit(String[] values) {
        for(String tocheck : values) {
            if(tocheck.equalsIgnoreCase("edit")) {
                return true;
            }
        }
        return false;
    }

    public boolean searchForRemove(String[] values) {
        for(String tocheck : values) {
            if(tocheck.equalsIgnoreCase("remove")) {
                return true;
            }
        }
        return false;
    }

    public boolean searchForBoolean(String[] values) {
        for(String tocheck : values) {
            if(checkBoolean(tocheck)) {
                return Boolean.valueOf(tocheck);
            }
        }
        return false;
    }

    public boolean checkBoolean(String value) {
        if(value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")) {
            return true;
        }
        return false;
    }

}
