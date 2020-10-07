package de.leontheking24.discordbot.Permissions;

import de.leontheking24.discordbot.Commands.Base.Command;
import de.leontheking24.discordbot.Database.MySql;
import de.leontheking24.discordbot.DiscordBot;
import de.leontheking24.discordbot.ServerManager;
import de.leontheking24.discordbot.Utils.Utils;
import net.dv8tion.jda.api.entities.Role;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

public class PermissionManager {

    private ServerManager serverManager;
    private MySql mySql;
    private final Utils utils = DiscordBot.getUtils();

    private HashMap<Long, List<String>> playerPermissions;
    private HashMap<Long, List<String>> groupPermissions;

    public PermissionManager(ServerManager serverManager) {
        this.serverManager = serverManager;
        mySql = serverManager.getMySql();

        mySql.execute("CREATE TABLE IF NOT EXISTS PlayerPermissions(playerId bigint NOT NULL, permission VARCHAR(100) NOT NULL)");
        mySql.execute("CREATE TABLE IF NOT EXISTS GroupPermissions(groupId bigint NOT NULL, permission VARCHAR(100) NOT NULL)");
        savePermissions();
    }

    public void updateManager(ServerManager serverManager) {
        this.serverManager = serverManager;
        mySql = serverManager.getMySql();
    }

    public void groupAddPermission(long groupId, String permission) {
        if(!groupHasPermission(groupId, permission)) {
            mySql.execute("INSERT INTO GroupPermissions(groupId, permission) VALUES ('" + groupId + "', '" + permission.toLowerCase() + "')");
        }
    }

    public void playerAddPermission(long playerID, String permission) {
        if(!playerHasPermission(playerID, permission)) {
            mySql.execute("INSERT INTO PlayerPermissions(playerId, permission) VALUES ('" + playerID + "', '" + permission.toLowerCase() + "')");
        }
    }

    public void groupRemovePermission(long groupId, String permission) {
        mySql.execute("DELETE FROM GroupPermissions WHERE groupId='" + groupId + "' AND permission='" + permission + "'");
    }

    public void playerRemovePermission(long playerId, String permission) {
        mySql.execute("DELETE FROM PlayerPermissions WHERE playerId='" + playerId + "' AND permission='" + permission + "'");
    }


    public boolean playerHasPermission(long playerId, String permission) {
        if(playerHasAsteriskPermission(playerId)) {
            return true;

        } else if(playerPermissionCheck(playerId, permission)) {
            return true;

        } else {
            String[] seperatedPermission = permission.split("\\.");
            for(String selector : seperatedPermission) {
                if(playerPermissionCheck(playerId, selector + ".*")) {
                    return true;
                }
            }
            for(Role role : serverManager.getGuild().getMemberById(playerId).getRoles()) {
                if(groupHasPermission(role.getIdLong(), permission)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean groupHasPermission(long groupId, String permission) {
        if(groupHasAsteriskPermission(groupId)) {
            return true;

        } else if(groupPermissionCheck(groupId, permission)) {
            return true;

        } else {
            String[] seperatedPermission = permission.split("\\.");
            for(String selector : seperatedPermission) {
                if(groupPermissionCheck(groupId, selector + ".*")) {
                    return true;
                }
            }
        }
        return false;
    }

    public List<String> getAllPlayerPermissions(long playerId) {
        ResultSet resultSet = mySql.executeWithResult("SELECT * FROM PlayerPermissions WHERE playerId='" + playerId + "'");
        List<String> permissions = new ArrayList<>();
        try {
            while (resultSet.next()) {
                permissions.add(resultSet.getString("permission"));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return permissions;
    }

    private boolean playerHasAsteriskPermission(long playerId) {
        return playerPermissionCheck(playerId, "*");
    }

    private boolean playerPermissionCheck(long playerId, String permission) {
        ResultSet resultSet = mySql.executeWithResult("SELECT EXISTS(SELECT 1 FROM PlayerPermissions WHERE playerId='" + playerId + "' AND permission='" + permission.toLowerCase() + "' LIMIT 1) " +
                "AS PlayerHasPermission");
        return utils.isResultTrue(resultSet, "PlayerHasPermission");

    }

    public List<String> getAllGroupPermissions(long groupId) {
        ResultSet resultSet = mySql.executeWithResult("SELECT * FROM GroupPermissions WHERE groupId='" + groupId + "'");
        List<String> permissions = new ArrayList<>();
        try {
            while (resultSet.next()) {
                permissions.add(resultSet.getString("permission"));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return permissions;
    }

    private boolean groupHasAsteriskPermission(long groupId) {
        return groupPermissionCheck(groupId, "*");
    }

    private boolean groupPermissionCheck(long groupId, String permission) {
        ResultSet resultSet = mySql.executeWithResult("SELECT EXISTS(SELECT 1 FROM GroupPermissions WHERE groupId='" + groupId + "' AND permission='" + permission.toLowerCase() + "' LIMIT 1) " +
                "AS GroupHasPermission");
        return utils.isResultTrue(resultSet, "GroupHasPermission");

    }

    private void savePermissions() {
        playerPermissions = new HashMap<>();
        groupPermissions = new HashMap<>();

        ResultSet resultSet = mySql.executeWithResult("SELECT * FROM PlayerPermissions");
        try {
            while (resultSet.next()) {
                long playerId = resultSet.getLong("playerId");
                String permission = resultSet.getString("permission");
                if(!playerPermissions.containsKey(playerId)) {
                    playerPermissions.put(playerId, new ArrayList<>());
                }
                List<String> permissions = playerPermissions.get(playerId);
                permissions.add(permission);
                playerPermissions.put(playerId, permissions);
            }
        } catch (SQLException throwables) {
            DiscordBot.getLogger().log(Level.WARNING, throwables.getMessage());
        }

        resultSet = mySql.executeWithResult("SELECT * FROM GroupPermissions");
        try {
            while (resultSet.next()) {
                long groupId = resultSet.getLong("groupId");
                String permission = resultSet.getString("permission");
                if(!groupPermissions.containsKey(groupId)) {
                    groupPermissions.put(groupId, new ArrayList<>());
                }
                List<String> permissions = groupPermissions.get(groupId);
                permissions.add(permission);
                groupPermissions.put(groupId, permissions);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void initPermissions() {
        mySql.execute("CREATE TABLE IF NOT EXISTS PlayerPermissions(playerId bigint NOT NULL, permission VARCHAR(100) NOT NULL)");
        mySql.execute("CREATE TABLE IF NOT EXISTS GroupPermissions(groupId bigint NOT NULL, permission VARCHAR(100) NOT NULL)");
        for(long playerId : playerPermissions.keySet()) {
            for(String permission : playerPermissions.get(playerId)) {
                playerAddPermission(playerId, permission);
            }
        }

        for(long groupId : groupPermissions.keySet()) {
            for(String permission : groupPermissions.get(groupId)) {
                groupAddPermission(groupId, permission);
            }
        }
        playerPermissions = null;
        groupPermissions = null;
    }
}
