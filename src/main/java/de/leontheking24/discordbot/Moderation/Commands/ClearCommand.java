package de.leontheking24.discordbot.Moderation.Commands;

import de.leontheking24.discordbot.Commands.Base.Command;
import de.leontheking24.discordbot.Commands.Base.CommandType;
import de.leontheking24.discordbot.ServerManager;
import de.leontheking24.discordbot.DiscordBot;
import de.leontheking24.discordbot.Utils.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageHistory;
import net.dv8tion.jda.api.entities.TextChannel;

import java.awt.*;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

public class ClearCommand extends Command {

    private final ServerManager serverManager;
    private final static String trigger = "Clear";
    private final Utils utils = DiscordBot.getUtils();
    private Message message;

    public ClearCommand(ServerManager serverManager) {
        super(serverManager, trigger, serverManager.getMessage("clear_description"), "command.clear", CommandType.Bot);
        this.serverManager = serverManager;
    }

    @Override
    public void executeCommand(TextChannel channel, Message message) {
        String[] args = message.getContentRaw().split(" ");
        if(args.length > 1) {
            int amount = searchForAmount(args);
            if(amount > 0) {
                if(amount < Integer.parseInt(serverManager.getConfigManager().getConfig("maximumClearAmount"))) {
                    List<Member> selectedMembers = message.getMentionedMembers();
                    List<TextChannel> selectedChannels = message.getMentionedChannels();
                    if(selectedChannels.size() == 0) {
                        selectedChannels = new ArrayList<>();
                        selectedChannels.add(channel);
                    }
                    this.message = message;
                    if(selectedMembers.size() > 0) {
                        deletedMessages(channel, selectedMembers, selectedChannels, amount);

                    } else {
                        deletedMessagesIgnorePlayer(channel, selectedChannels, amount);
                    }
                } else {
                    channel.sendMessage(new EmbedBuilder().setTitle(serverManager.getMessage("clear_secure_title")).setColor(Color.RED).setDescription(serverManager.getMessage("clear_secure_body")).build()).queue();
                    return;
                }
            }
        }
        channel.sendMessage(new EmbedBuilder().setTitle(serverManager.getMessage("clear_syntax_title")).setColor(Color.RED).setDescription(serverManager.getMessage("clear_syntax_body")).build()).queue();
    }

    public int searchForAmount(String[] args) {
        for(String arg : args) {
            if(utils.isNumeric(arg)) {
                return Integer.parseInt(arg);
            }
        }
        return 0;
    }

    public void deletedMessages(TextChannel senderChannel, List<Member> members, List<TextChannel> channels, int deleteAmount) {
        int deleteInt = 0;
        List<Member> deletedMembers = new ArrayList<>();
        String deleteMemberString = "";
        String deleteChannelString = "";
        for(TextChannel channel : channels) {
            deleteChannelString += channel.getAsMention() + ", ";
            int toDelete = deleteAmount;
            List<Message> messageList = new MessageHistory(channel).retrievePast(deleteAmount*10).complete();
            for(Message message : messageList) {
                if(!message.equals(this.message)) {
                    if(toDelete > 0) {
                        if(members.contains(message.getMember())) {
                            message.delete().queue();
                            toDelete--;
                            deleteInt++;
                            if(!deletedMembers.contains(message.getMember())) {
                                deletedMembers.add(message.getMember());
                                deleteMemberString += message.getMember().getAsMention() + ", ";
                            }
                        }
                    }
                }
            }
        }
        if(deleteInt > 0) {
            deleteChannelString = deleteChannelString.substring(0, deleteChannelString.length()-2);
            deleteMemberString = deleteMemberString.substring(0, deleteMemberString.length()-2);
            deleteMessage(deleteInt, deleteMemberString, deleteChannelString);
        } else {
            senderChannel.sendMessage(utils.createEmbed(serverManager.getMessage("clear_error_title"), Color.RED, serverManager.getMessage("clear_error_body"))).queue();
        }

    }

    public void deletedMessagesIgnorePlayer(TextChannel senderChannel, List<TextChannel> channels, int deleteAmount) {
        int deleteInt = 0;
        List<Member> deletedMembers = new ArrayList<>();
        String deleteMemberString = "";
        String deleteChannelString = "";
        for(TextChannel channel : channels) {
            deleteChannelString += channel.getAsMention() + ", ";
            List<Message> messageList = new MessageHistory(channel).retrievePast(deleteAmount+1).complete();
            for(Message message : messageList) {
                if(!message.equals(this.message)) {
                    deleteInt++;
                    message.delete().queue();
                    if(!deletedMembers.contains(message.getMember())) {
                        deletedMembers.add(message.getMember());
                        deleteMemberString += message.getMember().getAsMention() + ", ";
                    }
                }
            }
        }
        if(deleteInt > 0) {
            deleteChannelString = deleteChannelString.substring(0, deleteChannelString.length()-2);
            deleteMemberString = deleteMemberString.substring(0, deleteMemberString.length()-2);
            deleteMessage(deleteInt, deleteMemberString, deleteChannelString);
        } else {
            senderChannel.sendMessage(utils.createEmbed(serverManager.getMessage("clear_error_title"), Color.RED, serverManager.getMessage("clear_error_body"))).queue();
        }

    }

    public void deleteMessage(int messageAmount, String deletedMember, String deletedChannel) {
        serverManager.getModManager().getChannel().sendMessage(new EmbedBuilder().setColor(Color.RED)
                .setTitle(serverManager.getMessage("clear_message_title"))
                .setDescription(serverManager.getMessage("clear_message_description"))
                .addField(serverManager.getMessage("clear_message_user"), deletedMember, true)
                .addField(serverManager.getMessage("clear_message_reason_title"), serverManager.getMessage("clear_message_reason_body"), true)
                .addField(serverManager.getMessage("clear_message_time"),  DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT).format(new GregorianCalendar().getTime()), true)
                .addField(serverManager.getMessage("clear_message_channels"), deletedChannel, true)
                .addField(serverManager.getMessage("clear_message_counter"), String.valueOf(messageAmount), true)
                .build()).queue();
    }
}
