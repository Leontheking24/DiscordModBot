package de.leontheking24.discordbot.Moderation.Commands;

import de.leontheking24.discordbot.Commands.Base.Command;
import de.leontheking24.discordbot.Commands.Base.CommandType;
import de.leontheking24.discordbot.DiscordBot;
import de.leontheking24.discordbot.Moderation.Manager.BlacklistManager;
import de.leontheking24.discordbot.ServerManager;
import de.leontheking24.discordbot.Utils.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

import java.awt.*;
import java.util.List;

public class BlacklistCommand extends Command {

    private final ServerManager serverManager;
    private final static String trigger = "blacklist";
    private final BlacklistManager blacklistManager;
    private final Utils utils = DiscordBot.getUtils();

    public BlacklistCommand(ServerManager serverManager) {
        super(serverManager, trigger, serverManager.getMessage("blacklist_description"), "command.blacklist", CommandType.Bot);
        super.setInfo(serverManager.getMessage("blacklist_info"));
        this.serverManager = serverManager;
        blacklistManager = serverManager.getModManager();
    }

    @Override
    public void executeCommand(TextChannel channel, Message message) {
        String[] args = message.getContentRaw().split(" ");
        if(args.length == 1) {
            channel.sendMessage(help().build()).queue();

        } else if(args[1].equalsIgnoreCase("add")) {
            add(channel, args);

        } else if(args[1].equalsIgnoreCase("remove")) {
            remove(channel, args);

        } else if(args[1].equalsIgnoreCase("show") || args[1].equalsIgnoreCase("get")) {
            list(channel);

        } else {
            channel.sendMessage(help().build()).queue();
        }
    }

    public EmbedBuilder help() {
        EmbedBuilder info = new EmbedBuilder();
        info.setColor(Color.MAGENTA);
        info.setTitle(serverManager.getMessage("blacklist_help_title"));
        info.setDescription(serverManager.getMessage("blacklist_help_description"));
        info.addField(serverManager.getMessage("blacklist_help_show"), "> " + serverManager.getBotCommandPrefix() + trigger + " show", false);
        info.addField(serverManager.getMessage("blacklist_help_add_title"), serverManager.getMessage("blacklist_help_add_body").replace("{prefix}", serverManager.getBotCommandPrefix()).replace("{trigger}", trigger), false);
        info.addField(serverManager.getMessage("blacklist_help_remove_title"), serverManager.getMessage("blacklist_help_remove_body").replace("{prefix}", serverManager.getBotCommandPrefix()).replace("{trigger}", trigger), false);
        return info;
    }

    public void add(TextChannel channel, String[] args) {
        if(args.length == 2) {
            channel.sendMessage(utils.createEmbed(serverManager.getMessage("blacklist_add_syntax_title"), Color.RED, serverManager.getMessage("blacklist_add_syntax_body").
                    replace("{prefix}", serverManager.getBotCommandPrefix()).replace("{trigger}", trigger))).queue();

        } else {
            String list = "";
            for(int i = 2; i < args.length; i++) {
                if(!blacklistManager.isBlacklisted(args[i])) {
                    blacklistManager.addToBlackList(args[i]);
                    list += args[i] + ", ";
                }
            }
            list = list.substring(0, list.length()-2);
            if(list.length() > 0) {
                if(list.length() == 1) {
                    channel.sendMessage(utils.createEmbed(serverManager.getMessage("blacklist_add_success_title"), Color.GREEN, serverManager.getMessage("blacklist_add_success_single").replace("{list}", list))).queue();
                } else {
                    channel.sendMessage(utils.createEmbed(serverManager.getMessage("blacklist_add_success_title"), Color.GREEN, serverManager.getMessage("blacklist_add_success_multiple").replace("{list}", list))).queue();
                }
            } else {
                channel.sendMessage(utils.createEmbed(serverManager.getMessage("blacklist_add_error_title"), Color.RED, serverManager.getMessage("blacklist_add_error_body"))).queue();
            }

        }
    }

    public void remove(TextChannel channel, String[] args) {
        if(args.length == 2) {
            channel.sendMessage(utils.createEmbed(serverManager.getMessage("blacklist_remove_syntax_title"), Color.RED,
                    serverManager.getMessage("blacklist_remove_syntax_body").replace("{prefix}", serverManager.getBotCommandPrefix()).replace("{trigger}", trigger))).queue();

        } else {
            String list = "";
            for(int i = 2; i < args.length; i++) {
                if(blacklistManager.isBlacklisted(args[i])) {
                    blacklistManager.removeFromBlackList(args[i]);
                    list += args[i] + ", ";
                }
            }
            list = list.substring(0, list.length()-2);
            if(list.length() > 0) {
                if(list.length() == 1) {
                    channel.sendMessage(utils.createEmbed(serverManager.getMessage("blacklist_remove_success_title"), Color.GREEN, serverManager.getMessage("blacklist_remove_success_single").replace("{list}", list))).queue();
                } else {
                    channel.sendMessage(utils.createEmbed(serverManager.getMessage("blacklist_remove_success_title"), Color.GREEN, serverManager.getMessage("blacklist_remove_success_multiple").replace("{list}", list))).queue();
                }
            } else {
                channel.sendMessage(utils.createEmbed(serverManager.getMessage("blacklist_remove_error_title"), Color.RED, serverManager.getMessage("blacklist_remove_error_body"))).queue();
            }
        }
    }

    public void list(TextChannel channel) {
        EmbedBuilder list = new EmbedBuilder();
        list.setColor(Color.MAGENTA);
        list.setTitle(serverManager.getMessage("blacklist_list_title"));
        list.setDescription(serverManager.getMessage("blacklist_list_description_empty"));
        List<String> blacklist = blacklistManager.getBlackList();
        if(blacklist.size() > 0) {
            list.setDescription(serverManager.getMessage("blacklist_list_description"));
            for(String arg : blacklist) {
                list.appendDescription("\n> ➝ " + arg);
            }
        }
        channel.sendMessage(list.build()).queue();
    }
}