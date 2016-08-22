package com.schaffer.zookeeper.bridger.except;

/**
 * Created by schaffer on 2016/8/17.
 */
public class DBConnException extends Exception {

    public DBConnException(String message) {
        super(message);
    }

    public DBConnException(Throwable e) {
        super(e);
    }
}
