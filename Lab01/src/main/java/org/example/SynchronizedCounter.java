package org.example;

public class SynchronizedCounter extends Counter {
    private int value = 0;
    public SynchronizedCounter() {}

    public int getValue() {
        return value;
    }
    public synchronized void increment() {
        value++;
    }
    public synchronized void decrement() {
        value--;
    }
}
