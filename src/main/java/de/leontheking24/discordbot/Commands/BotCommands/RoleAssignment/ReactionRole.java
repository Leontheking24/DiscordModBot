package de.leontheking24.discordbot.Commands.BotCommands.RoleAssignment;

public class ReactionRole {

    private final long messageId;
    private final long roleId;
    private final String emoji;
    private final boolean isRemovable;

    public ReactionRole(long messageId, long roleId, String emoji, boolean isRemovable) {
        this.messageId = messageId;
        this.roleId = roleId;
        this.emoji = emoji;
        this.isRemovable = isRemovable;
    }

    public long getMessageId() {
        return messageId;
    }

    public long getRoleId() {
        return roleId;
    }

    public String getEmoji() {
        return emoji;
    }

    public boolean isRemovable() {
        return isRemovable;
    }
}
