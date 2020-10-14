package de.leontheking24.discordbot;

import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Level;

public class DiscordJoin extends ListenerAdapter {

    @Override
    public void onGuildJoin(@NotNull GuildJoinEvent event) {
        long id = event.getGuild().getIdLong();
        long ownerId = event.getGuild().getOwnerIdLong();
        DiscordBot.getServerLists().addServer(new ServerManager(id));
        event.getGuild().getMemberById(ownerId).getUser().openPrivateChannel().queue(privateChannel -> {
            privateChannel.sendMessage("Hey, thank you for using my bot! :) You can use `!help` to see helpful information. " +
                    "To see all commands, use `!commands [page]`").queue();
        });

        ServerManager serverManager = DiscordBot.getServerLists().getServerManager(id);
        serverManager.getPermissionManager().playerAddPermission(ownerId, "*");
        serverManager.getConfigManager().updateConfig("notificationChannel", serverManager.getGuild().getDefaultChannel().getId());
        DiscordBot.getLogger().log(Level.INFO, "A new Server added the bot! Id: " + id);
    }

    @Override
    public void onGuildLeave(@NotNull GuildLeaveEvent event) {
        DiscordBot.getServerLists().removeServer(event.getGuild().getIdLong());
        DiscordBot.getLogger().log(Level.INFO, "The Bot was removed from server " + event.getGuild().getId());
    }
}
