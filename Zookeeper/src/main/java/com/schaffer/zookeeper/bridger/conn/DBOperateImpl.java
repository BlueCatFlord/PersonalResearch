package com.schaffer.zookeeper.bridger.conn;


import com.schaffer.zookeeper.bridger.config.ConfigInject;
import com.schaffer.zookeeper.bridger.config.Configuration;
import com.schaffer.zookeeper.bridger.except.DBConnException;
import com.schaffer.zookeeper.bridger.lock.LockSynchronize;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by schaffer on 2016/8/18.
 */
public class DBOperateImpl implements DBOperate<Integer> {

    private Logger logger = Logger.getLogger(DBOperateImpl.class);

    private ConnectionPool connectionPool;

    public DBOperateImpl(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    @Override
    public boolean insert(Integer data) throws DBConnException {
        logger.debug("test insert data test");
        LockSynchronize.getLock().writeLock().lock();
        String sql = "INSERT INTO test " + " VALUES (?)";
        boolean statusResult = false;
        Connection connection = null;
        PreparedStatement pst = null;
        try {
            if (find(data)) {
                logger.info("the data " + data + " has already exists!!!");
                statusResult = false;
            } else {
                connection = connectionPool.getConnection();
                pst = connection.prepareStatement(sql);
                pst.setInt(1, data);
                int number = pst.executeUpdate();
                if (number == -1) {
                    logger.error("insert mysql error,data = " + data);
                } else {
                    statusResult = true;
                }
            }
        } catch (SQLException e) {
            logger.error(e.getMessage());
            throw new DBConnException(e);
        } finally {
            if (pst != null && connection != null) {
                try {
                    pst.close();
                    connection.close();
                } catch (SQLException e) {
                    throw new DBConnException(e);
                }
            }
            LockSynchronize.getLock().writeLock().unlock();
        }
        return statusResult;
    }

    @Override
    public boolean find(Integer query) throws DBConnException {
        logger.debug("test find data test");
        LockSynchronize.getLock().readLock().lock();
        String sql = "select * from test where id=?";
        boolean result = false;
        Connection connection = null;
        PreparedStatement pst = null;
        try {
            connection = connectionPool.getConnection();
            pst = connection.prepareStatement(sql);
            pst.setInt(1, query);
            ResultSet resultSet = pst.executeQuery();
            if (resultSet.next()) {
                result = true;
            } else {
                result = false;                        //return null,find no result
            }
        } catch (SQLException e) {
            logger.error(e.getMessage());
            throw new DBConnException(e);
        } finally {
            if (pst != null && connection != null) {
                try {
                    pst.close();
                    connection.close();
                } catch (SQLException e) {
                    throw new DBConnException(e);
                }
            }
            LockSynchronize.getLock().readLock().unlock();
        }
        return result;
    }

    @Override
    public boolean delete(Integer query) {
        return false;
    }

    @Override
    public boolean update(Integer query, Integer data) {
        return false;
    }

    public static void main(String args[]) throws DBConnException {
        String configFile = DBOperateImpl.class.getClassLoader().getResource("config.properties").getFile();
        Configuration configuration = ConfigInject.getInstance().properties2Config(configFile);
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        connectionPool.initialConnection(configuration);
        DBOperate<Integer> dbOperate = new DBOperateImpl(connectionPool);
        boolean result = dbOperate.insert(1);
        System.out.println(result);

    }
}
