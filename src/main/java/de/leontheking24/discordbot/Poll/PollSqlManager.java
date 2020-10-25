package de.leontheking24.discordbot.Poll;

import de.leontheking24.discordbot.Database.MySql;
import de.leontheking24.discordbot.DiscordBot;
import de.leontheking24.discordbot.Poll.Entitys.Poll;
import de.leontheking24.discordbot.ServerManager;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;

public class PollSqlManager {

    private final ServerManager serverManager;
    private final PollManager pollManager;
    private final MySql mySql;

    public PollSqlManager(ServerManager serverManager) {
        this.serverManager = serverManager;
        pollManager = serverManager.getPollManager();
        mySql = serverManager.getMySql();

        mySql.execute("CREATE TABLE IF NOT EXISTS Polls (messageID bigint PRIMARY KEY NOT NULL, channelID long NOT NULL, endDate datetime)");
        mySql.execute("CREATE TABLE IF NOT EXISTS PollReactions (messageID bigint PRIMARY KEY NOT NULL, reaction VARCHAR(200) NOT NULL)");
        initPolls();
    }

    public void insertPoll(long messageID, long channelID, List<String> reactions) {
        mySql.execute("INSERT INTO Polls (messageID, channelID) VALUES ('" + messageID + "', '" + channelID + "')");
        StringBuilder react = new StringBuilder();
        for(String reaction : reactions) {
            react.append(reaction).append(", ");
        }
        react = new StringBuilder(react.substring(0, react.length() - 2));
        mySql.execute("INSERT INTO PollReactions(messageID, reaction) VALUES ('" + messageID + "', '" + react + "')");
    }

    public void insertPoll(long messageID, long channelID, List<String> reactions, Date end) {
        mySql.execute("INSERT INTO Polls (messageID, channelID, endDate) VALUES ('" + messageID + "', '" + channelID + "', '" + new SimpleDateFormat("yyyy-MM-dd HH:mm").format(end) + "')");
        StringBuilder react = new StringBuilder();
        for(String reaction : reactions) {
            react.append(reaction).append(", ");
        }
        react = new StringBuilder(react.substring(0, react.length() - 2));
        mySql.execute("INSERT INTO PollReactions(messageID, reaction) VALUES ('" + messageID + "', '" + react + "')");
    }

    public boolean isPollExists(long messageID) {
        ResultSet resultSet = mySql.executeWithResult("SELECT EXISTS(SELECT 1 FROM Polls WHERE messageID='" + messageID + "' LIMIT 1) AS MessageExists");
        return DiscordBot.getUtils().isResultTrue(resultSet, "MessageExists");
    }

    public void removePoll(long messageID) {
        mySql.execute("DELETE FROM Polls WHERE messageID='" + messageID + "'");
        mySql.execute("DELETE FROM PollReactions WHERE messageID='" + messageID + "'");
    }

    public void initPolls() {
        ResultSet rsPoll = mySql.executeWithResult("SELECT * FROM Polls");
        try {
            while (rsPoll.next()) {
                long messageID = rsPoll.getLong("messageID");
                long channelID = rsPoll.getLong("channelID");
                Date date = null;
                if(rsPoll.getString("endDate") != null) {
                    date = new SimpleDateFormat("dd.MM.yyyy HH:mm").parse(rsPoll.getString("endDate"));
                }
                Poll poll = new Poll(serverManager, messageID, channelID);
                poll.setEndDate(date);
                pollManager.addPoll(poll);
            }

        } catch (SQLException | ParseException e) {
            DiscordBot.getLogger().log(Level.WARNING, e.getMessage());
        }

        for(Poll poll : pollManager.getPollList().values()) {
            List<String> reactions = new ArrayList<>();
            ResultSet resultSetReactions = mySql.executeWithResult("SELECT reaction FROM PollReactions WHERE messageID='" + poll.getMessageId() + "'");
            try {
                while (resultSetReactions.next()) {
                    Collections.addAll(reactions, resultSetReactions.getString("reaction").split(", "));
                    poll.setReactions(reactions);
                }
            } catch (SQLException e) {
                DiscordBot.getLogger().log(Level.WARNING, e.getMessage());
            }
        }
    }

}
