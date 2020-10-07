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

public class UnpermitCommand extends Command {

    private final ServerManager serverManager;
    private final Utils utils = DiscordBot.getUtils();
    private final static String trigger = "unpermit";

    public UnpermitCommand(ServerManager serverManager) {
        super(serverManager, trigger, serverManager.getMessage("unpermit_description"), "command.unpermit", CommandType.Bot);
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
                        if(isRemoveablePlayer(member)) {
                            String permissionSet = "";
                            for(String arg : args) {
                                if(!(arg.startsWith("<@") && arg.endsWith(">"))) {
                                    if(serverManager.getPermissionManager().playerHasPermission(member.getIdLong(), arg)) {
                                        serverManager.getPermissionManager().playerRemovePermission(member.getIdLong(), arg);
                                        permissionSet += arg + ", ";
                                    }
                                }
                            }
                            if(!permissionSet.equals("")) {
                                channel.sendMessage(utils.createEmbed(serverManager.getMessage("unpermit_default_title"), Color.GREEN, serverManager.getMessage("unpermit_success_body_player").replace("{Player}",
                                        member.getEffectiveName()).replace("{Permissions}", permissionSet.substring(0, permissionSet.length()-2)))).queue();

                            } else {
                                channel.sendMessage(utils.createEmbed(serverManager.getMessage("unpermit_default_title"), Color.RED, serverManager.getMessage("unpermit_not_found_body_player").replace("{Player}", member.getEffectiveName()))).queue();
                            }
                        } else {
                            channel.sendMessage(utils.createEmbed(serverManager.getMessage("unpermit_default_title"), Color.RED, serverManager.getMessage("unpermit_no_perms_body_player").replace("{Player}", member.getEffectiveName()))).queue();
                        }
                    }

                    for(Role role : selectedRoles) {
                        if(isRemoveableGroup(role)) {
                            String permissionSet = "";
                            for(String arg : args) {
                                if(!(arg.startsWith("<@") && arg.endsWith(">"))) {
                                    if(serverManager.getPermissionManager().groupHasPermission(role.getIdLong(), arg)) {
                                        serverManager.getPermissionManager().groupRemovePermission(role.getIdLong(), arg);
                                        permissionSet += arg + ", ";
                                    }
                                }
                            }
                            if(!permissionSet.equals("")) {
                                channel.sendMessage(utils.createEmbed(serverManager.getMessage("unpermit_default_title"), Color.GREEN, serverManager.getMessage("unpermit_success_body_group").replace("{group}",
                                        role.getName()).replace("{Permissions}", permissionSet.substring(0, permissionSet.length()-2)))).queue();

                            } else {
                                channel.sendMessage(utils.createEmbed(serverManager.getMessage("unpermit_default_title"), Color.RED, serverManager.getMessage("unpermit_not_found_body_group").replace("{group}", role.getName()))).queue();
                            }
                        } else {
                            channel.sendMessage(utils.createEmbed(serverManager.getMessage("unpermit_default_title"), Color.RED, serverManager.getMessage("unpermit_no_perms_body_group").replace("{group}", role.getName()))).queue();
                        }
                    }
                    return;

                }

            }
        }
        channel.sendMessage(utils.createEmbed(serverManager.getMessage("unpermit_syntax_title"), Color.RED, serverManager.getMessage("unpermit_syntax_body").
                replace("{prefix}", serverManager.getBotCommandPrefix()).replace("{trigger}", trigger))).queue();
    }

    public boolean isRemoveablePlayer(Member member) {
        if(!serverManager.getConfigManager().getConfig("unpermittableGroup").equals("0")) {
            for(Role role : member.getRoles()) {
                if(role.getIdLong() == Long.parseLong(serverManager.getConfigManager().getConfig("unpermittableGroup"))) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean isRemoveableGroup(Role role) {
        if(!serverManager.getConfigManager().getConfig("unpermittableGroup").equals("0")) {
            return role.getIdLong() != Long.parseLong(serverManager.getConfigManager().getConfig("unpermittableGroup"));
        }
        return true;
    }
}
