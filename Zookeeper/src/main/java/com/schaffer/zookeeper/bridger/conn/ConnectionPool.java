package com.schaffer.zookeeper.bridger.conn;


import com.schaffer.zookeeper.bridger.config.ConfigInject;
import com.schaffer.zookeeper.bridger.config.Configuration;
import com.schaffer.zookeeper.bridger.except.DBConnException;
import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * 采用单例模式
 * <p/>
 * Created by schaffer on 2016/8/19.
 */
public class ConnectionPool {

    private Logger logger = Logger.getLogger(ConnectionPool.class);

    private static ConnectionPool connectionPool;

    private BasicDataSource dataSource;

    private volatile boolean initialFlag = false;

    private ConnectionPool() {
    }

    /**
     * 单例模式
     *
     * @return
     */
    public static synchronized ConnectionPool getInstance() {
        if (connectionPool == null) {
            connectionPool = new ConnectionPool();
        }
        return connectionPool;
    }

    /**
     * 初始化connection pool
     *
     * @param configuration
     * @throws DBConnException
     */
    public void initialConnection(Configuration configuration) throws DBConnException {
        if (initialFlag) {
            String message = "the connection pool has already been initialed";
            logger.warn(message);
            throw new DBConnException(message);
        }
        this.dataSource = new BasicDataSource();
        String driverClassName = "com.mysql.jdbc.Driver";
        String userName = configuration.getUserName();
        String password = configuration.getPassword();
        int initialConnectionSize = configuration.getInitialConnectionSize();
        int maxActive = configuration.getMaxActiveConnection();
        int maxIdle = configuration.getMaxIdleConnection();
        int minIdle = configuration.getMinIdle();
        int maxWait = configuration.getMaxWaiting();
        String characterEncoding = configuration.getCharacterEncoding();
        String connectionUrl = "jdbc:mysql://" + configuration.getServerAddress() + ":"
                + configuration.getPort() + "/" + configuration.getDatabase() +
                "?characterEncoding=" + characterEncoding;

        dataSource.setDriverClassName(driverClassName);
        dataSource.setUrl(connectionUrl);
        dataSource.setUsername(userName);
        dataSource.setPassword(password);
        dataSource.setInitialSize(initialConnectionSize);
        dataSource.setMaxIdle(maxIdle);
        dataSource.setMaxTotal(maxActive);
        dataSource.setMaxWaitMillis(maxWait * 1000);
        dataSource.setMinIdle(minIdle);

        initialFlag = true;
    }

    /**
     * 连接池获取连接
     *
     * @return
     * @throws DBConnException
     */
    public Connection getConnection() throws DBConnException {
        if (initialFlag) {
            logger.info("get the connection for mysql");
            Connection connection;
            try {
                connection = dataSource.getConnection();
                logger.debug("the total connection num is " + dataSource.getMaxTotal());
                logger.debug("the active connections is " + dataSource.getNumActive());
                logger.debug("the idle connections is " + dataSource.getNumIdle());
                if (dataSource.getNumIdle() == 0) {
                    logger.warn("the idle connection is empty");
                    throw new DBConnException("the idle connection is empty");
                }
                return connection;
            } catch (SQLException e) {
                throw new DBConnException(e);
            }
        } else {
            throw new DBConnException("the connection has not been initialed");
        }
    }

    public void close() throws DBConnException {
        logger.info("close the connection pool");
        if (initialFlag) {
            try {
                dataSource.close();
            } catch (SQLException e) {
                throw new DBConnException(e);
            } finally {
                initialFlag = false;
            }
        }
    }

    public static void main(String args[]) {
        String logFile = ConnectionPool.class.getClassLoader().getResource("log4j.properties").getFile();
        PropertyConfigurator.configure(logFile);
        try {
            String connectionFile = ConnectionPool.class.getClassLoader().getResource("config.properties").getFile();
            ConfigInject configInject = ConfigInject.getInstance();
            Configuration configuration = configInject.properties2Config(connectionFile);
            System.out.println(configuration.toString());
            ConnectionPool connectionPool = ConnectionPool.getInstance();
            connectionPool.initialConnection(configuration);
            for (int i = 0; i <= 20; i++) {
                Connection connection = connectionPool.getConnection();
                //connection.close();
            }
        } catch (DBConnException e) {
            e.printStackTrace();
        } /*catch (SQLException e) {
            e.printStackTrace();
        }*/
    }
}
