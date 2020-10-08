package de.leontheking24.discordbot.Moderation.Listener;

import de.leontheking24.discordbot.DiscordBot;
import de.leontheking24.discordbot.ServerManager;
import de.leontheking24.discordbot.Utils.Utils;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class CheckCapslock extends ListenerAdapter {

    private final Utils utils = DiscordBot.getUtils();

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        ServerManager serverManager = DiscordBot.getServerManager(event.getGuild().getIdLong());
        if(Boolean.parseBoolean(serverManager.getConfigManager().getConfig("enableCapsProtection"))) {
            Member author = event.getMember();
            String message = event.getMessage().getContentRaw();

            if(!event.getAuthor().isBot()) {
                if(!serverManager.getPermissionManager().playerHasPermission(author.getIdLong(), "events.capslock.ignore")) {
                    if(utils.percentagOfUpperCase(message) >= 75) {
                        try {
                            event.getMessage().delete().queue();
                        } catch (Exception e) {
                        }
                        author.getUser().openPrivateChannel().queue(privateChannel -> {
                            privateChannel.sendMessage(serverManager.getMessage("capslock_user")).queue();
                        });
                    }
                }
            }
        }
    }
}
