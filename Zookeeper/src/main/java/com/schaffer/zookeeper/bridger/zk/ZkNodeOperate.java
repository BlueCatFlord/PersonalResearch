package com.schaffer.zookeeper.bridger.zk;

import com.alibaba.fastjson.JSON;
import com.schaffer.zookeeper.bridger.config.ConfigInject;
import com.schaffer.zookeeper.bridger.config.Configuration;
import org.I0Itec.zkclient.ZkClient;
import org.apache.log4j.Logger;
import org.apache.zookeeper.CreateMode;

/**
 * Created by schaffer on 2016/8/17.
 */
public class ZkNodeOperate {

    private Logger logger = Logger.getLogger(ZkNodeOperate.class);

    private ZkClient zkClient;

    private DBConnDataListener dataListener;

    public ZkNodeOperate(String serverAddress) {
        logger.info("------initial the zkClient------");
        this.zkClient = new ZkClient(serverAddress);
    }

    /**
     * confirm the node exist or not
     *
     * @param path
     */
    public boolean exist(String path) {
        return zkClient.exists(path);
    }

    /**
     * create the node with data
     *
     * @param path
     * @param data
     * @return
     */
    public boolean createNode(String path, Object data) {
        if (exist(path)) {
            logger.info("the node for the path: " + path + " has already exists");
            return false;
        }
        String nodePath = zkClient.create(path, data, CreateMode.PERSISTENT);
        return (nodePath == null) ? false : true;
    }

    /**
     * create node without data
     *
     * @param path
     * @return
     */
    public boolean createNode(String path) {
        return createNode(path, null);
    }


    /**
     * write data info the node
     *
     * @param path
     * @param data
     * @return
     */
    public boolean writeNodeData(String path, Object data) {
        if (!exist(path)) {
            logger.info("the node for the path " + path + " does not exists");
            return createNode(path, data);
        }
        zkClient.writeData(path, data);
        return true;
    }

    /**
     * @param path
     * @param deleteRecursive
     * @return
     */
    public boolean deleteNode(String path, boolean deleteRecursive) {
        if (!exist(path)) {
            logger.info("the node for the path: " + path + " does not exists!!!");
            return false;
        }
        //递归删除节点
        if (deleteRecursive) {
            return zkClient.deleteRecursive(path);
        } else {
            return zkClient.delete(path);
        }
    }

    /**
     * 读取节点存储的数据
     *
     * @param path
     * @return
     */
    public String readNode(String path) {
        if (!exist(path)) {
            logger.info("the node for the path: " + path + " does not exist!!!!");
            return null;
        }
        String nodeInfo = zkClient.readData(path, false);
        return nodeInfo;
    }

    /**
     * monitor the stat of data for the given path
     *
     * @param path
     */
    public void subscribeNodeDataChanged(String path) {
        if (!exist(path)) {
            logger.info("the node for the path " + path + " does not exist");
            return;
        }

        zkClient.subscribeDataChanges(path, dataListener);
    }

    public DBConnDataListener getDataListener() {
        return dataListener;
    }

    public void setDataListener(DBConnDataListener dataListener) {
        this.dataListener = dataListener;
    }

    public static void main(String args[]) {
        String configFile = ZkNodeOperate.class.getClassLoader().getResource("config.properties").getFile();
        ConfigInject configInject = ConfigInject.getInstance();
        Configuration configuration = configInject.properties2Config(configFile);
        ZkNodeOperate zkNodeOperate = new ZkNodeOperate("192.168.153.128:2181");
        zkNodeOperate.writeNodeData("/dbconn", configuration.toString());

        String nodeInfo = zkNodeOperate.readNode("/dbconn");
        configuration = JSON.parseObject(nodeInfo, Configuration.class);
        System.out.println(configuration.toString());
    }
}
