package org.tw;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantLock;

public class LockedList {
    private static class Node {
        private Object value;
        private Optional<Node> next = Optional.empty();
        final ReentrantLock lock = new ReentrantLock();

        public Node(Object value) {
            if (value == null) throw new IllegalArgumentException("Wartość węzła nie może być null!");
            this.value = value;
        }

        void lock() {
            lock.lock();
        }
        void unlock() {
            lock.unlock();
        }

        public Object getValue() {
            return value;
        }

        public Optional<Node> getNext() {
            return next;
        }

        public void setNext(Optional<Node> next) {
            this.next = next;
        }

        public Optional<Node> getNextWithLock() {
            this.next.ifPresent(Node::lock);
            return this.next;
        }
    }

    private Node head = new Node("");

    public Node getLockedTail() {
        head.lock();
        Optional<Node> ptr = Optional.ofNullable(head);
        Optional<Node> prev = ptr;
        while(ptr.get().getNextWithLock().isPresent()) {
            prev = ptr;
            ptr = ptr.get().getNextWithLock();
            prev.get().unlock();
        }
        return ptr.get();
    }

    public List<Optional<Node>> findLocked(Object value) {
        head.lock();
        Optional<Node> ptr = Optional.ofNullable(head);
        Optional<Node> prev = Optional.empty();
        while(ptr.get().getNextWithLock().isPresent()) {
            if (ptr.get().getValue().equals(value)) {
                return List.of(prev, ptr);
            }
            prev.ifPresent(Node::unlock);
            prev = ptr;
            ptr = ptr.get().getNextWithLock();
        }
        if (!ptr.get().getValue().equals(value)) {
            return List.of(Optional.empty(), Optional.empty());
        }
        return List.of(prev, ptr);
    }

    void add (Object value) {
        Node node = getLockedTail();
        node.next = Optional.of(new Node(value));
        node.unlock();
    }

    void remove (Object value) {
        List<Optional<Node>> ptrs = findLocked(value);
        Optional<Node> prev = ptrs.get(0);
        Optional<Node> ptr = ptrs.get(1);
        if (prev.isPresent()) {
            prev.get().setNext(ptr.get().getNext());
            ptr.get().unlock();
            prev.get().unlock();
        } else if (ptr.isPresent()) {
            head = ptr.get().getNext().get();
            ptr.get().unlock();
        }
    }

    boolean contains(Object value) {
        List<Optional<Node>> ptrs = findLocked(value);
        Optional<Node> ptr = ptrs.get(1);
        return ptr.isPresent();
    }
}
