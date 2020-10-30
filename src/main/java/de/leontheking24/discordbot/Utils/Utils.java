package de.leontheking24.discordbot.Utils;

import de.leontheking24.discordbot.DiscordBot;
import de.leontheking24.discordbot.Moderation.ELeetSpeak;
import de.leontheking24.discordbot.Moderation.Likeness;
import de.leontheking24.discordbot.ServerManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

public class Utils {

    public Utils() {
    }

    public Likeness stringLikeness(ServerManager serverManager, String value1, String value2) {
        char[] value1Char = value1.toUpperCase().toCharArray();
        char[] value2Char = value2.toUpperCase().toCharArray();
        for(int i = -1; i <= 1; i++) {
            Likeness likeness = checkLikeness(value1Char, value2Char, i);
            if(likeness.isLikeness()) {
                return likeness;
            }
        }

        if(Boolean.parseBoolean(serverManager.getConfigManager().getConfig("isCheckingLeetspeakAtBlacklist"))) {
            StringBuilder value2Leetspeak = new StringBuilder();
            for(char character : value2Char) {
                if(ELeetSpeak.getCharByLeet(character) != null) {
                    value2Leetspeak.append(ELeetSpeak.getCharByLeet(character));
                } else {
                    value2Leetspeak.append(character);
                }
            }
            Likeness leet = checkLikeness(value1Char, value2Leetspeak.toString().toCharArray(), 0);
            if(leet.isLikeness()) {
                return leet;
            }
        }
        return checkLikeness(value1Char, value2Char, 0);
    }

    public Likeness checkLikeness(char[] value1, char[] value2, int delay) {
        int deviationToReach = 75;
        int deviationsChars = 0;

        for(int i = 0; i < value2.length; i++) {
            if(i + delay < value1.length && i + delay >= 0) {
                if(value1[i + delay] == value2[i]) {
                    deviationsChars++;
                }
            }
        }
        int deviation = (int) Math.round((double)deviationsChars*100/(double)value1.length);
        return new Likeness(deviation >= deviationToReach, deviation);
    }

    public int checkLikeness(char[] value1, char[] value2) {
        int deviationsChars = 0;
        for(int i = 0; i < value2.length; i++) {
            if(i < value1.length && i >= 0) {
                if(value1[i] == value2[i]) {
                    deviationsChars++;
                }
            }
        }
        return (int) Math.round((double)deviationsChars*100/(double)value1.length);
    }

    public boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            Double.parseDouble(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    public int percentagOfUpperCase(String toCheck) {
        toCheck = toCheck.replaceAll("\\s+","");
        int uppers = 0;
        for(char c : toCheck.toCharArray()) {
            if(Character.isUpperCase(c)) {
                uppers++;
            }
        }
        return  (int) Math.round((double)uppers*100/(double)toCheck.length());
    }

    public long searchForMessageID(String[] args) {
        for(String toCheck : args) {
            if(toCheck.length() == 18) {
                if(isNumeric(toCheck)) {
                    return Long.parseLong(toCheck);
                }
            }
        }
        return 0;
    }

    public boolean isStringAMessageId(String arg) {
        if(arg.length() == 18) {
            return isNumeric(arg);
        }
        return false;
    }

    public MessageEmbed createEmbed(String title, Color color, String message) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(color).setTitle(title).setDescription(message);
        return embedBuilder.build();
    }

    public boolean isResultTrue(ResultSet resultSet, String resultName) {
        try {
            while (resultSet.next()) {
                if(resultSet.getInt(resultName) == 1) {
                    return true;
                }
            }
        } catch (SQLException throwable) {
            DiscordBot.getLogger().log(Level.WARNING, throwable.getMessage());
        }
        return false;
    }

}
