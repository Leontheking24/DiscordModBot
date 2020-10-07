package de.leontheking24.discordbot.Moderation.Commands;

import de.leontheking24.discordbot.Commands.Base.Command;
import de.leontheking24.discordbot.Commands.Base.CommandType;
import de.leontheking24.discordbot.DiscordBot;
import de.leontheking24.discordbot.ServerManager;
import de.leontheking24.discordbot.Utils.Utils;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

import java.awt.*;

public class UnmuteCommand extends Command {

    private final ServerManager serverManager;
    private final static String trigger = "Unmute";
    private final Utils utils = DiscordBot.getUtils();

    public UnmuteCommand(ServerManager serverManager) {
        super(serverManager, trigger, serverManager.getMessage("unmute_description"), "command.unmute", CommandType.Bot);
        this.serverManager = serverManager;
    }

    @Override
    public void executeCommand(TextChannel channel, Message message) {
        if(!isArgumentLengthZero(message)) {
            if(message.getMentionedMembers().size() > 0) {
                Member member = message.getMentionedMembers().get(0);
                if(serverManager.getMuteManager().isPlayerMuted(member.getIdLong())) {
                    serverManager.getMuteManager().removeMute(member.getIdLong());
                    DiscordBot.getJda().getTextChannelById(serverManager.getConfigManager().getConfig("notificationChannel")).sendMessage(utils.createEmbed(
                            serverManager.getMessage("unmute_success_title"), Color.GREEN, serverManager.getMessage("unmute_success_body").replace("{player}", member.getAsMention()))).queue();
                    return;
                }
                channel.sendMessage(utils.createEmbed(serverManager.getMessage("unmute_error_title"), Color.RED, serverManager.getMessage("unmute_error_body").replace("{player}", member.getAsMention()))).queue();
                return;
            }
        }
        channel.sendMessage(utils.createEmbed(serverManager.getMessage("unmute_syntax_title"), Color.RED, serverManager.
                getMessage("unmute_syntax_body").replace("{prefix}", serverManager.getBotCommandPrefix()).replace("{trigger}", trigger))).queue();
    }
}
