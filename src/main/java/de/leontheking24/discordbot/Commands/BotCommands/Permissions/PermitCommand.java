package de.leontheking24.discordbot.Commands.BotCommands.Permissions;

import de.leontheking24.discordbot.Commands.Base.Command;
import de.leontheking24.discordbot.Commands.Base.CommandType;
import de.leontheking24.discordbot.DiscordBot;
import de.leontheking24.discordbot.ServerManager;
import de.leontheking24.discordbot.Utils.Utils;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;

import java.awt.*;
import java.util.List;

public class PermitCommand extends Command {

    private final ServerManager serverManager;
    private final Utils utils = DiscordBot.getUtils();
    private final static String trigger = "permit";

    public PermitCommand(ServerManager serverManager) {
        super(serverManager, trigger, serverManager.getMessage("permit_description"), "command.permit", CommandType.Bot);
        this.serverManager = serverManager;
    }

    @Override
    public void executeCommand(TextChannel channel, Message message) {
        if(!isArgumentLengthZero(message)) {
            String[] args = getArguments(message);
            List<Member> selectedMembers = message.getMentionedMembers();
            List<Role> selectedRoles = message.getMentionedRoles();

            if(args.length > 1) {
                if(selectedMembers.size() > 0 || selectedRoles.size() > 0) {
                    for(Member member : selectedMembers) {
                        String permissionSet = "";
                        for(String arg : args) {
                            if(!(arg.startsWith("<@") && arg.endsWith(">"))) {
                                if(!serverManager.getPermissionManager().playerHasPermission(member.getIdLong(), arg)) {
                                    serverManager.getPermissionManager().playerAddPermission(member.getIdLong(), arg);
                                    permissionSet += arg + ", ";
                                }
                            }
                        }
                        if(!permissionSet.equals("")) {
                            channel.sendMessage(utils.createEmbed(serverManager.getMessage("permit_success_title"), Color.GREEN, serverManager.getMessage("permit_success_body_player").replace("{player}",
                                    member.getEffectiveName()).replace("{permissions}", permissionSet.substring(0, permissionSet.length()-2)))).queue();

                        } else {
                            channel.sendMessage(utils.createEmbed(serverManager.getMessage("permit_error_title"), Color.RED, serverManager.getMessage("permit_error_body_player").replace("{player}", selectedMembers.get(0).getEffectiveName()))).queue();
                            return;
                        }
                    }
                    for(Role role : selectedRoles) {
                        String permissionSet = "";
                        for(String arg : args) {
                            if(!(arg.startsWith("<@") && arg.endsWith(">"))) {
                                if(!serverManager.getPermissionManager().groupHasPermission(role.getIdLong(), arg)) {
                                    serverManager.getPermissionManager().groupAddPermission(role.getIdLong(), arg);
                                    permissionSet += arg + ", ";
                                }
                            }
                        }
                        if(!permissionSet.equals("")) {
                            channel.sendMessage(utils.createEmbed(serverManager.getMessage("permit_success_title"), Color.GREEN, serverManager.getMessage("permit_success_body_group").replace("{group}",
                                    role.getName()).replace("{permissions}", permissionSet.substring(0, permissionSet.length()-2)))).queue();

                        } else {
                            channel.sendMessage(utils.createEmbed(serverManager.getMessage("permit_error_title"), Color.RED, serverManager.getMessage("permit_error_body_group").replace("{group}", role.getName()))).queue();
                        }
                    }
                    return;
                }
            }
        }
        channel.sendMessage(utils.createEmbed(serverManager.getMessage("permit_syntax_title"), Color.RED, serverManager.getMessage("permit_syntax_body").
                replace("{prefix}", serverManager.getBotCommandPrefix()).replace("{trigger}", trigger))).queue();
    }
}
