package de.leontheking24.discordbot.Moderation.Manager;

import de.leontheking24.discordbot.DiscordBot;
import de.leontheking24.discordbot.Moderation.Mute;
import de.leontheking24.discordbot.ServerManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Level;

public class MuteManager {

    private final ServerManager serverManager;
    private final HashMap<Long, Mute> muteList;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private final SimpleDateFormat dateMessage = new SimpleDateFormat("dd.MM.yyyy HH:mm");

    public MuteManager(ServerManager serverManager) {
        this.serverManager = serverManager;
        this.muteList = new HashMap<>();
        initMutes();
    }

    public boolean isPlayerMuted(long playerId) {
        return muteList.containsKey(playerId);
    }

    public Mute getPlayerMute(long playerId) {
        return muteList.get(playerId);
    }

    public void removeMute(long playerId) {
        muteList.remove(playerId);
        serverManager.getMySql().execute("DELETE FROM MutedPlayers WHERE playerId='" + playerId + "'");
        DiscordBot.getJda().getUserById(playerId).openPrivateChannel().queue(privateChannel -> {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setTitle(serverManager.getMessage("unmute_player_title")).setColor(Color.GREEN);
            embed.setDescription(serverManager.getMessage("unmute_player_body").replace("{Server}", serverManager.getGuild().getName()));
            privateChannel.sendMessage(embed.build()).queue();

        });
    }

    public void mutePlayer(long playerId, Date endDate, String reason) {
        if(!isPlayerMuted(playerId)) {
            muteList.put(playerId, new Mute(playerId, endDate, reason));
            serverManager.getMySql().execute("INSERT INTO MutedPlayers (playerId, muteEnd, reason) VALUES('" + playerId + "', '" + dateFormat.format(endDate) + "', '" + reason + "')");
            muteMessage(serverManager.getGuild().getMemberById(playerId), endDate, getPlayerMute(playerId).getReason());
        }
    }

    public void initMutes() {
        ResultSet resultSet = serverManager.getMySql().executeWithResult("SELECT * FROM MutedPlayers");
        try {
            while (resultSet.next()) {
                long playerId = resultSet.getLong("playerId");
                muteList.put(playerId, new Mute(playerId, resultSet.getTimestamp("muteEnd"), resultSet.getString("reason")));

            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void checkMuteEnd() {
        if(muteList.size() > 0) {
            for(long playerId : muteList.keySet()) {
                Date endDate = getPlayerMute(playerId).getMuteEnd();
                Date now = new Date();

                if(endDate.before(now) || dateFormat.format(endDate).equals(dateFormat.format(now))) {
                    DiscordBot.getLogger().log(Level.INFO, "Try to remove Mute from " + playerId);
                    removeMute(playerId);
                }
            }
        }
    }

    public void muteMessage(Member player, Date endDate, String reason) {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle(serverManager.getMessage("mute_message_title")).setColor(Color.RED);
        embed.addField(serverManager.getMessage("mute_message_player"), player.getAsMention(), true);
        embed.addField(serverManager.getMessage("mute_message_date"), dateMessage.format(endDate) + " Uhr", true);
        embed.addField(serverManager.getMessage("mute_message_reason"), reason, false);

        serverManager.getNotificationChannel().sendMessage(embed.build()).queue();
        player.getUser().openPrivateChannel().queue(privateChannel -> {
            embed.setDescription(serverManager.getMessage("mute_playermessage").replace("{Server}", serverManager.getGuild().getName()));
            privateChannel.sendMessage(embed.build()).queue();
        });
    }

    public void saveMutes() {
        for(Mute mute : muteList.values()) {
            ResultSet resultSet = serverManager.getMySql().executeWithResult("SELECT EXISTS(SELECT 1 FROM MutedPlayers WHERE playerId='" + mute.getPlayerId() + "' LIMIT 1) AS isMuteExists");
            if(!DiscordBot.getUtils().isResultTrue(resultSet, "isMuteExists")) {
                serverManager.getMySql().execute("INSERT INTO MutedPlayers (playerId, muteEnd, reason) VALUES('" + mute.getPlayerId() + "', '" + dateFormat.format(mute.getMuteEnd()) + "', '" + mute.getReason() + "')");
            }
        }
    }

}
