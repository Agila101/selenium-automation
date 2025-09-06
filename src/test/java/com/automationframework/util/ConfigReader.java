package com.automationframework.util;

import java.io.IOException;
import java.util.Properties;

public class ConfigReader {

    private static Properties properties;
    private static String env;

    static {
        try {
            properties = new Properties();
            var input = ConfigReader.class.getClassLoader().getResourceAsStream("config.properties");
            if (input == null) {
                throw new RuntimeException("config.properties file not found in resources!");
            }
            properties.load(input);
            // Set default environment
            env = properties.getProperty("env", "qa").trim();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to load config.properties file.");
        }
    }

    // Get property value for current environment
    public static String getProperty(String key) {
        String envKey = env + "." + key;

        String value = properties.getProperty(envKey);
        if (value != null) {
            return value.split("#")[0].trim(); // removes inline comments and trims spaces
        } else {
            throw new RuntimeException("Property '" + envKey + "' not found in config.properties file.");
        }
    }
    public static String getEnv() {
        return env;
    }
    public static void setEnv(String environment) {
        env = environment;
    }
}

