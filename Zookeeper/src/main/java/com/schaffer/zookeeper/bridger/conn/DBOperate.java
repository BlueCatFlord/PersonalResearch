package com.schaffer.zookeeper.bridger.conn;


import com.schaffer.zookeeper.bridger.except.DBConnException;

/**
 * Created by schaffer on 2016/8/18.
 */
public interface DBOperate<T> {

    boolean insert(T data) throws DBConnException;

    boolean find(T query) throws DBConnException;

    boolean delete(T query);

    boolean update(T query, T data);

}
