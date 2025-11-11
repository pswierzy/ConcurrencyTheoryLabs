package org.example;

public class Main {
    static void main() {

        int philosopherCount = 5;
        int eatCount = 1000;

        Fork[] forks = new Fork[philosopherCount];
        Philosopher[] philosophers = new Philosopher[philosopherCount];
        Thread[] threads = new Thread[philosopherCount];

        for (int i = 0; i < philosopherCount; i++) {
            forks[i] = new Fork();
        }

        for (int i = 0; i < philosopherCount; i++) {
            Fork left = forks[i];
            Fork right = forks[(i + 1) % philosopherCount];
            philosophers[i] = new Philosopher(i, left, right, eatCount);
            threads[i] = new Thread(philosophers[i]);
        }

        for (Thread t : threads) {
            t.start();
        }

        for (Thread t : threads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        System.out.println("Uczta zakonczona!");

        for (Philosopher p : philosophers) {
            System.out.println("Sredni czas czekania filozofa " + p.getId() + " to " + p.getTimeWaiting()/eatCount);
            //System.out.println("Najdłuższy czas czekania filozofa to " + p.getLongestTimeWaiting());
        }
    }
}
