package de.leontheking24.discordbot.Poll;

import de.leontheking24.discordbot.Commands.Base.Command;
import de.leontheking24.discordbot.Commands.Base.CommandType;
import de.leontheking24.discordbot.DiscordBot;
import de.leontheking24.discordbot.Poll.Entitys.*;
import de.leontheking24.discordbot.ServerManager;
import de.leontheking24.discordbot.Utils.DateValidatorUsingDateFormat;
import de.leontheking24.discordbot.Utils.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PollCommand extends Command {

    private final ServerManager serverManager;
    private final static String trigger = "poll";
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
    private final DateValidatorUsingDateFormat dateValidator = new DateValidatorUsingDateFormat("dd.MM.yyyy HH:mm");
    private final Utils utils = DiscordBot.getUtils();
    private final PollManager pollManager;

    public PollCommand(ServerManager serverManager) {
        super(serverManager, trigger, serverManager.getMessage("poll_description"), "command.poll", CommandType.Bot);
        this.serverManager = serverManager;
        pollManager = serverManager.getPollManager();
    }

    @Override
    public void executeCommand(TextChannel channel, Message message) {
        String[] baseArgs = message.getContentRaw().split(" ");
        if(baseArgs.length > 1) {
            if(baseArgs[1].equalsIgnoreCase("end") || baseArgs[1].equalsIgnoreCase("finish")) {
                finishPoll(channel, message);

            } else {
                startPoll(message, channel);
            }

        } else {
            channel.sendMessage(help().build()).queue();
        }
    }

    public void finishPoll(TextChannel channel, Message message) {
        String[] args = message.getContentRaw().split(" ");
        if(args.length > 2) {
            long messageID = utils.searchForMessageID(args);
            if(messageID != 0) {
                if(pollManager.isMessageAPoll(messageID)) {
                    pollManager.getPoll(messageID).finishPoll();

                } else {
                    channel.sendMessage(new EmbedBuilder().setColor(Color.RED).setTitle(serverManager.getMessage("poll_finish_wrong_title")).
                            setDescription(serverManager.getMessage("poll_finish_wrong_body")).build()).queue();
                }

            } else {
                channel.sendMessage(new EmbedBuilder().setColor(Color.RED).setTitle(serverManager.getMessage("poll_finish_arg_title")).setDescription(
                        serverManager.getMessage("poll_finish_arg_body").replace("{prefix}", serverManager.getBotCommandPrefix()).replace("{trigger}", trigger)).build()).queue();
            }

        } else {
            channel.sendMessage(new EmbedBuilder().setColor(Color.RED).setTitle(serverManager.getMessage("poll_finish_syntax_title")).setDescription(
                    serverManager.getMessage("poll_finish_syntax_body").replace("{prefix}", serverManager.getBotCommandPrefix()).replace("{trigger}", trigger)).build()).queue();
        }

    }

    public void startPoll(Message message, TextChannel channel) {
        PollChannelSearch pollChannelSearch = searchForChannel(message);
        PollDateSearch pollDateSearch = searchForDate(message);
        PollMessageSearch pollMessageSearch = searchForMessage(message);

        String[] args = message.getContentRaw().replace(pollChannelSearch.getReplaceArgs(), "").replace(pollDateSearch.getReplaceArgs(), "")
                .replace(pollMessageSearch.getReplaceArgs(), "").substring(trigger.length() + serverManager.getBotCommandPrefix().length() + 1).split(pollManager.getSeperateKey());

        if(args.length > 1) {
            EmbedBuilder pollEmbed = new EmbedBuilder();
            pollEmbed.setColor(Color.magenta);
            pollEmbed.setTitle(serverManager.getMessage("poll_start_success_title"));
            pollEmbed.setDescription(serverManager.getMessage("poll_start_success_description"));
            Date endDate = pollDateSearch.getDate();
            boolean isEndSet = false;

            for(int i = 1; i <= args.length; i++) {
                if(args[i-1].startsWith(" ")) {
                    args[i-1] = args[i-1].substring(1);
                }
                pollEmbed.addField(serverManager.getMessage("poll_start_success_field") + " " + PollEmojis.getByDigit(i).getEmoji(),"> " + args[i-1], true);
                pollEmbed.setFooter(serverManager.getMessage("poll_start_success_no_time"));
            }

            if(!pollDateSearch.getReplaceArgs().equals("")) {
                if(endDate.after(new Date())) {
                    isEndSet = true;
                    pollEmbed.setFooter(serverManager.getMessage("poll_start_success_time").replace("{time}", dateFormat.format(endDate)));
                }
            }

            int finalSelectCount = args.length;
            boolean finalIsEndSet = isEndSet;

            if(pollMessageSearch.getMessageId() != 0) {
                pollEmbed.setDescription(pollChannelSearch.getSendChannel().retrieveMessageById(pollMessageSearch.getMessageId()).complete().getContentRaw());
                pollEmbed.appendDescription("\n\n" + serverManager.getMessage("poll_start_success_description"));

                pollChannelSearch.getSendChannel().retrieveMessageById(pollMessageSearch.getMessageId()).complete().delete().queue();
                pollChannelSearch.getSendChannel().sendMessage(pollEmbed.build()).queue(botMessage -> createPoll(botMessage, pollChannelSearch.getSendChannel(), finalIsEndSet, finalSelectCount, endDate));
                message.delete().queue();
                return;
            }
            pollChannelSearch.getSendChannel().sendMessage(pollEmbed.build()).queue(botMessage -> createPoll(botMessage, pollChannelSearch.getSendChannel(), finalIsEndSet, finalSelectCount, endDate));
            message.delete().queue();
            return;
        }
        channel.sendMessage(utils.createEmbed(serverManager.getMessage("poll_start_syntax_title"), Color.RED, serverManager.getMessage("poll_start_syntax_body")
                .replace("{prefix}", serverManager.getBotCommandPrefix()).replace("{trigger}", trigger))).queue();
    }

    public void createPoll(Message message, TextChannel channel, boolean finalIsEndSet, int finalSelectCount, Date endDate) {
        List<String> reactions = new ArrayList<>();
        for(int i = 1; i <= finalSelectCount; i++) {
            PollEmojis emojis = PollEmojis.getByDigit(i);
            message.addReaction(emojis.getUnicode()).queue();
            reactions.add(emojis.getUnicodeDB());
        }
        Poll poll = new Poll(serverManager, message.getIdLong(), channel.getIdLong(), reactions);
        if(finalIsEndSet) {
            poll.setEndDate(endDate);
        }
        poll.initToDatebase();
    }

    public EmbedBuilder help() {
        EmbedBuilder help = new EmbedBuilder();
        help.setTitle(serverManager.getMessage("poll_help_title")).setColor(Color.cyan);
        help.addField("Seperate-Key", pollManager.getSeperateKey(), false);
        return help;
    }


    public PollMessageSearch searchForMessage(Message message) {
        String[] args = message.getContentRaw().split(" ");
        long returnId = 0;
        String replaceString = "";
        int triggerSlot = -1;

        for(int i = 0; i < args.length; i++) {
            String arg = args[i];
            if(arg.equalsIgnoreCase("-message")) {
                triggerSlot = i;
            }
        }

        if(utils.isStringAMessageId(args[triggerSlot+1])) {
            replaceString = args[triggerSlot] + " " + args[triggerSlot+1];
            returnId = Long.valueOf(args[triggerSlot+1]);
        }
        return new PollMessageSearch(returnId, replaceString);
    }

    public PollChannelSearch searchForChannel(Message message) {
        String[] args = message.getContentRaw().split(" ");
        List<TextChannel> channels = message.getMentionedChannels();
        TextChannel returnChannel = message.getTextChannel();
        String replaceString = "";
        int triggerSlot = -1;

        for(int i = 0; i < args.length; i++) {
            String arg = args[i];
            if(arg.equalsIgnoreCase("-channel")) {
                triggerSlot = i;
            }
        }

        if(triggerSlot != -1) {
            for(TextChannel channel : channels) {
                String channelId = "<#" + channel.getId() + ">";
                if(channelId.equals(args[triggerSlot+1])) {
                    returnChannel = channel;
                    replaceString = args[triggerSlot] + " " + args[triggerSlot+1];
                }
            }
        }

        return new PollChannelSearch(returnChannel, replaceString);
    }

    public PollDateSearch searchForDate(Message message) {
        String[] args = message.getContentRaw().split(" ");
        String replaceString = "";
        String date = "15.09.2020 10:00";
        int triggerSlot = -1;

        for(int i = 0; i < args.length; i++) {
            String arg = args[i];
            if(arg.equalsIgnoreCase("-end") || arg.equalsIgnoreCase("-date")) {
                triggerSlot = i;
            }
        }
        if(dateValidator.isValid(args[triggerSlot+1] + " " + args[triggerSlot+2])) {
            date = args[triggerSlot+1] + " " + args[triggerSlot+2];
            replaceString = args[triggerSlot] + " " + date;
        }
        return new PollDateSearch(date, replaceString);
    }

}