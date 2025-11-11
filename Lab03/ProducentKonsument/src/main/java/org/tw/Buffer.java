package org.tw;

public class Buffer {
    private final int[] data;
    private int size = 0;
    private int head = 0;
    private int tail = 0;

    public Buffer(int size) {
        this.data = new int[size];
    }

    public synchronized void put(int i) {
        while (size == data.length) {
            try {
                wait();
            } catch (InterruptedException e) {}
        }

        try {Thread.sleep(20);} catch (InterruptedException e) {}

        data[tail] = i;
        tail = (tail + 1) % data.length;
        size++;

        notifyAll();
    }

    public synchronized int get() {
        while (size == 0) {
            try {
                wait();
            } catch (InterruptedException e) {}
        }

        try {Thread.sleep(20);} catch (InterruptedException e) {}

        int ret = data[head];
        head = (head + 1) % data.length;
        size--;

        notifyAll();
        return ret;
    }
}
