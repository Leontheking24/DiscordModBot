package de.leontheking24.discordbot.Commands.BotCommands.RoleAssignment;

import de.leontheking24.discordbot.Database.MySql;
import de.leontheking24.discordbot.DiscordBot;
import de.leontheking24.discordbot.ServerManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class ReactRoleManager {

    private ServerManager serverManager;
    private MySql mySql;
    private List<ReactionRole> reactionRoleList;

    public ReactRoleManager(ServerManager serverManager) {
        this.serverManager = serverManager;
        mySql = serverManager.getMySql();
        mySql.execute("CREATE TABLE IF NOT EXISTS ReactRoles(id INTEGER PRIMARY KEY AUTO_INCREMENT, messageID bigint NOT NULL, roleID bigint NOT NULL, reaction VARCHAR(50) NOT NULL, removable BOOLEAN)");
        initRoles();
    }

    public void addReactRole(long messageID, long roleID, String reaction, boolean removable) {
        mySql.execute("INSERT INTO ReactRoles (messageID, roleID, reaction, removable) VALUES ('" + messageID + "', '" + roleID + "', '" + reaction + "', '" + (removable ? 1:0) + "')");
    }

    public boolean roleMessageExists(long messageID) {
        ResultSet resultSet = mySql.executeWithResult("SELECT * FROM ReactRoles WHERE messageID='" + messageID + "'");
        if(resultSet == null) {
            return false;
        } else {
            return true;
        }
    }

    public void removeRoleMessage(long messageID) {
        mySql.execute("DELETE FROM ReactRoles WHERE messageID='" + messageID + "'");
    }

    public void removeOneReactRole(long messageID, String react) {
        mySql.execute("DELETE FROM ReactRoles WHERE messageID='" + messageID + "' AND reaction='" + react + "'");
    }

    public long getRoleFromMessageAndReact(long messageID, String reaction) {
        ResultSet resultSet = mySql.executeWithResult("SELECT roleID FROM ReactRoles WHERE messageID='" + messageID + "' AND reaction='" + reaction + "'");
        try {
            while (resultSet.next())
                return resultSet.getLong("roleID");
        } catch (SQLException e) {
            DiscordBot.getLogger().log(Level.WARNING, e.getMessage());
        }
        return 0;
    }

    public List<String> getReactFromMessage(long messageID) {
        ResultSet resultSet = mySql.executeWithResult("SELECT reaction FROM ReactRoles WHERE messageID='" + messageID + "'");
        List<String> resultList = new ArrayList<>();
        try {
            while (resultSet.next()) {
                resultList.add(resultSet.getString("reaction"));
            }
        } catch (SQLException e) {
            DiscordBot.getLogger().log(Level.WARNING, e.getMessage());
        }
        return resultList;
    }

    public boolean isRoleRemovable(long messageID, String reaction) {
        ResultSet resultSet = mySql.executeWithResult("SELECT removable FROM ReactRoles WHERE messageID='" + messageID + "' AND reaction='" + reaction + "'");
        try {
            while (resultSet.next())
                return Boolean.valueOf(resultSet.getString("removable"));
        } catch (SQLException e) {
            DiscordBot.getLogger().log(Level.WARNING, e.getMessage());
        }
        return false;
    }

    private void initRoles() {
        reactionRoleList = new ArrayList<>();
        ResultSet resultSet = mySql.executeWithResult("SELECT * FROM ReactRoles");
        try {
            while (resultSet.next()) {
                reactionRoleList.add(new ReactionRole(resultSet.getLong("messageID"), resultSet.getLong("roleID"),
                        resultSet.getString("reaction"), resultSet.getBoolean("removable")));
            }
        } catch (SQLException throwables) {
            DiscordBot.getLogger().log(Level.WARNING, throwables.getMessage());
        }
    }

    public void saveReactRoles() {
        mySql = serverManager.getMySql();
        mySql.execute("CREATE TABLE IF NOT EXISTS ReactRoles(id INTEGER PRIMARY KEY AUTO_INCREMENT, messageID bigint NOT NULL, roleID bigint NOT NULL, reaction VARCHAR(50) NOT NULL, removable BOOLEAN)");
        for(ReactionRole reactionRole : reactionRoleList) {
            addReactRole(reactionRole.getMessageId(), reactionRole.getRoleId(), reactionRole.getEmoji(), reactionRole.isRemovable());
        }
    }

    public void removeList() {
        reactionRoleList = null;
    }
}
