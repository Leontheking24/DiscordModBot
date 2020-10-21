package de.leontheking24.discordbot.Poll.Entitys;

import de.leontheking24.discordbot.DiscordBot;
import de.leontheking24.discordbot.Poll.PollManager;
import de.leontheking24.discordbot.ServerManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;

import java.awt.*;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;

public class Poll {

    private final ServerManager serverManager;
    private final PollManager pollManager;

    private final long messageId;
    private final long sendChannelId;
    private List<String> reactions;
    private Date endDate = null;
    private final String[] emotes = new String[]{":zero:", ":one:", ":two:", ":three:", ":four:", ":five:", ":six:", ":seven:", ":eight:", ":nine:"};

    public Poll(ServerManager serverManager, long messageId, long sendChannelId) {
        this.serverManager = serverManager;
        this.pollManager = serverManager.getPollManager();
        this.messageId = messageId;
        this.sendChannelId = sendChannelId;
    }


    public Poll(ServerManager serverManager, long messageId, long sendChannelId, List<String> reactions) {
        this.serverManager = serverManager;
        this.pollManager = serverManager.getPollManager();
        this.messageId = messageId;
        this.sendChannelId = sendChannelId;
        this.reactions = reactions;
    }

    public Poll(ServerManager serverManager, long messageId, long sendChannelId, List<String> reactions, Date endDate) {
        this.serverManager = serverManager;
        this.pollManager = serverManager.getPollManager();
        this.messageId = messageId;
        this.sendChannelId = sendChannelId;
        this.reactions = reactions;
        this.endDate = endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public void setReactions(List<String> reactions) {
        this.reactions = reactions;
    }

    public boolean hasEndDate() {
        return endDate != null;
    }

    public boolean isReactionFromPoll(String reaction) {
        reaction = new Activity.Emoji(reaction).toString().split("\\(")[1].replace(")", "");
        for (String react : reactions) {
            if(react.equals(reaction)) {
                return true;
            }
        }
        return false;
    }

    public long getMessageId() {
        return messageId;
    }

    public List<String> getReactions() {
        return reactions;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void initToDatabase() {
        pollManager.addPoll(this);
        if(hasEndDate()) {
            serverManager.getPollSqlManager().insertPoll(messageId, sendChannelId, reactions, endDate);
        } else {
            serverManager.getPollSqlManager().insertPoll(messageId, sendChannelId, reactions);
        }
    }

    public void remove() {
        serverManager.getPollSqlManager().removePoll(messageId);
        pollManager.removePoll(messageId);
        DiscordBot.getLogger().log(Level.INFO, "The Poll with the messageID '" + messageId + "' was deleted successful");
    }


    public void finishPoll() {
        EmbedBuilder embedMessage = new EmbedBuilder();
        embedMessage.setColor(Color.MAGENTA);
        embedMessage.setTitle(serverManager.getMessage("poll_finished_title"));

        Message message = Objects.requireNonNull(DiscordBot.getJda().getTextChannelById(sendChannelId)).retrieveMessageById(messageId).complete();
        List<MessageReaction> reactions = message.getReactions();
        List<MessageEmbed.Field> fields = message.getEmbeds().get(0).getFields();
        int mostVotes = 0;

        for(int i = 0; i < fields.size(); i++) {
            embedMessage.addField(emotes[i+1] + " > " + Objects.requireNonNull(fields.get(i).getValue()).substring(2), "> " + reactions.get(i).getCount(), false);
            if(reactions.get(i).getCount() > reactions.get(mostVotes).getCount()) {
                mostVotes = i;
            }
        }
        embedMessage.setDescription(serverManager.getMessage("poll_finished_message").replace("{Emoji}", emotes[mostVotes+1]).replace("{Winner}", Objects.requireNonNull(fields.get(mostVotes).getValue()).substring(2)));
        Objects.requireNonNull(DiscordBot.jda.getTextChannelById(sendChannelId)).sendMessage(embedMessage.build()).queue();
        remove();

        if(pollManager.willPollDeleteAfterFinish()) {
            Objects.requireNonNull(DiscordBot.jda.getTextChannelById(sendChannelId)).retrieveMessageById(messageId).complete().delete().queue();
        }
    }

}
