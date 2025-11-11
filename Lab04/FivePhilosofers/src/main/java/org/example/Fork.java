package org.example;

public class Fork {
    private boolean isFree = true;

    public synchronized boolean tryAcquire(Philosopher p) {
        if (isFree) {
            isFree = false;
            System.out.println("Filozof " + p.getId() + " podniosl widelec.");
            return true;
        }
        return false;
    }

    public synchronized void release(Philosopher p) {
        isFree = true;
        System.out.println("Filozof " + p.getId() + " odlozyl widelec.");
        notifyAll();
    }
}
