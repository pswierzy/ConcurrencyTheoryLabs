package org.tw;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

class LockedListTest {

    private LockedList list;

    @BeforeEach
    void setUp() {
        list = new LockedList();
    }

    @Test
    void testAddAndContains() {
        list.add("A");
        list.add("B");
        list.add("C");

        assertTrue(list.contains("A"));
        assertTrue(list.contains("B"));
        assertTrue(list.contains("C"));
        assertFalse(list.contains("D"));
    }

    @Test
    void testRemove() {
        list.add("A");
        list.add("B");
        list.add("C");

        list.remove("B");
        assertTrue(list.contains("A"));
        assertFalse(list.contains("B"));
        assertTrue(list.contains("C"));

        list.remove("A");
        assertFalse(list.contains("A"));
        assertTrue(list.contains("C"));

        list.remove("C");
        assertFalse(list.contains("C"));
    }

    @Test
    void testRemoveNonExistent() {
        list.add("X");
        list.remove("Y"); // Usuwanie nieistniejącego elementu
        assertTrue(list.contains("X"));
    }

    @Test
    void testAddAndRemoveConcurrently() throws InterruptedException {
        int threads = 10;
        int operationsPerThread = 100;
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        CountDownLatch latch = new CountDownLatch(threads);

        for (int i = 0; i < threads; i++) {
            final int threadNum = i;
            executor.submit(() -> {
                for (int j = 0; j < operationsPerThread; j++) {
                    String value = "Item-" + threadNum + "-" + j;
                    list.add(value);
                    if (j % 2 == 0) {
                        list.remove(value);
                    }
                }
                latch.countDown();
            });
        }

        latch.await();
        executor.shutdown();

        // Sprawdzenie spójności listy - wszystkie elementy o nieparzystych j powinny istnieć
        for (int i = 0; i < threads; i++) {
            for (int j = 0; j < operationsPerThread; j++) {
                String value = "Item-" + i + "-" + j;
                if (j % 2 == 0) {
                    assertFalse(list.contains(value));
                } else {
                    assertTrue(list.contains(value));
                }
            }
        }
    }
}
