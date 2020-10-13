package de.leontheking24.discordbot.Commands.Base;

import de.leontheking24.discordbot.ServerManager;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class CommandManager {

    private final ServerManager serverManager;
    private final TreeMap<String, Command> commandList;

    public CommandManager(ServerManager serverManager) {
        this.serverManager = serverManager;
        commandList = new TreeMap<>();
    }

    public void addCommand(Command command) {
        commandList.put(command.getTrigger().toUpperCase(), command);
    }

    public void deleteCommand(Command command) {
        commandList.remove(command.getTrigger().toUpperCase());
        serverManager.getCommandSqlManager().deleteCommand(command.getTrigger());
    }

    public void updateCommand(Command command) {
        commandList.replace(command.getTrigger(), command);
        serverManager.getCommandSqlManager().updateCommand(command);
    }

    public boolean isCommandExists(String trigger) {
        return commandList.containsKey(trigger.toUpperCase());
    }

    public Command getCommand(String trigger) {
        return commandList.get(trigger.toUpperCase());
    }

    public List<Command> getBotCommandList() {
        List<Command> commands = new ArrayList<>();
        for(Command command : commandList.values()) {
            if(command.getCommandType().equals(CommandType.Bot)) {
                commands.add(command);
            }
        }
        return commands;
    }

    public List<Command> getUserCommandList() {
        List<Command> commands = new ArrayList<>();
        for(Command command : commandList.values()) {
            if(command.getCommandType().equals(CommandType.User)) {
                commands.add(command);
            }
        }
        return commands;
    }

    public List<Command> getCommandListTypSorted() {
        List<Command> commands = getBotCommandList();
        commands.addAll(getUserCommandList());
        return commands;
    }

    public void initToDatabase() {
        for(Command command : commandList.values()) {
            serverManager.getCommandSqlManager().addCommand(command);
        }
    }
}