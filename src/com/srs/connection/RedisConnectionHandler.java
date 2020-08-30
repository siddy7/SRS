//$Id$
package com.srs.connection;

import com.srs.constants.ConfigurationHandler;
import com.srs.constants.ConfigurationHandler.ConfigKey;
import com.srs.exception.ComponentException;

import redis.clients.jedis.Jedis;

public class RedisConnectionHandler {
    private static RedisConnectionHandler INSTANCE = new RedisConnectionHandler();

    Jedis jedisConnection;

    private RedisConnectionHandler() {
    }

    public static RedisConnectionHandler getInstance() {
        return INSTANCE;
    }

    private void initConnection() throws ComponentException {
        jedisConnection = new Jedis(ConfigurationHandler.getInstance().getStringValue(ConfigKey.REDIS_URL),
                ConfigurationHandler.getInstance().getIntValue(ConfigKey.REDIS_PORT));
        jedisConnection.auth(ConfigurationHandler.getInstance().getStringValue(ConfigKey.REDIS_PASSWORD));
    }

    public Jedis getJedisConnection() throws ComponentException {
        if (jedisConnection == null) {
            initConnection();
        }
        return jedisConnection;
    }

}
