package de.leontheking24.discordbot;

import de.leontheking24.discordbot.Commands.Base.CommandSqlManager;
import de.leontheking24.discordbot.Commands.BotCommands.*;
import de.leontheking24.discordbot.Commands.BotCommands.Permissions.UnpermitCommand;
import de.leontheking24.discordbot.Commands.BotCommands.Permissions.UserInfoCommand;
import de.leontheking24.discordbot.Commands.BotCommands.RoleAssignment.ReactRoleManager;
import de.leontheking24.discordbot.Commands.BotCommands.UserCommands.AddCommand;
import de.leontheking24.discordbot.Commands.BotCommands.UserCommands.InfoCommand;
import de.leontheking24.discordbot.Commands.BotCommands.UserCommands.RemoveCommand;
import de.leontheking24.discordbot.Config.ConfigManager;
import de.leontheking24.discordbot.Config.FileGenerator;
import de.leontheking24.discordbot.Database.Config.DatabaseConfig;
import de.leontheking24.discordbot.Database.Config.DatabaseDefaultConfig;
import de.leontheking24.discordbot.Database.SqlManager;
import de.leontheking24.discordbot.Moderation.Commands.*;
import de.leontheking24.discordbot.Moderation.Manager.MuteManager;
import de.leontheking24.discordbot.Permissions.PermissionManager;
import de.leontheking24.discordbot.Commands.Base.CommandManager;
import de.leontheking24.discordbot.Commands.BotCommands.Permissions.PermitCommand;
import de.leontheking24.discordbot.Commands.BotCommands.RoleAssignment.ReactRoleCommand;
import de.leontheking24.discordbot.Database.MySql;
import de.leontheking24.discordbot.Moderation.Manager.BlacklistManager;
import de.leontheking24.discordbot.Moderation.Manager.SpamManager;
import de.leontheking24.discordbot.Poll.PollCommand;
import de.leontheking24.discordbot.Poll.PollManager;
import de.leontheking24.discordbot.Poll.PollSqlManager;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;

public class ServerManager {

    private final long serverId;
    private String languageKey = "en";

    private MySql mySql;
    private CommandSqlManager commandSqlManager;
    private ConfigManager configManager;
    private PermissionManager permissionManager;
    private CommandManager commandManager;
    private ReactRoleManager reactRoleManager;
    private BlacklistManager blacklistManager;
    private SpamManager spamManager;
    private PollManager pollManager;
    private PollSqlManager pollSqlManager;
    private DatabaseConfig databaseConfig;
    private MuteManager muteManager;
    private FileGenerator fileGenerator;

    public ServerManager(long serverId) {
        this.serverId = serverId;
        initServer();
    }

    public void initServer() {
        initObjects();
        initCommands();
        startCheckTimer();
    }

    private void initObjects() {
        configManager = new ConfigManager(this);
        databaseConfig = new DatabaseConfig();
        commandManager = new CommandManager(this);

        loadCurrentDatabase();
        fileGenerator = new FileGenerator(this);
        permissionManager = new PermissionManager(this);
        reactRoleManager = new ReactRoleManager(this);
        blacklistManager = new BlacklistManager(this);
        spamManager = new SpamManager(this);
        pollManager = new PollManager(this);
        pollSqlManager = new PollSqlManager(this);
        muteManager = new MuteManager(this);

        checkDatabaseUpdate();
    }

    private void loadCurrentDatabase() {
        new SqlManager(DiscordBot.getDatabaseGlobalConfig()).initConfig(this);
        mySql = new MySql(databaseConfig);
        commandSqlManager = new CommandSqlManager(this);
        if(!configManager.areSettingsSet()) {
            configManager.initDefaultConfig();
        }
        configManager.loadConfig();
        if(Boolean.parseBoolean(configManager.getConfig("enableUserCommands"))) {
            commandSqlManager.insertCommands();
        }
    }

    private void checkDatabaseUpdate() {
        DatabaseConfig oldConfig = databaseConfig;
        if(Boolean.parseBoolean(configManager.getConfig("isUsingOwnDatabase"))) {
            if(configManager.isDatabaseConfigUsable()) {
                databaseConfig = configManager.getUserDatabaseConfig();
                new SqlManager(DiscordBot.getDatabaseGlobalConfig()).updateServerConfig(serverId, databaseConfig);
                mySql = new MySql(databaseConfig);
                commandSqlManager = new CommandSqlManager(this);
                configManager.initAllConfigs();
                transferDatabaseData(oldConfig);
            }
            setLanguageKey();
            reactRoleManager.removeList();

        } else {
            if(!configManager.isDatabaseConfigDefault()) {
                databaseConfig = new DatabaseDefaultConfig(serverId);
                new SqlManager(DiscordBot.getDatabaseGlobalConfig()).updateServerConfig(serverId, databaseConfig);
                mySql = new MySql(databaseConfig);
                commandSqlManager = new CommandSqlManager(this);
                configManager.initAllConfigs();
                transferDatabaseData(oldConfig);
            }
        }
        setLanguageKey();
        reactRoleManager.removeList();
    }

