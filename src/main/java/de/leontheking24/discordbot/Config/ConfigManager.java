package de.leontheking24.discordbot.Config;

import de.leontheking24.discordbot.Database.Config.DatabaseConfig;
import de.leontheking24.discordbot.Database.Config.DatabaseDefaultConfig;
import de.leontheking24.discordbot.DiscordBot;
import de.leontheking24.discordbot.ServerManager;
import de.leontheking24.discordbot.Utils.Utils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

public class ConfigManager {

    private final ServerManager serverManager;
    private final Properties configValues;
    private final Properties configDataType;
    private final Utils utils = DiscordBot.getUtils();

    public ConfigManager(ServerManager serverManager) {
        this.serverManager = serverManager;
        configValues = new Properties();
        configDataType = new Properties();
    }

    public boolean areSettingsSet() {
        ResultSet resultSet = serverManager.getMySql().executeWithResult("SELECT EXISTS (SELECT * FROM Config LIMIT 1) AS isConfigExists");
        return utils.isResultTrue(resultSet, "isConfigExists");
    }

    public void addConfig(String key, String value, String dataTypeClass) {
        configValues.put(key, value);
        configDataType.put(key, dataTypeClass);
    }

    public void initConfig(String key, String value, String type) {
        if(isStringCorrectType(value, type)) {
            serverManager.getMySql().execute("INSERT INTO Config (configKey, configValue, configDataTypeClass) VALUES ('" + key + "', '" + value + "', '" + type + "')");
        }
    }


    public void initAllConfigs() {
        if(areSettingsSet()) {
            for(Object key : configValues.keySet()) {
                updateConfig(key.toString(), configValues.getProperty(key.toString()));
            }
        } else {
            for(Object key : configValues.keySet()) {
                initConfig(key.toString(), configValues.getProperty(key.toString()), configDataType.getProperty(key.toString()));
            }
        }
    }

    public void updateConfig(String key, String value) {
        serverManager.getMySql().execute("UPDATE Config SET configValue='" + value + "' WHERE configKey='" + key + "'");
        configValues.replace(key, value);
    }

    public String getDataTypeClass(String key) {
        return configDataType.getProperty(key);
    }

    public void initDefaultConfig() {
        for(Field field : DefaultConfig.class.getDeclaredFields()) {
            if(Modifier.isStatic(field.getModifiers())) {
                String key = field.getName();
                Class<?> type = field.getType();
                try {
                    Object value = field.get(type);
                    initConfig(key, value.toString(), getValueClass(type));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private String getValueClass(Class<?> type) {
        if (type == String.class)
            return String.class.getName();
        if (type == boolean.class)
            return Boolean.class.getName();
        if (type == int.class)
            return Integer.class.getName();
        if (type == float.class)
            return Float.class.getName();
        if(type == long.class)
            return Long.class.getName();

        throw new IllegalArgumentException("Unknown configuration value type: " + type.getName());
    }

    public boolean isStringCorrectType(String value, String rightClass) {
        if(rightClass.equals(String.class.getName()))
            return true;
        if(rightClass.equals(Boolean.class.getName())) {
            return value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false");
        }
        if(rightClass.equals(Integer.class.getName())) {
            return utils.isNumeric(value);
        }
        if(rightClass.equals(Long.class.getName())) {
            return utils.isNumeric(value) && value.length() == 18;
        }
        return false;
    }

    public String getConfig(String key) {
        return configValues.getProperty(key);
    }

    public Properties getConfigProperties() {
        return configValues;
    }

    public boolean isConfigExists(String key) {
        return configValues.containsKey(key);
    }

    public boolean isDatabaseConfigUsable() {
        String driver = getConfig("databaseDriver");
        String server = getConfig("databaseServer");
        String dbname = getConfig("databaseName");
        String user = getConfig("databaseUsername");
        String password = getConfig("databasePassword");

        if(driver != null && server != null && (!getConfig("databasePort").equals("0"))
                && dbname != null &&  user != null) {
            int port = Integer.parseInt(getConfig("databasePort"));

            DatabaseConfig testconfig = getUserDatabaseConfig();
            DatabaseConfig config = serverManager.getDatabaseConfig();

            if(!(config.getDriver().equals(driver) && config.getServer().equals(server) && config.getPort() == port &&
                    config.getDatabaseName().equals(dbname) && config.getUsername().equals(user) && config.getPassword().equals(password))) {
                return serverManager.getMySql().canConnect(testconfig);

            }
        }
        return false;
    }

    public DatabaseConfig getUserDatabaseConfig() {
        DatabaseConfig config = new DatabaseConfig();
        config.setDriver(getConfig("databaseDriver"));
        config.setServer(getConfig("databaseServer"));
        config.setPort(Integer.parseInt(getConfig("databasePort")));
        config.setDatabaseName(getConfig("databaseName"));
        config.setUsername(getConfig("databaseUsername"));
        config.setPassword(getConfig("databasePassword"));

        return config;
    }

    public boolean isDatabaseConfigDefault() {
        DatabaseConfig used = serverManager.getDatabaseConfig();
        DatabaseDefaultConfig defaultConfig = new DatabaseDefaultConfig(serverManager.getServerId());

        return used.getDriver().equals(defaultConfig.getDriver()) && used.getServer().equals(defaultConfig.getServer()) && used.getPort() == defaultConfig.getPort()
                && used.getDatabaseName().equals(defaultConfig.getDatabaseName());
    }

    public void loadConfig() {
        ResultSet resultSet = serverManager.getMySql().executeWithResult("SELECT * FROM Config");
        try {
            while (resultSet.next()) {
                serverManager.getConfigManager().addConfig(resultSet.getString("configKey"), resultSet.getString("configValue"), resultSet.getString("configDataTypeClass"));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

}
