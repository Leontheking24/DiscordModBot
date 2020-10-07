package de.leontheking24.discordbot.Moderation.Manager;

import de.leontheking24.discordbot.ServerManager;
import net.dv8tion.jda.api.entities.Member;
import java.util.*;

public class SpamManager {

    private final Timer timer;
    private final HashMap<Member, Integer> detectiveMemberList;
    private final HashMap<Member, Integer> amountOfMessages;
    private final HashMap<Member, Long> announcementId;
    private final ServerManager serverManager;

    public SpamManager(ServerManager serverManager) {
        this.serverManager = serverManager;
        timer = new Timer();
        detectiveMemberList = new HashMap<>();
        amountOfMessages = new HashMap<>();
        announcementId = new HashMap<>();
        startTimer();
    }

    public boolean isMemberDetective(Member member) {
        return detectiveMemberList.containsKey(member);
    }

    public int getMessageAmount(Member member) {
        return amountOfMessages.get(member);
    }

    public long getAnnouncmentId(Member member) {
        return announcementId.get(member);
    }

    public void addDetectiveMember(Member member, int messageAmount, long announcmentId) {
        detectiveMemberList.put(member, 20);
        amountOfMessages.put(member, messageAmount);
        this.announcementId.put(member, announcmentId);
    }

    public void updateCooldown(Member member, int cooldown) {
        detectiveMemberList.replace(member, getCooldown(member), cooldown);
    }

    public void updateMessageAmount(Member member, int amount) {
        amountOfMessages.replace(member, getMessageAmount(member), amount);
    }

    public int getCooldown(Member member) {
        return detectiveMemberList.get(member);
    }

    public void removeDetectiveMember(Member member) {
        detectiveMemberList.remove(member);
        amountOfMessages.remove(member);
        announcementId.remove(member);
    }

    public int getSpamCooldown() {
        return Integer.parseInt(serverManager.getConfigManager().getConfig("spamCooldown"));
    }

    public int getMessagesInTime() {
        return Integer.parseInt(serverManager.getConfigManager().getConfig("messagesInTime"));
    }

    public int getMessageTime() {
        return Integer.parseInt(serverManager.getConfigManager().getConfig("messageTime"));
    }

    public void startTimer() {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                for(Member member : detectiveMemberList.keySet()) {
                    updateCooldown(member, getCooldown(member)-1);
                    if(getCooldown(member) <= 0) {
                        removeDetectiveMember(member);
                    }
                }
            }
        }, 0, 1000);
    }

}
