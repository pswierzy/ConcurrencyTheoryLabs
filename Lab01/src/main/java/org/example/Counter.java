package org.example;

public class Counter {
    private int value = 0;
    public Counter() {}

    public int getValue() {
        return value;
    }
    public void increment() {
        value++;
    }
    public void decrement() {
        value--;
    }
}
