package org.semaphore;

public class SemaforBinarny {
    private boolean stan = true;
    private int czeka = 0;
    public SemaforBinarny() {
    }
    public synchronized void P() {
        czeka++;
        while(!stan) {
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
