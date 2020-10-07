package de.leontheking24.discordbot.Commands.BotCommands;

import de.leontheking24.discordbot.Commands.Base.Command;
import de.leontheking24.discordbot.Commands.Base.CommandType;
import de.leontheking24.discordbot.DiscordBot;
import de.leontheking24.discordbot.ServerManager;
import de.leontheking24.discordbot.Utils.Utils;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

import java.awt.*;
import java.util.logging.Level;

public class ReloadServer extends Command {

    private final static String trigger = "reload";
    private final Utils utils = DiscordBot.getUtils();
    private final ServerManager serverManager;

    public ReloadServer(ServerManager serverManager) {
        super(serverManager, trigger, serverManager.getMessage("reload_description"), "command.reload", CommandType.Bot);
        this.serverManager = serverManager;
    }

    @Override
    public void executeCommand(TextChannel channel, Message message) {
        long id = message.getGuild().getIdLong();
        DiscordBot.getLogger().log(Level.INFO, "Bot will be restart on server " + id);
        serverManager.initServer();
        DiscordBot.getLogger().log(Level.INFO, "Bot was restarted on server " + id);
        channel.sendMessage(utils.createEmbed(serverManager.getMessage("reload_title"), Color.CYAN, serverManager.getMessage("reload_body"))).queue();
    }
}
