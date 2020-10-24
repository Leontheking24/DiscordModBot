package de.leontheking24.discordbot.Commands.Base;

import de.leontheking24.discordbot.ServerManager;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

public class Command implements ICommandExecute {

    private final ServerManager serverManager;
    private final CommandSqlManager sqlManager;
    private final String trigger;
    private String value;
    private String description;
    private String permission;
    private String info;
    private final CommandType commandType;

    public Command(ServerManager serverManager, String trigger, String value, String description, String permission, CommandType commandType) {
        this.serverManager = serverManager;
        sqlManager = serverManager.getCommandSqlManager();
        this.trigger = trigger;
        this.value = value;
        this.description = description;
        this.permission = permission;
        this.commandType = commandType;
    }

    public Command(ServerManager serverManager, String trigger, String description, String permission, CommandType commandType) {
        this.serverManager = serverManager;
        sqlManager = serverManager.getCommandSqlManager();
        this.trigger = trigger;
        this.description = description;
        this.permission = permission;
        this.commandType = commandType;
    }

    public Command(ServerManager serverManager, String trigger, String description, CommandType commandType) {
        this.serverManager = serverManager;
        sqlManager = serverManager.getCommandSqlManager();
        this.trigger = trigger;
        this.description = description;
        this.commandType = commandType;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public boolean hasValue() {
        return value != null;
    }

    public boolean hasPermission() {
        return permission != null;
    }

    public boolean hasInfo() {
        return info != null;
    }

    public String getTrigger() {
        return trigger;
    }

    public String getValue() {
        return value;
    }

    public String getDescription() {
        return description;
    }

    public String getPermission() {
        return permission;
    }

    public CommandType getCommandType() {
        return commandType;
    }

    public String getInfo() {
        return info;
    }

    @Override
    public void executeCommand(TextChannel channel, Message message) {
        String messageContext = value;
        if(value.contains("{Counter}")) {
            sqlManager.countUp(trigger);
            messageContext = messageContext.replace("{Counter}", String.valueOf(sqlManager.getCounter(trigger)));
        }
        if(value.contains("{Player}")) {
            String replacedPlayer = message.getMember().getEffectiveName();
            if(message.getMentionedMembers().size() > 0) {
                replacedPlayer = message.getMentionedMembers().get(0).getEffectiveName();
            }
            messageContext = messageContext.replace("{Player}", replacedPlayer);
        }
        channel.sendMessage(messageContext).queue();
    }

    @Override
    public boolean isArgumentLengthZero(Message message) {
        String messageWOPrefix = message.getContentRaw().substring(serverManager.getBotCommandPrefix().length() + trigger.length());
        return messageWOPrefix.length() == 0;
    }

    @Override
    public String[] getArguments(Message message) {
        return message.getContentRaw().substring(serverManager.getBotCommandPrefix().length() + trigger.length() +1).split(" ");
    }

}
