//$Id$
package com.srs.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.srs.constants.ConfigurationHandler;
import com.srs.constants.ConfigurationHandler.ConfigKey;
import com.srs.exception.ComponentException;
import com.srs.exception.ErrorCode.ComponentErrorCode;

public class MySQLConnectionHandler {
    private static final MySQLConnectionHandler INSTANCE = new MySQLConnectionHandler();
    Connection connection;

    private MySQLConnectionHandler() {

    }

    public static MySQLConnectionHandler getInstance() {
        return INSTANCE;
    }

    private void initConnection() throws ComponentException {
        try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			throw new ComponentException(ComponentErrorCode.MYSQL_CONNECTOR_NOT_LOADED);
		}
        String mysqlURI = ConfigurationHandler.getInstance().getStringValue(ConfigKey.MYSQL_URI);
        String userName = ConfigurationHandler.getInstance().getStringValue(ConfigKey.MYSQL_USER);
        String password = ConfigurationHandler.getInstance().getStringValue(ConfigKey.MYSQL_PASSWORD);
        String dbName = ConfigurationHandler.getInstance().getStringValue(ConfigKey.MYSQL_DBNAME);
        mysqlURI += "/" + dbName;
        try {
			connection = DriverManager.getConnection("jdbc:" + mysqlURI, userName, password);
		} catch (SQLException e) {
			throw new ComponentException(ComponentErrorCode.ERROR_INITIALIZING_MYSQL_CONNECTION);
		}
    }

    public Connection getConnection() throws ComponentException {
        if (connection == null) {
            initConnection();
        }
        return connection;
    }


}