package de.leontheking24.discordbot.Poll.Entitys;

public class PollMessageSearch {

    private long messageId;
    private String replaceArgs;

    public PollMessageSearch(long messageId, String replaceArgs) {
        this.messageId = messageId;
        this.replaceArgs = replaceArgs;
    }

    public long getMessageId() {
        return messageId;
    }

    public String getReplaceArgs() {
        return replaceArgs;
    }
}
