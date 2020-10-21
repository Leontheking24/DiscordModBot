package de.leontheking24.discordbot.Moderation.Listener;

import de.leontheking24.discordbot.ServerManager;
import de.leontheking24.discordbot.DiscordBot;
import de.leontheking24.discordbot.Moderation.Manager.SpamManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageHistory;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;
import java.text.DateFormat;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

public class SpamSecure extends ListenerAdapter {

    private ServerManager serverManager;
    private SpamManager spamManager;

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        if(!event.getMember().getUser().isBot()) {
            serverManager = DiscordBot.getServerManager(event.getGuild().getIdLong());
            spamManager = serverManager.getSpamManager();

            if(Boolean.parseBoolean(serverManager.getConfigManager().getConfig("enableSpamProtection"))) {
                Member member = event.getMember();
                if(serverManager.getPermissionManager().playerHasPermission(member.getIdLong(), "events.spam.ignore")) {
                    if(!member.getUser().isBot()) {
                        if(spamManager.isMemberDetective(member)) {
                            try {
                                event.getMessage().delete().queue();
                                spamManager.updateCooldown(member, spamManager.getSpamCooldown());
                                spamManager.updateMessageAmount(member, spamManager.getMessageAmount(member)+1);
                                updateSpamMessage(member, event.getChannel(), event.getMessage().getContentRaw(), spamManager.getMessageAmount(member));
                            } catch (Exception e) {
                            }

                        } else {
                            List<Message> deleteMessage = createUserList(member.getId(), event);
                            boolean couldDelete = false;

                            if(deleteMessage.size() >= spamManager.getMessagesInTime()) {
                                for(Message deleteMsg : deleteMessage) {
                                    try {
                                        deleteMsg.delete().queue();
                                        couldDelete = true;
                                    } catch (Exception e) {
                                    }
                                }
                                if(couldDelete) {
                                    serverManager.getNotificationChannel().sendMessage(SpamMessage(member, event.getChannel(), deleteMessage.get(deleteMessage.size()-1).getContentRaw(),
                                            deleteMessage.size()).build()).queue(message -> spamManager.addDetectiveMember(member, deleteMessage.size(), message.getIdLong()));
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public List<Message> createUserList(String authorID, GuildMessageReceivedEvent event) {
        List<Message> messageList = new MessageHistory(event.getChannel()).retrievePast(30).complete();
        List<Message> deleteMessage = new ArrayList<>();
        OffsetDateTime compareTime = event.getMessage().getTimeCreated().minusSeconds(spamManager.getMessageTime());

        for(Message msgFromHistory : messageList) {
            if(authorID.equals(msgFromHistory.getAuthor().getId())) {
                if(compareTime.compareTo(msgFromHistory.getTimeCreated()) < 0) {
                    deleteMessage.add(msgFromHistory);
                }
            }
        }
        return deleteMessage;
    }

    public EmbedBuilder SpamMessage(Member author, TextChannel channel, String lastMessage, int amount) {
        return new EmbedBuilder().setTitle(serverManager.getMessage("spam_message_title")).setColor(Color.RED)
                .addField(serverManager.getMessage("spam_message_user"), author.getAsMention(), true)
                .addField(serverManager.getMessage("spam_message_channel"), "<#" + channel.getId() + ">", true)
                .addField(serverManager.getMessage("spam_message_time"),  DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT).format(new GregorianCalendar().getTime()), true)
                .addField(serverManager.getMessage("spam_message_reason"), "Spam", true)
                .addField(serverManager.getMessage("spam_message_counter"), String.valueOf(amount), true)
                .addField(serverManager.getMessage("spam_message_message"), lastMessage, true);
    }

    public void updateSpamMessage(Member author, TextChannel channel, String lastMessage, int amount) {
        serverManager.getNotificationChannel().retrieveMessageById(spamManager.getAnnouncmentId(author)).complete().editMessage(
                SpamMessage(author, channel, lastMessage, amount).build()).queue();

    }


}
