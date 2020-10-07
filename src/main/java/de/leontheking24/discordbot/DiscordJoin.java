package de.leontheking24.discordbot;

import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Level;

public class DiscordJoin extends ListenerAdapter {

    @Override
    public void onGuildJoin(@NotNull GuildJoinEvent event) {
        long id = event.getGuild().getIdLong();
        long ownerId = event.getGuild().getOwnerIdLong();
        DiscordBot.getServerLists().addServer(new ServerManager(id));

        ServerManager serverManager = DiscordBot.getServerLists().getServerManager(id);
        serverManager.getPermissionManager().playerAddPermission(ownerId, "*");
        serverManager.getConfigManager().updateConfig("notificationChannel", serverManager.getGuild().getDefaultChannel().getId());
        DiscordBot.getLogger().log(Level.INFO, "A new Server added the bot! Id: " + id);
    }
}
