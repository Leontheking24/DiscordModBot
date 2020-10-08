package de.leontheking24.discordbot.Database;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import de.leontheking24.discordbot.Database.Config.DatabaseConfig;

import java.sql.*;

public class MySql {

    private Connection connection;
    private Statement statement;
    private DatabaseConfig config;

    public MySql(DatabaseConfig config) {
        this.config = config;
        connection = connection();
        try {
            statement = connection.createStatement();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        execute("CREATE TABLE IF NOT EXISTS BlackListedWords(value VARCHAR(100) NOT NULL PRIMARY KEY)");
        execute("CREATE TABLE IF NOT EXISTS Config (configKey VARCHAR(100) PRIMARY KEY NOT NULL, configValue VARCHAR(100) NOT NULL, configDataTypeClass VARCHAR(100) NOT NULL)");
        execute("CREATE TABLE IF NOT EXISTS MutedPlayers (playerId bigint PRIMARY KEY NOT NULL, muteEnd datetime NOT NULL, reason VARCHAR(200))");
    }

    public Connection connection() {
        try {
            Class.forName(config.getDriver());
            MysqlDataSource dataSource = new MysqlDataSource();
            dataSource.setServerName(config.getServer());
            dataSource.setPort(config.getPort());
            dataSource.setServerTimezone("Europe/Berlin");
            dataSource.setDatabaseName(config.getDatabaseName());
            dataSource.setUser(config.getUsername());
            dataSource.setPassword(config.getPassword());

            connection = dataSource.getConnection();

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }

    public boolean canConnect(DatabaseConfig config) {
        try {
            Class.forName(config.getDriver());
            MysqlDataSource dataSource = new MysqlDataSource();
            dataSource.setServerName(config.getServer());
            dataSource.setPort(config.getPort());
            dataSource.setServerTimezone("Europe/Berlin");
            dataSource.setDatabaseName(config.getDatabaseName());
            dataSource.setUser(config.getUsername());
            dataSource.setPassword(config.getPassword());
            connection = dataSource.getConnection();

        } catch (ClassNotFoundException e) {
            return false;
        } catch (SQLException throwables) {
            return false;
        }

        return true;
    }

    public void execute(String sql) {
        try {
            statement.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ResultSet executeWithResult(String sql) {
        try {
            return statement.executeQuery(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

}
