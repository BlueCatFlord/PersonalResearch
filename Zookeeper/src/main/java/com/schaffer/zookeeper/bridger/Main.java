package com.schaffer.zookeeper.bridger;


import com.schaffer.zookeeper.bridger.config.ConfigInject;
import com.schaffer.zookeeper.bridger.config.Configuration;
import com.schaffer.zookeeper.bridger.conn.ConnectionPool;
import com.schaffer.zookeeper.bridger.conn.DBOperate;
import com.schaffer.zookeeper.bridger.conn.DBOperateImpl;
import com.schaffer.zookeeper.bridger.except.DBConnException;
import com.schaffer.zookeeper.bridger.zk.DBConnDataListener;
import com.schaffer.zookeeper.bridger.zk.ZkNodeOperate;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by schaffer on 2016/8/18.
 */
public class Main {

    static {
        PropertyConfigurator.configure(Main.class.getClassLoader().getResource("log4j.properties").getFile());
    }

    public static void main(String args[]) throws DBConnException {

        final Logger logger = Logger.getLogger(Main.class);

        String configFile = Main.class.getClassLoader().getResource("config.properties").getFile();
        ConfigInject configInject = ConfigInject.getInstance();
        final Configuration configuration = configInject.properties2Config(configFile);
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        connectionPool.initialConnection(configuration);
        final DBOperate<Integer> dbOperate = new DBOperateImpl(connectionPool);
        Timer operateTimer = new Timer("read write db");
        final Random random = new Random();
        operateTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    logger.info("-------timer insert-----------");
                    boolean status = dbOperate.insert(random.nextInt(100));
                    System.out.println(status);
                } catch (DBConnException e) {
                    e.printStackTrace();
                    System.exit(-1);
                }
            }
        }, 0, 1);

        DBConnDataListener dataListener = new DBConnDataListener(connectionPool);
        final ZkNodeOperate zkNodeOperate = new ZkNodeOperate("192.168.153.128:2181");
        zkNodeOperate.deleteNode("/dbconn", true);
        zkNodeOperate.createNode("/dbconn", configuration.toString());
        zkNodeOperate.setDataListener(dataListener);

        /*Timer dataListenerTimer = new Timer("data listener");
        dataListenerTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                logger.info("---data listener----");
                zkNodeOperate.subscribeNodeDataChanged("/dbconn");
            }
        }, 0, 1000);*/
        zkNodeOperate.subscribeNodeDataChanged("/dbconn");

        Timer configChangeTimer = new Timer("change config");
        configChangeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                logger.info("update the config info,----test the reconnect-------");
                configuration.setUserName("bluefish");
                logger.info("------------the configuration update-------------");
                logger.info(configuration.toString());
                zkNodeOperate.writeNodeData("/dbconn", configuration.toString());
            }
        }, 1 * 1000, 1000 * 1000);
    }
}
