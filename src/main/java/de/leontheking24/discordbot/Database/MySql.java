package de.leontheking24.discordbot.Database;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import de.leontheking24.discordbot.Database.Config.DatabaseConfig;
import de.leontheking24.discordbot.DiscordBot;

import java.sql.*;
import java.util.logging.Level;

public class MySql {

    private Connection connection;
    private Statement statement;
    private final DatabaseConfig config;

    public MySql(DatabaseConfig config) {
        this.config = config;
        connection = connection();
        try {
            statement = connection.createStatement();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
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
            DiscordBot.getLogger().log(Level.WARNING, e.getMessage());
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

        } catch (ClassNotFoundException | SQLException e) {
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
