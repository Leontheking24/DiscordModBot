package de.leontheking24.discordbot.Moderation.Manager;

import de.leontheking24.discordbot.DiscordBot;
import de.leontheking24.discordbot.ServerManager;
import de.leontheking24.discordbot.Utils.Utils;
import net.dv8tion.jda.api.entities.TextChannel;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BlacklistManager {

    private final ServerManager serverManager;
    private final Utils utils = DiscordBot.getUtils();
    private final List<String> blacklist;

    public BlacklistManager(ServerManager serverManager) {
        this.blacklist = new ArrayList<>();
        this.serverManager = serverManager;
        initBlacklist();

    }

    public List<String> getBlackList() {
        return blacklist;
    }

    public void addToBlackList(String toAdd) {
        if(!isBlacklisted(toAdd)) {
            blacklist.add(toAdd);
            serverManager.getMySql().execute("INSERT INTO BlackListedWords (value) VALUES ('" + toAdd + "')");
        }
    }

    public void removeFromBlackList(String toRemove) {
        if(isBlacklisted(toRemove)) {
            blacklist.remove(toRemove);
            serverManager.getMySql().execute("REMOVE FROM BlackListedWords WHERE value='" + toRemove + "'");
        }
    }

    public boolean isBlacklisted(String toCheck) {
        return blacklist.contains(toCheck);
    }

    private void initBlacklist() {
        ResultSet resultSet = serverManager.getMySql().executeWithResult("SELECT * FROM BlackListedWords");
        try {
            while (resultSet.next()) {
                blacklist.add(resultSet.getString("value"));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void saveBlacklist() {
        for(String word : blacklist) {
            ResultSet resultSet = serverManager.getMySql().executeWithResult("SELECT EXISTS(SELECT 1 FROM BlackListedWords WHERE value='" + word + "' LIMIT 1) AS isBlacklisted");
            if(!utils.isResultTrue(resultSet, "isBlacklisted")) {
                serverManager.getMySql().execute("INSERT INTO BlackListedWords (value) VALUES ('" + word + "')");
            }
        }
    }


}
