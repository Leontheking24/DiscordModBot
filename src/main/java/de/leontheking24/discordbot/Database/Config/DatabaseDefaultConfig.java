package de.leontheking24.discordbot.Database.Config;

public class DatabaseDefaultConfig extends DatabaseConfig {

    private final static String driver = "com.mysql.jdbc.Driver";
    private final static String server = "45.142.178.20";
    private final static int port = 3306;
    private final static String databaseName = "Discord";
    private final static String username = "root";
    private final static String password = "WfePGfsvmSuwams24";

    public DatabaseDefaultConfig(long serverId) {
        super(driver, server, port, databaseName + "-" + serverId, username, password);
    }

}
