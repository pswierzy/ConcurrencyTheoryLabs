package org.tw;

import java.util.Optional;
import java.util.concurrent.locks.ReentrantLock;

public class LockedList {

    private static class Node {
        private Object value;
        private Optional<Node> next = Optional.empty();
        private final ReentrantLock lock = new ReentrantLock();

        Node(Object value) {
            if (value == null)
                throw new IllegalArgumentException("Value cannot be null!");
            this.value = value;
        }

        void lock() { lock.lock(); }
        void unlock() { lock.unlock(); }
    }

    private final Node head = new Node("");  // dummy head

    public LockedList() {}

    private Pair find(Object value) {
        Node prev = head;
        prev.lock();

        Optional<Node> nextOpt = prev.next;

        while (nextOpt.isPresent()) {
            Node curr = nextOpt.get();
            curr.lock();

            if (curr.value.equals(value)) {
                return new Pair(prev, curr);
            }

            prev.unlock();
            prev = curr;
            nextOpt = curr.next;
        }

        prev.unlock();
        return new Pair(null, null);
    }

    void add(Object value) {
        Node newNode = new Node(value);

        Node prev = head;
        prev.lock();

        Optional<Node> nextOpt = prev.next;

        while (nextOpt.isPresent()) {
            Node curr = nextOpt.get();
            curr.lock();
            prev.unlock();
            prev = curr;
            nextOpt = curr.next;
        }

        prev.next = Optional.of(newNode);
        prev.unlock();
    }

    void remove(Object value) {
        Pair p = find(value);
        if (p.prev == null) return;

        Node prev = p.prev;
        Node curr = p.curr;

        prev.next = curr.next;

        curr.unlock();
        prev.unlock();
    }

    boolean contains(Object value) {
        Pair p = find(value);
        boolean found = p.curr != null;
        if (p.curr != null) p.curr.unlock();
        if (p.prev != null) p.prev.unlock();
        return found;
    }

    private static class Pair {
        final Node prev, curr;
        Pair(Node prev, Node curr) { this.prev = prev; this.curr = curr; }
    }
}
