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
import java.util.*;
import java.util.List;

public class MuteCommand extends Command {

    private static String trigger = "mute";
    private ServerManager serverManager;
    private Utils utils = DiscordBot.getUtils();

    public MuteCommand(ServerManager serverManager) {
        super(serverManager, trigger, serverManager.getMessage("mute_description"), "command.mute", CommandType.Bot);
        this.serverManager = serverManager;

    }

    @Override
    public void executeCommand(TextChannel channel, Message message) {
        if(!isArgumentLengthZero(message)) {
            String[] args = getArguments(message);
            if(args.length >= 2) {
                if(message.getMentionedMembers().size() == 1) {
                    Member mutedPlayer = message.getMentionedMembers().get(0);
                    Date muteTime = getMuteTime(args[1]);
                    String reason = null;

                    if(muteTime != null) {
                        if(args.length >= 3) {
                            reason = "";
                            for(int i = 2; i < args.length; i++) {
                                reason += args[i] + " ";
                            }
                            reason.substring(0, reason.length()-1);
                        }
                        serverManager.getMuteManager().mutePlayer(mutedPlayer.getIdLong(), muteTime, reason);
                        return;

                    } else {
                        channel.sendMessage(utils.createEmbed(serverManager.getMessage("mute_convert_title"), Color.RED, serverManager.getMessage("mute_convert_body").replace("{value}", args[1]))).queue();
                    }
                }
            }
        }
        channel.sendMessage(utils.createEmbed(serverManager.getMessage("mute_syntax_title"), Color.RED, serverManager.getMessage("mute_syntax_body").
                replace("{author}",  message.getAuthor().getAsMention()).replace("{prefix}", serverManager.getBotCommandPrefix()).replace("{trigger}", trigger))).queue();
        return;
    }

    public Date getMuteTime(String value) {
        List<Character> correctChars = new ArrayList(Arrays.asList('m', 'h', 'd', 'w'));
        char endchar = value.toCharArray()[value.getBytes().length-1];
        String timeStr = value.substring(0, value.length()-1);

        if(correctChars.contains(endchar) && utils.isNumeric(timeStr)) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            Integer time = Integer.valueOf(timeStr);

            if(endchar == 'm') {
                calendar.add(Calendar.MINUTE, time);

            }
            if(endchar == 'h') {
                calendar.add(Calendar.HOUR_OF_DAY, time);
            }
            if(endchar == 'd') {
                calendar.add(Calendar.DAY_OF_YEAR, time);
            }
            if(endchar == 'w') {
                calendar.add(Calendar.WEEK_OF_YEAR, time);
            }
            return calendar.getTime();
        }
        return null;
    }
}
