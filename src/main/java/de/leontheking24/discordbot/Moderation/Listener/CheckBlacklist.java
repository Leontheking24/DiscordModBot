package de.leontheking24.discordbot.Moderation.Listener;

import de.leontheking24.discordbot.ServerManager;
import de.leontheking24.discordbot.DiscordBot;
import de.leontheking24.discordbot.Moderation.Likeness;
import de.leontheking24.discordbot.Moderation.Manager.BlacklistManager;
import de.leontheking24.discordbot.Utils.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;
import java.text.DateFormat;
import java.util.GregorianCalendar;
import java.util.logging.Level;

public class CheckBlacklist extends ListenerAdapter {

    private final Utils utils = DiscordBot.getUtils();

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        long serverId = event.getGuild().getIdLong();
        ServerManager serverManager = DiscordBot.getServerManager(serverId);
        if(Boolean.parseBoolean(serverManager.getConfigManager().getConfig("enableBlacklist"))) {
            Member author = event.getMember();
            Message message = event.getMessage();

            if(serverManager.getMuteManager().isPlayerMuted(author.getIdLong())) {
                message.delete().queue();

            } else {
                BlacklistManager blacklistManager = serverManager.getModManager();
                if(!event.getAuthor().isBot()) {
                    if(!serverManager.getPermissionManager().playerHasPermission(author.getIdLong(), "events.blacklist.ignore")) {
                        if(!message.getContentRaw().startsWith(serverManager.getBotCommandPrefix() + "blacklist")) {
                            String[] messageParts = message.getContentRaw().split(" ");
                            boolean hasBadWord = false;

                            for(String part : messageParts) {
                                for(String partList : blacklistManager.getBlackList()) {
                                    if(!hasBadWord) {
                                        Likeness likeness = utils.stringLikeness(serverManager, partList, part);
                                        if(likeness.isLikeness()) {
                                            blacklistManager.getChannel().sendMessage(new EmbedBuilder().setTitle(serverManager.getMessage("blacklist_message_title")).setColor(Color.RED)
                                                    .addField(serverManager.getMessage("blacklist_message_user"), author.getAsMention(), true)
                                                    .addField(serverManager.getMessage("blacklist_message_channel"), "<#" + message.getChannel().getId() + ">", true)
                                                    .addField(serverManager.getMessage("blacklist_message_time"),  DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT).format(new GregorianCalendar().getTime()), true)
                                                    .addField(serverManager.getMessage("blacklist_message_reason"), part, true)
                                                    .addField(serverManager.getMessage("blacklist_message_similar"), partList, true)
                                                    .addField(serverManager.getMessage("blacklist_message_likeness"), likeness.getDeviation() + "%", true)
                                                    .addField(serverManager.getMessage("blacklist_message_message"), message.getContentRaw(), false)
                                                    .build()).queue();
                                            try {
                                                message.delete().queue();
                                            } catch (Exception e) {
                                                DiscordBot.getLogger().log(Level.INFO, "Message couldn't delete");
                                            }
                                            hasBadWord = true;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

}
