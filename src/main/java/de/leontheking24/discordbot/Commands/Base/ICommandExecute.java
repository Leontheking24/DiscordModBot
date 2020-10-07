package de.leontheking24.discordbot.Commands.Base;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

public interface ICommandExecute {

    void executeCommand(TextChannel textChannel, Message message);
    boolean isArgumentLengthZero(Message message);
    String[] getArguments(Message message);

}
