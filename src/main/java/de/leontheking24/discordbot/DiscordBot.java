package de.leontheking24.discordbot;

import de.leontheking24.discordbot.Commands.Base.CommandListener;
import de.leontheking24.discordbot.Commands.BotCommands.RoleAssignment.ReactionListener;
import de.leontheking24.discordbot.Database.Config.DatabaseGlobalConfig;
import de.leontheking24.discordbot.Greeting.GreetingListener;
import de.leontheking24.discordbot.LanguageManager.LanguageManager;
import de.leontheking24.discordbot.Logger.DefaultLogger;
import de.leontheking24.discordbot.Logger.Logger;
import de.leontheking24.discordbot.Moderation.Listener.*;
import de.leontheking24.discordbot.Poll.PollEventListener;
import de.leontheking24.discordbot.Utils.MemoryManager;
import de.leontheking24.discordbot.Utils.Utils;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.SelfUser;

import javax.security.auth.login.LoginException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;

public class DiscordBot {

    public static JDA jda;
    public static String version = "1.0.0";

    private static Logger logger;
    private static ServerLists serverLists;

    private static MemoryManager memoryManager;
    private static Utils utils;
    private static DatabaseGlobalConfig databaseGlobalConfig;
    private static LanguageManager languageManager;

    public static void main(String[] args) throws LoginException {
        new DiscordBot();
        new LanguageManager();
    }

    public DiscordBot() throws LoginException {
        String token = "NzUwMzcxOTMzMDMxMjM1NzQ2.X05kUg.RcH5hJiT7BaiJAk1eXy7akP276A";
        jda = new JDABuilder(AccountType.BOT).setToken(token).build();
        jda.addEventListener(new DiscordJoin());
        logger = new DefaultLogger();
        databaseGlobalConfig = new DatabaseGlobalConfig();
        languageManager = new LanguageManager();
        serverLists = new ServerLists();
        utils = new Utils();
        memoryManager = new MemoryManager();
        initServer();
        memoryManager.startMemoryScheduler();
    }

    public void initServer() {
        jda.getPresence().setStatus(OnlineStatus.IDLE);
        jda.getPresence().setActivity(Activity.playing("Status: Initializing Servers"));
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                int cooldown = 0;
                for(Guild guild : jda.getGuilds()) {
                    if(cooldown <= 20) {
                        try {
                            serverLists.addServer(new ServerManager(guild.getIdLong()));
                            logger.log(Level.INFO, "Server with id " + guild.getIdLong() + " was initialized");
                        } catch (Exception e) {
                            logger.log(Level.WARNING, "Couldn't start the bot for server " + guild.getId());
                        }
                        cooldown++;
                    } else {
                        new Timer().schedule(new TimerTask() {
                            @Override
                            public void run() {
                            }
                        }, 1000);
                        cooldown = 0;
                    }
                }

                goOnline();
                jda.getPresence().setStatus(OnlineStatus.ONLINE);
                jda.getPresence().setActivity(Activity.playing("Status: Online"));
            }
        },1000*2);
    }

    public static ServerLists getServerLists() {
        return serverLists;
    }

    public static ServerManager getServerManager(long serverId) {
        return serverLists.getServerManager(serverId);
    }

    public static JDA getJda() {
        return jda;
    }

    public static Logger getLogger() {
        return logger;
    }

    public static MemoryManager getMemoryManager() {
        return memoryManager;
    }

    public static DatabaseGlobalConfig getDatabaseGlobalConfig() {
        return databaseGlobalConfig;
    }

    public static LanguageManager getLanguageManager() {
        return languageManager;
    }

    public static Utils getUtils() {
        return utils;
    }

    private void goOnline() {
        initEvents();
        logger.log(Level.INFO, "The Bot is now online!");
    }

    private static void initEvents() {
        jda.addEventListener(new CommandListener());
        jda.addEventListener(new ReactionListener());
        jda.addEventListener(new CheckCapslock());
        jda.addEventListener(new PollEventListener());
        jda.addEventListener(new SpamSecure());
        jda.addEventListener(new CheckBlacklist());
        jda.addEventListener(new GreetingListener());
    }

    public static SelfUser getBot() {
        return jda.getSelfUser();
    }

    //embedBuilder.setAuthor(bot.getName(), bot.getEffectiveAvatarUrl(), bot.getEffectiveAvatarUrl());
}
