package de.leontheking24.discordbot.Poll.Entitys;

import net.dv8tion.jda.api.entities.TextChannel;

public class PollChannelSearch {

    private final TextChannel sendChannel;
    private final String replaceArgs;

    public PollChannelSearch(TextChannel sendChannel, String replaceArgs) {
        this.sendChannel = sendChannel;
        this.replaceArgs = replaceArgs;
    }

    public TextChannel getSendChannel() {
        return sendChannel;
    }

    public String getReplaceArgs() {
        return replaceArgs;
    }
}