    private void transferDatabaseData(DatabaseConfig oldConfig) {
        permissionManager.updateManager(this);
        pollSqlManager = new PollSqlManager(this);

        commandManager.initToDatabase();
        permissionManager.initPermissions();
        pollManager.savePolls();
        blacklistManager.saveBlacklist();
        muteManager.saveMutes();
        reactRoleManager.saveReactRoles();

        new MySql(oldConfig).execute("DROP TABLE BlackListedWords, Commands, Config, Counter, " +
                "GroupPermissions, MutedPlayers, PlayerPermissions, PollReactions, Polls, ReactRoles");
    }

    private void initCommands() {
        commandManager.addCommand(new ReloadServer(this));
        commandManager.addCommand(new ListCommand(this));
        commandManager.addCommand(new HelpCommand(this));
        commandManager.addCommand(new InfoCommand(this));
        commandManager.addCommand(new UnpermitCommand(this));
        commandManager.addCommand(new UserInfoCommand(this));
        commandManager.addCommand(new PermitCommand(this));
        commandManager.addCommand(new ConfigCommand(this));

        if(Boolean.parseBoolean(configManager.getConfig("enableBlacklist"))) {
            commandManager.addCommand(new BlacklistCommand(this));
        }
        if(Boolean.parseBoolean(configManager.getConfig("enableClearCommand"))) {
            commandManager.addCommand(new ClearCommand(this));
        }

        if(Boolean.parseBoolean(configManager.getConfig("enableMuteCommands"))) {
            commandManager.addCommand(new MuteCommand(this));
            commandManager.addCommand(new UnmuteCommand(this));
            commandManager.addCommand(new MuteInfoCommand(this));
        }

        if(Boolean.parseBoolean(configManager.getConfig("enablePollCommand"))) {
            commandManager.addCommand(new PollCommand(this));
        }

        if(Boolean.parseBoolean(configManager.getConfig("enableReactRoleCommand"))) {
            commandManager.addCommand(new ReactRoleCommand(this));
        }

        if(Boolean.parseBoolean(configManager.getConfig("enableUserCommands"))) {
            commandManager.addCommand(new AddCommand(this));
            commandManager.addCommand(new RemoveCommand(this));
        }
    }

    public long getServerId() {
        return serverId;
    }

    public String getBotCommandPrefix() {
        return configManager.getConfig("botCommandPrefix");
    }

    public String getUserCommandPrefix() {
        return configManager.getConfig("userCommandPrefix");
    }

    public void setLanguageKey() {
        String language = configManager.getConfig("language");
        if(language.equalsIgnoreCase("de") || language.equalsIgnoreCase("en")) {
            languageKey = language.toLowerCase();
        }
    }

    public MySql getMySql() {
        return mySql;
    }

    public CommandSqlManager getCommandSqlManager() {
        return commandSqlManager;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public PermissionManager getPermissionManager() {
        return permissionManager;
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }

    public ReactRoleManager getRoleAssignmentManager() {
        return reactRoleManager;
    }

    public BlacklistManager getModManager() {
        return blacklistManager;
    }

    public SpamManager getSpamManager() {
        return spamManager;
    }

    public PollManager getPollManager() {
        return pollManager;
    }

    public PollSqlManager getPollSqlManager() {
        return pollSqlManager;
    }

    public DatabaseConfig getDatabaseConfig() {
        return databaseConfig;
    }

    public MuteManager getMuteManager() {
        return muteManager;
    }

    public FileGenerator getFileGenerator() {
        return fileGenerator;
    }


    public Guild getGuild() {
        for(Guild guild : DiscordBot.getJda().getGuilds()) {
            if(guild.getIdLong() == serverId) {
                return guild;
            }
        }
        return null;
    }

    public void startCheckTimer() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                pollManager.checkEndTimer();
                muteManager.checkMuteEnd();
            }
        }, 10*1000, 60*1000);
        DiscordBot.getLogger().log(Level.INFO, "The Check-Schedule has been started");
    }

    public String getMessage(String messageKey) {
        return DiscordBot.getLanguageManager().getMessage(messageKey, languageKey);
    }

    public TextChannel getNotificationChannel() {
        TextChannel channel = DiscordBot.getJda().getTextChannelById(configManager.getConfig("notificationChannel"));
        if(channel != null) {
            return channel;
        }
        return getGuild().getDefaultChannel();
    }
}
