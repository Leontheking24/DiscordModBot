package de.leontheking24.discordbot.Greeting;

import de.leontheking24.discordbot.DiscordBot;
import de.leontheking24.discordbot.ServerManager;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class GreetingListener extends ListenerAdapter {

    @Override
    public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event) {
        ServerManager serverManager = DiscordBot.getServerManager(event.getGuild().getIdLong());
        if(Boolean.parseBoolean(serverManager.getConfigManager().getConfig("enableJoinMessage"))) {
            String message = serverManager.getMessage("joinMessage").replace("{Player}", event.getUser().getName());
            if(serverManager.getConfigManager().getConfig("customJoinMessage").trim().length() > 0) {
                message = serverManager.getConfigManager().getConfig("customJoinMessage").replace("{Player}", event.getMember().getAsMention());
            }
            serverManager.getGreetingChannel().sendMessage(message).queue();
        }
    }


    @Override
    public void onGuildMemberRemove(@NotNull GuildMemberRemoveEvent event) {
        ServerManager serverManager = DiscordBot.getServerManager(event.getGuild().getIdLong());
        if(Boolean.parseBoolean(serverManager.getConfigManager().getConfig("enableLeaveMessage"))) {
            String message = serverManager.getMessage("leaveMessage").replace("{Player}", event.getUser().getName());
            if(serverManager.getConfigManager().getConfig("customLeaveMessage").trim().length() > 0) {
                message = serverManager.getConfigManager().getConfig("customLeaveMessage").replace("{Player}", event.getMember().getAsMention());
            }
            serverManager.getGreetingChannel().sendMessage(message).queue();
        }
    }
}
