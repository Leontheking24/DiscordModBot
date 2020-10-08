package de.leontheking24.discordbot.Commands.BotCommands;

import de.leontheking24.discordbot.Commands.Base.Command;
import de.leontheking24.discordbot.Commands.Base.CommandManager;
import de.leontheking24.discordbot.Commands.Base.CommandType;
import de.leontheking24.discordbot.DiscordBot;
import de.leontheking24.discordbot.ServerManager;
import de.leontheking24.discordbot.Utils.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.Date;
import java.util.List;

public class ListCommand extends Command {

    private final ServerManager serverManager;
    private final CommandManager commandManager;
    private final static String trigger = "commands";
    private final Utils utils = DiscordBot.getUtils();
    private final int cmdsPerSide = 8;
    private int pageAmount;

    public ListCommand(ServerManager serverManager) {
        super(serverManager, trigger, serverManager.getMessage("list_description"), "command.commands", CommandType.Bot);
        this.serverManager = serverManager;
        commandManager = serverManager.getCommandManager();
    }

    @Override
    public void executeCommand(TextChannel channel, Message message) {
        String[] args = message.getContentRaw().split(" ");
        pageAmount = (int) (Math.ceil((double)commandManager.getCommandListTypSorted().size()/cmdsPerSide));
        if(args.length == 1) {
            channel.sendMessage(createMessage(1).build()).queue();

        } else if(utils.isNumeric(args[1])) {
            int page = Integer.parseInt(args[1]);
            if(page < 1) {
                page=1;
            }
            if(page > pageAmount) {
                page = pageAmount;
            }
            channel.sendMessage(createMessage(page).build()).queue();
        } else {
            channel.sendMessage(createMessage(1).build()).queue();
        }
    }

    public EmbedBuilder createMessage(int page) {
        List<Command> commandList = commandManager.getCommandListTypSorted();

        EmbedBuilder help = new EmbedBuilder();
        help.setAuthor(DiscordBot.getBot().getName(), DiscordBot.getBot().getEffectiveAvatarUrl(), DiscordBot.getBot().getEffectiveAvatarUrl());
        help.setTitle(serverManager.getMessage("list_help_title"));
        if(serverManager.getBotCommandPrefix().equals(serverManager.getUserCommandPrefix())) {
            help.setDescription(serverManager.getMessage("list_help_body_single").replace("{prefix}", serverManager.getBotCommandPrefix()));
        }
        if(!serverManager.getBotCommandPrefix().equals(serverManager.getUserCommandPrefix())) {
            help.setDescription(serverManager.getMessage("list_help_body_double").replace("{botprefix}", serverManager.getBotCommandPrefix()).replace("{userprefix}", serverManager.getUserCommandPrefix()));
        }

        help.setColor(0xD631B5);
        int start = (page-1)*cmdsPerSide;
        int end = page*cmdsPerSide;
        boolean startWithBot = false;

        if(end > commandList.size()) {
            end = commandList.size();
        }

        if(start <= commandManager.getBotCommandList().size()) {
            help.appendDescription(serverManager.getMessage("list_bot_field"));
            startWithBot = true;
        }
        if(start >= commandManager.getBotCommandList().size()) {
            help.appendDescription(serverManager.getMessage("list_user_field"));
            startWithBot = false;
        }

        for(int i = start; i < end; i++) {
            if(commandList.size() > i) {
                Command command = commandList.get(i);
                if(startWithBot) {
                    if(command.getCommandType().equals(CommandType.User)) {
                        help.addField(serverManager.getMessage("list_user_field"), "",false);
                        startWithBot=false;
                    }
                }

                help.addField(serverManager.getMessage("list_message_name"), command.getTrigger(), true);
                if(command.hasValue()) {
                    help.addField(serverManager.getMessage("list_message_value"), command.getValue(), true);
                    help.addField(serverManager.getMessage("list_message_description"), command.getDescription(), true);
                }
                if(!command.hasValue()) {
                    help.addField(serverManager.getMessage("list_message_description"), command.getDescription(), true);
                    help.addBlankField(true);
                }
            }
        }
        help.setFooter(serverManager.getMessage("list_page") + "\n" + page + "/" + pageAmount + "\u200b \u200b \u200b \u200b \u200b | \u200b \u200b \u200b");
        help.setTimestamp(new Date().toInstant());

        return help;
    }
}
