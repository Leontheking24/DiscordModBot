package de.leontheking24.discordbot.Moderation.Commands;

import de.leontheking24.discordbot.Commands.Base.Command;
import de.leontheking24.discordbot.Commands.Base.CommandType;
import de.leontheking24.discordbot.DiscordBot;
import de.leontheking24.discordbot.Moderation.Mute;
import de.leontheking24.discordbot.ServerManager;
import de.leontheking24.discordbot.Utils.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

import java.awt.*;
import java.text.SimpleDateFormat;

public class MuteInfoCommand extends Command {

    private final ServerManager serverManager;
    private final static String trigger = "MuteInfo";
    private final Utils utils = DiscordBot.getUtils();
    private final SimpleDateFormat dateMessage = new SimpleDateFormat("dd.MM.yyyy HH:mm");

    public MuteInfoCommand(ServerManager serverManager) {
        super(serverManager, trigger, serverManager.getMessage("muteinfo_description"), "command.muteinfo", CommandType.Bot);
        this.serverManager = serverManager;
    }

    @Override
    public void executeCommand(TextChannel channel, Message message) {
        if(!isArgumentLengthZero(message)) {
            if(message.getMentionedMembers().size() > 0) {
                Member player = message.getMentionedMembers().get(0);
                if(serverManager.getMuteManager().isPlayerMuted(player.getIdLong())) {
                    Mute mute = serverManager.getMuteManager().getPlayerMute(player.getIdLong());

                    EmbedBuilder embed = new EmbedBuilder();
                    embed.setTitle(serverManager.getMessage("muteinfo_success_title")).setColor(Color.RED);
                    embed.addField(serverManager.getMessage("muteinfo_success_player"), player.getAsMention(), true);
                    embed.addField(serverManager.getMessage("muteinfo_success_time"),  dateMessage.format(mute.getMuteEnd()), true);
                    embed.addField(serverManager.getMessage("muteinfo_success_reason"), mute.getReason(), false);

                    channel.sendMessage(embed.build()).queue();
                    return;
                }
                channel.sendMessage(utils.createEmbed(serverManager.getMessage("muteinfo_error_title"), Color.RED,
                        serverManager.getMessage("muteinfo_error_body").replace("{Player}", player.getEffectiveName()))).queue();
                return;
            }
        }
        channel.sendMessage(utils.createEmbed(serverManager.getMessage("muteinfo_syntax_title"), Color.RED,
                serverManager.getMessage("muteinfo_syntax_body").replace("{prefix}", serverManager.getBotCommandPrefix()).replace("{trigger}", trigger))).queue();
    }

}
