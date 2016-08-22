package com.schaffer.zookeeper.bridger.config;

import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.SerializeWriter;

import java.io.Serializable;

/**
 * Created by schaffer on 2016/8/17.
 * <p/>
 * 配置项，给出默认值
 */
public class Configuration implements Serializable {

    private static final String APP_NAME = "Reconnect DB Based on Zookeeper";

    private String serverAddress = "0.0.0.0";           //default value

    private int port = 3306;                            //default value

    private int maxConnections = 200;

    private String userName = "liuxiaofei";

    private String password = "liuxiaofei";

    private int initialConnectionSize = 20;

    private int maxActiveConnection = 15;

    private int maxIdleConnection = 5;

    private int minIdle = 3;

    private int maxWaiting = 5000;

    private String characterEncoding = "UTF-8";

    private boolean autoReconnect = true;              //reconnect the db

    private boolean autoReconnectForPools = true;     //reconnect the db based on connection pools

    private int maxReconnects = 5;                    //重连次数

    private int initialTimeout = 1;                   //the interval time between the reconnect

    private int connectTimeout = 5000;                //limit the connect timeout

    private int socketTimeout = 10000;                //session timeout

    private String database = "test";                 //the db for connecting

    public int getMaxActiveConnection() {
        return maxActiveConnection;
    }

    public void setMaxActiveConnection(int maxActiveConnection) {
        this.maxActiveConnection = maxActiveConnection;
    }

    public int getMaxIdleConnection() {
        return maxIdleConnection;
    }

    public void setMaxIdleConnection(int maxIdleConnection) {
        this.maxIdleConnection = maxIdleConnection;
    }

    public int getMinIdle() {
        return minIdle;
    }

    public void setMinIdle(int minIdle) {
        this.minIdle = minIdle;
    }

    public int getMaxWaiting() {
        return maxWaiting;
    }

    public void setMaxWaiting(int maxWaiting) {
        this.maxWaiting = maxWaiting;
    }

    public String getServerAddress() {
        return serverAddress;
    }

    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getMaxConnections() {
        return maxConnections;
    }

    public void setMaxConnections(int maxConnections) {
        this.maxConnections = maxConnections;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCharacterEncoding() {
        return characterEncoding;
    }

    public void setCharacterEncoding(String characterEncoding) {
        this.characterEncoding = characterEncoding;
    }

    public boolean isAutoReconnect() {
        return autoReconnect;
    }

    public void setAutoReconnect(boolean autoReconnect) {
        this.autoReconnect = autoReconnect;
    }

    public boolean isAutoReconnectForPools() {
        return autoReconnectForPools;
    }

    public void setAutoReconnectForPools(boolean autoReconnectForPools) {
        this.autoReconnectForPools = autoReconnectForPools;
    }

    public int getMaxReconnects() {
        return maxReconnects;
    }

    public void setMaxReconnects(int maxReconnects) {
        this.maxReconnects = maxReconnects;
    }

    public int getInitialTimeout() {
        return initialTimeout;
    }

    public void setInitialTimeout(int initialTimeout) {
        this.initialTimeout = initialTimeout;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public int getSocketTimeout() {
        return socketTimeout;
    }

    public void setSocketTimeout(int socketTimeout) {
        this.socketTimeout = socketTimeout;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public int getInitialConnectionSize() {
        return initialConnectionSize;
    }

    public void setInitialConnectionSize(int initialConnectionSize) {
        this.initialConnectionSize = initialConnectionSize;
    }

    @Override
    public String toString() {
        SerializeWriter writer = new SerializeWriter();
        JSONSerializer serializer = new JSONSerializer(writer);
        serializer.write(this);
        return writer.toString();
    }
}
