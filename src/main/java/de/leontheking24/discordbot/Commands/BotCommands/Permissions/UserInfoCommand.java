package de.leontheking24.discordbot.Commands.BotCommands.Permissions;

import de.leontheking24.discordbot.Commands.Base.Command;
import de.leontheking24.discordbot.Commands.Base.CommandType;
import de.leontheking24.discordbot.DiscordBot;
import de.leontheking24.discordbot.ServerManager;
import de.leontheking24.discordbot.Utils.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;

import java.awt.*;

public class UserInfoCommand extends Command {

    private final ServerManager serverManager;
    private final static String trigger = "permissions";
    private final Utils utils = DiscordBot.getUtils();

    public UserInfoCommand(ServerManager serverManager) {
        super(serverManager, trigger,serverManager.getMessage("userinfo_description"), "command.permissions", CommandType.Bot);
        this.serverManager = serverManager;
    }

    @Override
    public void executeCommand(TextChannel channel, Message message) {
        if(!isArgumentLengthZero(message)) {
            if(message.getMentionedMembers().size() > 0) {
                Member member = message.getMentionedMembers().get(0);

                EmbedBuilder embed = new EmbedBuilder();
                embed.setTitle(serverManager.getMessage("userinfo_success_title_player").replace("{Player}", member.getEffectiveName())).setColor(Color.MAGENTA);
                embed.setDescription(serverManager.getMessage("userinfo_success_noperms_player").replace("{Player}", member.getEffectiveName()));
                if(serverManager.getPermissionManager().getAllPlayerPermissions(member.getIdLong()).size() > 0) {
                    embed.setDescription(serverManager.getMessage("userinfo_success_perms_player").replace("{Player}", member.getEffectiveName()));
                    for(String permission : serverManager.getPermissionManager().getAllPlayerPermissions(member.getIdLong())) {
                        embed.appendDescription("\n > ➝ **" + permission + "**");
                    }
                }
                channel.sendMessage(embed.build()).queue();
                return;

            } else if(message.getMentionedRoles().size() > 0) {
                Role role = message.getMentionedRoles().get(0);
                EmbedBuilder embed = new EmbedBuilder();
                embed.setTitle(serverManager.getMessage("userinfo_success_title_group").replace("{group}", role.getName())).setColor(Color.MAGENTA);
                embed.setDescription(serverManager.getMessage("userinfo_success_noperms_group").replace("{group}", role.getName()));
                if(serverManager.getPermissionManager().getAllGroupPermissions(role.getIdLong()).size() > 0) {
                    embed.setDescription(serverManager.getMessage("userinfo_success_perms_group").replace("{group}", role.getName()));
                    for(String permission : serverManager.getPermissionManager().getAllGroupPermissions(role.getIdLong())) {
                        embed.appendDescription("\n > ➝ **" + permission + "**");
                    }
                }
                channel.sendMessage(embed.build()).queue();
                return;
            }
        }
        channel.sendMessage(utils.createEmbed(serverManager.getMessage("userinfo_syntax_title"), Color.RED, serverManager.getMessage("userinfo_syntax_body").
                replace("{prefix}", serverManager.getBotCommandPrefix()).replace("{trigger}", trigger))).queue();
    }
}
