package org.example;

import static java.lang.Math.max;

public class Philosopher implements Runnable {
    private int id;
    private Fork left;
    private Fork right;
    private int eatCount;
    private long timeWaiting = 0;
    private long longestTimeWaiting = 0;

    public Philosopher(int id, Fork left, Fork right, int eatCount) {
        this.id = id;
        this.left = left;
        this.right = right;
        this.eatCount = eatCount;
    }

    public int getId() {
        return id;
    }

    public long getTimeWaiting() {
        return timeWaiting;
    }

    public long getLongestTimeWaiting() {
        return longestTimeWaiting;
    }

    @Override
    public void run() {
        try {
            for (int i = 0; i < eatCount; i++) {
                think();
                eat();
            }
            System.out.println("Filozof " + id + " skonczyl uczte.");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void think() throws InterruptedException {
        System.out.println("Filozof " + id + " mysli...");
    }

    private void eat() throws InterruptedException {
//        rozwiązanie bez rozwiązania problemu
//        left.acquire(this);
//        right.acquire(this);

        long start = System.nanoTime();
        while (true) {
            left.tryAcquire(this);
            if (right.tryAcquire(this)) {
                System.out.println("Filozof " + id + " je...");
                right.release(this);
                left.release(this);

                long time = System.nanoTime() - start;
                timeWaiting += time;
                longestTimeWaiting = max(longestTimeWaiting, time);

                break;
            }
            left.release(this);
        }
    }
}
