package de.leontheking24.discordbot.Database;

import de.leontheking24.discordbot.Commands.Base.Command;
import de.leontheking24.discordbot.Commands.Base.CommandType;
import de.leontheking24.discordbot.ServerManager;
import de.leontheking24.discordbot.Commands.Base.CommandManager;
import de.leontheking24.discordbot.DiscordBot;
import de.leontheking24.discordbot.Utils.Utils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class SQLManager {

    private ServerManager serverManager;
    private MySql mySql;
    private Utils utils = DiscordBot.getUtils();

    public SQLManager(ServerManager serverManager) {
        this.serverManager = serverManager;
        mySql = serverManager.getMySql();

        mySql.execute("CREATE TABLE IF NOT EXISTS Commands(trigger_word VARCHAR(100) NOT NULL PRIMARY KEY, value VARCHAR(255) NOT NULL, description VARCHAR(255) NOT NULL, permission VARCHAR(50), type VARCHAR(30) NOT NULL)");
        mySql.execute("CREATE TABLE IF NOT EXISTS Counter(name VARCHAR(100) NOT NULL PRIMARY KEY, value INTEGER)");
        mySql.execute("CREATE TABLE IF NOT EXISTS BlackListedWords(value VARCHAR(100) NOT NULL PRIMARY KEY)");
        mySql.execute("CREATE TABLE IF NOT EXISTS Config (configKey VARCHAR(100) PRIMARY KEY NOT NULL, configValue VARCHAR(100) NOT NULL, configDataTypeClass VARCHAR(100) NOT NULL)");
        mySql.execute("CREATE TABLE IF NOT EXISTS MutedPlayers (playerId bigint PRIMARY KEY NOT NULL, muteEnd datetime NOT NULL, reason VARCHAR(200))");
    }

    public void addCommand(Command command) {
        ResultSet resultSet = mySql.executeWithResult("SELECT EXISTS(SELECT 1 FROM Commands WHERE trigger_word='" + command.getTrigger() + "' LIMIT 1) AS CommandExists");
        if(!utils.isResultTrue(resultSet, "CommandExists")) {
            mySql.execute("INSERT INTO Commands (trigger_word, value, description, type) VALUES ('" + command.getTrigger() + "', '" + command.getValue() + "', '" + command.getDescription() + "', '"
                    + command.getCommandType() + "')");
            if(command.hasPermission()) {
                mySql.execute("INSERT INTO Commands (permission) VALUES ('" + command.getPermission().toLowerCase() + "') WHERE trigger_word='" + command.getTrigger() + "'");
            }
            if(command.getValue().contains("{Counter}")) {
                mySql.execute("INSERT INTO Counter (name, value) VALUES ('" + command.getTrigger() + "', 0)");
            }
            DiscordBot.getLogger().log(Level.INFO, "The command '" + command.getTrigger() + "' was added at the server with id " + serverManager.getServerId());
        }
    }

    public void deleteCommand(String commandTrigger) {
        mySql.execute("DELETE FROM Commands WHERE trigger_word='" + commandTrigger + "'");
        mySql.execute("DELETE FROM Counter WHERE name='" + commandTrigger + "'");
    }

    public int getCounter(String counterName) {
        ResultSet resultSet = mySql.executeWithResult("SELECT * FROM Counter WHERE name='" + counterName + "'");
        try {
            while (resultSet.next()) {
                return resultSet.getInt("value");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        mySql.execute("INSERT INTO Counter (name, value) VALUES ('" + counterName + "', 1)");
        return 1;
    }

    public void countUp(String counterName) {
        mySql.execute("UPDATE Counter SET value = value + 1 WHERE name = '" + counterName + "'");
    }

    public void insertCommands() {
        ResultSet result = mySql.executeWithResult("SELECT * FROM Commands ORDER BY trigger_word ASC");
        List<Command> commandList = new ArrayList<>();
        try {
            while (result.next()) {
                Command command = new Command(serverManager, result.getString("trigger_word"), result.getString("value"),
                        result.getString("description"), null, CommandType.User);
                commandList.add(command);
                if(result.getString("permission") != null) {
                    command.setPermission(result.getString("permission"));
                }
                serverManager.getCommandManager().addCommand(command);
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void loadConfig() {
        ResultSet resultSet = mySql.executeWithResult("SELECT * FROM Config");
        try {
            while (resultSet.next()) {
                serverManager.getConfigManager().addConfig(resultSet.getString("configKey"), resultSet.getString("configValue"), resultSet.getString("configDataTypeClass"));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
