package de.leontheking24.discordbot.Moderation;

import java.util.Date;

public class Mute {

    private final long playerId;
    private final Date muteEnd;
    private String reason = "Kein Grund angegeben";

    public Mute(long playerId, Date muteEnd) {
        this.playerId = playerId;
        this.muteEnd = muteEnd;
    }

    public Mute(long playerId, Date muteEnd, String reason) {
        this.playerId = playerId;
        this.muteEnd = muteEnd;
        if(reason != null) {
            this.reason = reason;
        }
    }

    public long getPlayerId() {
        return playerId;
    }

    public Date getMuteEnd() {
        return muteEnd;
    }

    public String getReason() {
        return reason;
    }
}
