package org.tw;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    static void simulateCost(int cost) {
        // Symulacja kosztownej operacji (np. porównanie, wstawianie)
        for (int i = 0; i < cost; i++) {
            Math.sqrt(i);
        }
    }

    static void benchmarkLockedList(int threads, int opsPerThread, int cost) throws InterruptedException {
        LockedList list = new LockedList();
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        CountDownLatch latch = new CountDownLatch(threads);

        long start = System.currentTimeMillis();

        for (int i = 0; i < threads; i++) {
            final int threadNum = i;
            executor.submit(() -> {
                for (int j = 0; j < opsPerThread; j++) {
                    String value = "Item-" + threadNum + "-" + j;
                    simulateCost(cost);
                    list.add(value);
                    if (j % 2 == 0) {
                        simulateCost(cost);
                        list.remove(value);
                    }
                    simulateCost(cost);
                    list.contains(value);
                }
                latch.countDown();
            });
        }

        latch.await();
        executor.shutdown();
        long end = System.currentTimeMillis();
        System.out.println("LockedList: " + (end - start) + " ms, cost=" + cost);
    }

    static void benchmarkSingleLockList(int threads, int opsPerThread, int cost) throws InterruptedException {
        SingleLockList list = new SingleLockList();
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        CountDownLatch latch = new CountDownLatch(threads);

        long start = System.currentTimeMillis();

        for (int i = 0; i < threads; i++) {
            final int threadNum = i;
            executor.submit(() -> {
                for (int j = 0; j < opsPerThread; j++) {
                    String value = "Item-" + threadNum + "-" + j;
                    simulateCost(cost);
                    list.add(value);
                    if (j % 2 == 0) {
                        simulateCost(cost);
                        list.remove(value);
                    }
                    simulateCost(cost);
                    list.contains(value);
                }
                latch.countDown();
            });
        }

        latch.await();
        executor.shutdown();
        long end = System.currentTimeMillis();
        System.out.println("SingleLockList: " + (end - start) + " ms, cost=" + cost);
    }

    public static void main(String[] args) throws InterruptedException {
        int threads = 30;
        int ops = 1000;

        int[] costs = {0, 10, 100, 1000, 10000};

        for (int cost : costs) {
            benchmarkLockedList(threads, ops, cost);
            benchmarkSingleLockList(threads, ops, cost);
            System.out.println("-----------------------------");
        }
    }
}
