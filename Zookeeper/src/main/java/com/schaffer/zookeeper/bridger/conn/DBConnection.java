package com.schaffer.zookeeper.bridger.conn;


import com.schaffer.zookeeper.bridger.config.Configuration;
import com.schaffer.zookeeper.bridger.except.DBConnException;

/**
 * Created by schaffer on 2016/8/17.
 */
public interface DBConnection {

    /**
     * connect the servers
     *
     * @param configuration
     * @return
     */
    boolean connect(Configuration configuration) throws DBConnException;

    /**
     * disconnect the servers
     *
     * @return
     */
    void close() throws DBConnException;

    boolean isOpen() throws DBConnException;
}
