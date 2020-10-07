package de.leontheking24.discordbot.Database;

import de.leontheking24.discordbot.Database.Config.DatabaseConfig;
import de.leontheking24.discordbot.DiscordBot;
import de.leontheking24.discordbot.ServerManager;
import de.leontheking24.discordbot.Utils.Utils;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SqlManagerNew {

    private MySql mySql;
    private DatabaseConfig config;
    private Utils utils = DiscordBot.getUtils();

    public SqlManagerNew(DatabaseConfig config) {
        this.config = config;
        mySql = new MySql(config);
    }


    public void initConfig(ServerManager serverManager) {
        mySql.execute("CREATE TABLE IF NOT EXISTS Config(serverId bigint PRIMARY KEY NOT NULL, databaseDriver VARCHAR(100) NOT NULL, databaseServer VARCHAR(100) NOT NULL, " +
                "databasePort smallint NOT NULL, databaseName VARCHAR(50) NOT NULL, databaseUsername VARBINARY(100) NOT NULL, databasePassword VARBINARY(200))");
        long serverId = serverManager.getServerId();

        if(checkConfigExists(serverId)) {
            ResultSet resultSet = mySql.executeWithResult("SELECT databaseDriver, databaseServer, databasePort, databaseName, " +
                    "AES_DECRYPT(databaseUsername, 'jbW3ElDw82') AS databaseUsername, AES_DECRYPT(databasePassword, 'jbW3ElDw82') AS databasePassword FROM Config WHERE serverId = '" + serverId + "'");
            try {
                while (resultSet.next()) {
                    serverManager.getDatabaseConfig().setDriver(resultSet.getString("databaseDriver"));
                    serverManager.getDatabaseConfig().setServer(resultSet.getString("databaseServer"));
                    serverManager.getDatabaseConfig().setPort(resultSet.getInt("databasePort"));
                    serverManager.getDatabaseConfig().setDatabaseName(resultSet.getString("databaseName"));
                    serverManager.getDatabaseConfig().setUsername(resultSet.getString("databaseUsername"));
                    serverManager.getDatabaseConfig().setPassword(resultSet.getString("databasePassword"));
                }

            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        } else {
            mySql.execute("INSERT INTO Config (serverId, databaseDriver, databaseServer, databasePort, databaseName, databaseUsername, databasePassword) VALUES " +
                    "('" + serverId + "', 'com.mysql.jdbc.Driver', '45.142.178.20', '3306', 'Discord-" + serverId + "', AES_ENCRYPT('Leon', 'jbW3ElDw82'), AES_ENCRYPT('WfePGfsvmSuwams24', 'jbW3ElDw82'))");
            mySql.execute("CREATE DATABASE IF NOT EXISTS `Discord-" + serverId + "`;");
            initConfig(serverManager);
        }
    }

    public void updateServerConfig(long serverId, DatabaseConfig config) {
        mySql.execute("UPDATE Config SET databaseDriver='" + config.getDriver() + "', databaseServer='" + config.getServer() + "', databasePort='" + config.getPort() + "', databaseName='" + config.getDatabaseName() +
                "', databaseUsername=AES_ENCRYPT('" + config.getUsername() + "', 'jbW3ElDw82'), databasePassword=AES_ENCRYPT('" + config.getPassword() + "', 'jbW3ElDw82') WHERE serverId='" + serverId + "'");
    }

    private boolean checkConfigExists(long serverId) {
        ResultSet resultSet = mySql.executeWithResult("SELECT EXISTS(SELECT 1 FROM Config WHERE serverId='" + serverId + "' LIMIT 1) AS ConfigExists");
        return utils.isResultTrue(resultSet, "ConfigExists");
    }

}
