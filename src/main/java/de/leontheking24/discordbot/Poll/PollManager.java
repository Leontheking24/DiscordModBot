package de.leontheking24.discordbot.Poll;

import de.leontheking24.discordbot.DiscordBot;
import de.leontheking24.discordbot.Poll.Entitys.Poll;
import de.leontheking24.discordbot.ServerManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Timer;
import java.util.logging.Level;

public class PollManager {

    private ServerManager serverManager;

    private HashMap<Long, Poll> pollList;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
    private boolean willPollDeleteAfterFinish;
    private String seperateKey;

    public PollManager(ServerManager serverManager) {
        this.serverManager = serverManager;
        pollList = new HashMap<>();
        willPollDeleteAfterFinish = Boolean.valueOf(serverManager.getConfigManager().getConfig("deletePollAfterFinish"));
        seperateKey = serverManager.getConfigManager().getConfig("seperateKey");
    }

    public boolean isMessageAPoll(long messageId) {
        return pollList.containsKey(messageId);
    }

    public void addPoll(Poll poll) {
        pollList.put(poll.getMessageId(), poll);
    }

    public void removePoll(long messageId) {
        pollList.remove(messageId);
    }

    public Poll getPoll(long messageId) {
        return pollList.get(messageId);
    }

    public HashMap<Long, Poll> getPollList() {
        return pollList;
    }

    public void checkEndTimer() {
        if(pollList.values().size() > 0) {
            for(long pollId : pollList.keySet()) {
                Poll poll = serverManager.getPollManager().getPoll(pollId);
                if(poll.hasEndDate()) {
                    Date endDate = poll.getEndDate();
                    Date now = new Date();
                    if(endDate.before(now) || dateFormat.format(endDate).equals(dateFormat.format(now))) {
                        DiscordBot.getLogger().log(Level.INFO, "Try to delete poll with id '" + poll.getMessageId() + "'");
                        poll.finishPoll();
                    }
                }
            }
        }
    }

    public boolean willPollDeleteAfterFinish() {
        return this.willPollDeleteAfterFinish;
    }

    public String getSeperateKey() {
        return seperateKey;
    }

    public void savePolls() {
        PollSqlManager sqlManager = serverManager.getPollSqlManager();

        for(Poll poll : pollList.values()) {
            if(!sqlManager.isPollExists(poll.getMessageId())) {
                poll.initToDatebase();
            }
        }
    }

}
