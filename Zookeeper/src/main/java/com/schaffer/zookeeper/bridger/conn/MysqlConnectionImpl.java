package com.schaffer.zookeeper.bridger.conn;


import com.schaffer.zookeeper.bridger.config.ConfigInject;
import com.schaffer.zookeeper.bridger.config.Configuration;
import com.schaffer.zookeeper.bridger.except.DBConnException;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by schaffer on 2016/8/17.
 */
public class MysqlConnectionImpl implements DBConnection {

    private static Logger logger = Logger.getLogger(MysqlConnectionImpl.class);

    private static volatile Connection connection;

    public MysqlConnectionImpl() {
    }

    @Override
    public boolean connect(Configuration configuration) throws DBConnException {

        String connectionUlr = connectionUrl(configuration);
        System.out.println(connectionUlr);

        //create the connection
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(connectionUlr);
            if (!connection.isClosed()) {
                logger.info("connect the mysql success!!!!");
                return true;
            } else {
                logger.info("connect the mysql fail!!!!");
                return false;
            }
        } catch (ClassNotFoundException | SQLException e) {
            logger.error(e.getMessage());
            throw new DBConnException(e);
        }
    }

    @Override
    public void close() throws DBConnException {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                logger.error(e.getMessage());
                throw new DBConnException(e);
            }
        } else {
            logger.error("the mysql connection has not been initialized");
            return;
        }
    }

    @Override
    public boolean isOpen() throws DBConnException {
        if (connection != null) {
            try {
                return !connection.isClosed();
            } catch (SQLException e) {
                logger.error(e.getMessage());
                throw new DBConnException(e);
            }
        } else {
            logger.error("the mysql connection has not been initialized");
            return false;
        }
    }

    public static Connection getConnection() {
        return connection;
    }

    public static String connectionUrl(Configuration configuration) throws DBConnException {
        if (configuration == null) {
            throw new DBConnException("the configuration info is error");
        }

        String serverAddress = configuration.getServerAddress();
        int port = configuration.getPort();
        int maxConnections = configuration.getMaxConnections();
        String userName = configuration.getUserName();
        String password = configuration.getPassword();
        String characterEncoding = configuration.getCharacterEncoding();
        boolean autoReconnect = configuration.isAutoReconnect();
        boolean autoReconnectForPools = configuration.isAutoReconnectForPools();
        int maxReconnects = configuration.getMaxReconnects();
        int initialTimeout = configuration.getInitialTimeout();
        int connectionTimeout = configuration.getConnectTimeout();
        int socketTimeout = configuration.getSocketTimeout();
        String database = configuration.getDatabase();

        String connectionUlr = "jdbc:mysql://" + serverAddress + ":" + port + "/" + database + "?"
                + "user=" + userName + "&password=" + password + "&maxConnections=" + maxConnections
                + "&characterEncoding=" + characterEncoding + "&autoReconnect=" + autoReconnect
                + "&autoReconnectForPools=" + autoReconnectForPools + "&maxReconnects=" + maxReconnects
                + "&initialTimeout=" + initialTimeout + "&connectionTimeout=" + connectionTimeout
                + "&socketTimeout=" + socketTimeout;
        logger.info("the connection url is:" + connectionUlr);
        return connectionUlr;
    }


    public static void main(String args[]) {
        String configFile = MysqlConnectionImpl.class.getClassLoader().getResource("config.properties").getFile();
        ConfigInject configInject = ConfigInject.getInstance();
        Configuration configuration = configInject.properties2Config(configFile);
        System.out.println(configuration.toString());
        DBConnection mysqlConnection = new MysqlConnectionImpl();
        try {
            mysqlConnection.connect(configuration);
        } catch (DBConnException e) {
            e.printStackTrace();
        }
    }
}
