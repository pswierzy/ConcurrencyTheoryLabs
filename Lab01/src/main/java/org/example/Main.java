package org.example;

import java.awt.event.WindowFocusListener;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        Counter counter = new Counter();
        double i_max = 1000000;

        long startTime = System.nanoTime();

        DecrementThread decrementThread = new DecrementThread(counter, i_max);
        IncrementThread incrementThread = new IncrementThread(counter, i_max);
        decrementThread.start();
        incrementThread.start();

        decrementThread.join();
        incrementThread.join();

        long endTime = System.nanoTime();

        System.out.println("Wynik bez synchronizacji: " + counter.getValue());
        System.out.println("Czas: " + ((endTime - startTime) / 1000000.0) + "ms\n");

        // SYNCHRONIZACJA

        counter = new SynchronizedCounter();

        startTime = System.nanoTime();

        decrementThread = new DecrementThread(counter, i_max);
        incrementThread = new IncrementThread(counter, i_max);
        decrementThread.start();
        incrementThread.start();

        decrementThread.join();
        incrementThread.join();

        endTime = System.nanoTime();

        System.out.println("Wynik z synchronizacja: " + counter.getValue());
        System.out.println("Czas: " + ((endTime - startTime) / 1000000.0) + "ms");

        // Ile wątków pójdzie?

        ArrayList<Thread> threadList = new ArrayList<>();
        counter = new Counter();

        for(int i=2; i<1000000; i+=2){
            decrementThread = new DecrementThread(counter, Double.POSITIVE_INFINITY);
            incrementThread = new IncrementThread(counter, Double.POSITIVE_INFINITY);
            decrementThread.start();
            incrementThread.start();

            System.out.println("Odpalono " + i + " wątków!");
        }

    }
}