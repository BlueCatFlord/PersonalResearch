package com.schaffer.zookeeper.bridger.zk;

import com.alibaba.fastjson.JSON;
import com.schaffer.zookeeper.bridger.config.Configuration;
import com.schaffer.zookeeper.bridger.conn.ConnectionPool;
import com.schaffer.zookeeper.bridger.lock.LockSynchronize;
import org.I0Itec.zkclient.IZkDataListener;
import org.apache.log4j.Logger;

/**
 * Created by schaffer on 2016/8/17.
 */
public class DBConnDataListener implements IZkDataListener {

    private Logger logger = Logger.getLogger(DBConnDataListener.class);

    private ConnectionPool connectionPool;

    public DBConnDataListener(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    /**
     * monitor the data changed, reconnect the db
     *
     * @param dataPath
     * @param data
     * @throws Exception
     */
    @Override
    public void handleDataChange(String dataPath, Object data) throws Exception {
        logger.info("the data for the path " + dataPath + ",data has already changed");
        //获取新的配置信息
        Configuration configuration = JSON.parseObject((String) data, Configuration.class);
        logger.info("the data has changed,now the configuration is " + configuration.toString());

        //设置写锁
        LockSynchronize.getLock().writeLock().lock();
        //关闭原有连接连接池，释放连接
        connectionPool.close();
        //重新初始化连接
        logger.info("reconnect the mysql and re-initial the connection pool");
        connectionPool.initialConnection(configuration);            //重新连接,reconnect the db
        logger.debug("the handleDataChange is executing,waiting..........");
        //释放写锁
        LockSynchronize.getLock().writeLock().unlock();
    }

    /**
     * monitor the data deleted,disconnect the db
     *
     * @param dataPath
     * @throws Exception
     */
    @Override
    public void handleDataDeleted(String dataPath) throws Exception {
        logger.info("the node for the path: " + dataPath + "has already been deleted");
        connectionPool.close();
    }

}
