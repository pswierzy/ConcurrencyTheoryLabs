package org.semaphore;

public class SemaforIF {
    private boolean stan = true;
    private int czeka = 0;
    public SemaforIF() {
    }
    public synchronized void P() {
        czeka++;
        if(!stan) {
            try{
                wait();
            } catch(InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        czeka--;
        stan = false;
    }
    public synchronized void V() {
        stan = true;
        notify();
    }
}
