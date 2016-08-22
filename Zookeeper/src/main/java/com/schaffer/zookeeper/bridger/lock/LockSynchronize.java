package com.schaffer.zookeeper.bridger.lock;

import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by schaffer on 2016/8/22.
 */
public class LockSynchronize {

    private static volatile ReentrantReadWriteLock lock = new ReentrantReadWriteLock(false);

    public static ReentrantReadWriteLock getLock() {
        return lock;
    }
}
