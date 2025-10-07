package org.example;

public class DecrementThread extends Thread {
    private final Counter counter;
    private final double i_max;
    public DecrementThread(Counter counter, double i_max) {
        this.counter = counter;
        this.i_max = i_max;
    }
    public void run() {
        for(int i=0; i<i_max; i++) counter.decrement();
    }
}
