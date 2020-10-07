package de.leontheking24.discordbot.Database.Config;

public class DatabaseConfig {

    private String driver;
    private String server;
    private int port;
    private String databaseName;
    private String username;
    private String password;

    public DatabaseConfig() {
    }

    public DatabaseConfig(String driver, String server, int port, String databaseName, String username, String password) {
        this.driver = driver;
        this.server = server;
        this.port = port;
        this.databaseName = databaseName;
        this.username = username;
        this.password = password;
    }

    public String getDriver() {
        return driver;
    }

    public String getServer() {
        return server;
    }

    public int getPort() {
        return port;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
