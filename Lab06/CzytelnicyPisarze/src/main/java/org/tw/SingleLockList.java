package org.tw;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class SingleLockList {
    private final ReentrantLock lock = new ReentrantLock();
    private final List<Object> list = new LinkedList<>();

    void add(Object value) {
        lock.lock();
        try {
            list.add(value);
        } finally {
            lock.unlock();
        }
    }

    void remove(Object value) {
        lock.lock();
        try {
            list.remove(value);
        } finally {
            lock.unlock();
        }
    }

    boolean contains(Object value) {
        lock.lock();
        try {
            return list.contains(value);
        } finally {
            lock.unlock();
        }
    }
}
