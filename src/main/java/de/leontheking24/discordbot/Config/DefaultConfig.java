package de.leontheking24.discordbot.Config;

import java.util.Locale;

public class DefaultConfig {

    //General Config
    public static String botCommandPrefix="!";
    public static String userCommandPrefix="!";
    public static String language=Locale.ENGLISH.toLanguageTag();

    //Command Config
    public static boolean enableClearCommand=true;
    public static boolean enableMuteCommands=true;
    public static boolean enableUserCommands=true;
    public static boolean enablePollCommand=true;
    public static boolean enableReactRoleCommand=true;
    public static String createCommandSeparateKey="\\n";

    //Moderation-Config
    public static boolean enableCapsProtection=true;
    public static boolean enableBlacklist=true;
    public static boolean isCheckingLeetspeakAtBlacklist=true;
    public static int maximumClearAmount=50;
    public static long notificationChannel=123456789876543212l;

    //Spam-Config
    public static boolean enableSpamProtection=true;
    public static int spamCooldown=20;
    public static int messagesInTime=5;
    public static int messageTime=30;

    //Poll-Config
    public static String pollSeparateKey=";";
    public static boolean deletePollAfterFinish=false;

    //Greeting-Config
    public static long greetingChannel=123456789876543212l;
    public static boolean enableJoinMessage=false;
    public static boolean enableLeaveMessage=false;
    public static String customJoinMessage="";
    public static String customLeaveMessage="";

    //Database-Config
    public static boolean isUsingOwnDatabase=false;
    public static String databaseDriver="";
    public static String databaseServer="";
    public static int databasePort=0;
    public static String databaseName="";
    public static String databaseUsername="";
    public static String databasePassword="";

}
