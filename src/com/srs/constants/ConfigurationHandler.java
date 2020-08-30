//$Id$
package com.srs.constants;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.srs.exception.ComponentException;
import com.srs.exception.ErrorCode.ComponentErrorCode;

public class ConfigurationHandler {

    private static final String CONFIG_PATH = "/Users/siddha-5197/Documents/SeatReservationSystem/config.json";

    public enum ConfigKey {
        MYSQL_URI("mysql_uri"),
        MYSQL_USER("mysql_user"),
        MYSQL_PASSWORD("mysql_password"),
        MYSQL_DBNAME("mysql_dbname"),
        SEAT_PAYMENT_THRESHOLD_TIME_SEC("seat_payment_threshold_time_sec"),
        REDIS_URL("redis_url"),
        REDIS_PORT("redis_port"),
        REDIS_PASSWORD("redis_password"),
        ;

        String key;

        ConfigKey(String key) {
            this.key = key;
        }

        public String getKey() {
            return key;
        }
    }

    private JSONObject configuration;

    private static final ConfigurationHandler INSTANCE = new ConfigurationHandler();

    private ConfigurationHandler() {
    }

    public static ConfigurationHandler getInstance() {
        return INSTANCE;
    }

    private void loadConfigMap() throws ComponentException {
        JSONParser parser = new JSONParser();
        try {
			configuration = (JSONObject) parser.parse(new FileReader(new File(CONFIG_PATH)));
		} catch (IOException | ParseException e) {
			throw new ComponentException(ComponentErrorCode.ERROR_LOADING_CONFIGURATION_FILE, e);
		}
    }

    public String getStringValue(ConfigKey configKey) throws ComponentException {
        if (configuration == null) {
            loadConfigMap();
        }
        if (configuration.containsKey(configKey.getKey())) {
            return (String) configuration.get(configKey.getKey());
        }
        throw new ComponentException(ComponentErrorCode.CONFIGURATION_KEY_NOT_LOADED);
    }

    public long getLongValue (ConfigKey configKey) throws ComponentException {
        if (configuration == null) {
            loadConfigMap();
        }
        if (configuration.containsKey(configKey.getKey())) {
            return (long) configuration.get(configKey.getKey());
        }
        throw new ComponentException(ComponentErrorCode.CONFIGURATION_KEY_NOT_LOADED);
    }

    public int getIntValue (ConfigKey configKey) throws ComponentException {
        if (configuration == null) {
            loadConfigMap();
        }
        if (configuration.containsKey(configKey.getKey())) {
            return ((Long)configuration.get(configKey.getKey())).intValue();
        }
        throw new ComponentException(ComponentErrorCode.CONFIGURATION_KEY_NOT_LOADED);
    }
}
