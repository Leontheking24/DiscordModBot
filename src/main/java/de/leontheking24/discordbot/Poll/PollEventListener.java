package de.leontheking24.discordbot.Poll;

import de.leontheking24.discordbot.DiscordBot;
import de.leontheking24.discordbot.Poll.Entitys.Poll;
import de.leontheking24.discordbot.ServerManager;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
import java.util.List;

public class PollEventListener extends ListenerAdapter {


    @Override
    public void onMessageReactionAdd(@Nonnull MessageReactionAddEvent event) {
        ServerManager serverManager = DiscordBot.getServerLists().getServerManager(event.getGuild().getIdLong());
        if(Boolean.valueOf(serverManager.getConfigManager().getConfig("enablePollCommand"))) {
            PollManager pollManager = serverManager.getPollManager();
            long messageID = event.getMessageIdLong();

            if(pollManager.isMessageAPoll(messageID)) {
                Poll poll = pollManager.getPoll(messageID);
                Message message = event.getChannel().retrieveMessageById(messageID).complete();
                List<MessageReaction> reactions = message.getReactions();

                if(!event.getUser().isBot()) {
                    if(!poll.isReactionFromPoll(event.getReactionEmote().getName())) {
                        for(int i = 0; i < reactions.size(); i++) {
                            if(reactions.get(i).getReactionEmote().equals(event.getReactionEmote())) {
                                reactions.get(i).removeReaction(event.getUser()).queue();
                            }
                        }

                    } else {
                        for(int i = 0; i < reactions.size(); i++) {
                            MessageReaction messageReaction = reactions.get(i);
                            if(messageReaction.retrieveUsers().complete().contains(event.getUser())) {
                                if(!event.getReactionEmote().equals(messageReaction.getReactionEmote())) {
                                    messageReaction.removeReaction(event.getUser()).queue();
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onMessageDelete(@Nonnull MessageDeleteEvent event) {
        ServerManager serverManager = DiscordBot.getServerLists().getServerManager(event.getGuild().getIdLong());
        PollManager pollManager = serverManager.getPollManager();

        long messageID = event.getMessageIdLong();
        if(pollManager.isMessageAPoll(messageID)) {
            pollManager.getPoll(messageID).remove();
        }
    }
}
