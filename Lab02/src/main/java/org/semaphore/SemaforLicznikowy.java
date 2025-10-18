package org.semaphore;

public class SemaforLicznikowy {
    private int wartosc;
    private final SemaforBinarny mutex;
    private final SemaforBinarny kolejka;
    private int czekajace = 0;

    public SemaforLicznikowy(int poczatkowaWartosc) {
        this.wartosc = poczatkowaWartosc;
        this.mutex = new SemaforBinarny();
        this.kolejka = new SemaforBinarny();
        this.kolejka.P();
    }


    public void P() {
        mutex.P();

        wartosc--;
        if (wartosc < 0) {
            czekajace++;
            mutex.V();
            kolejka.P();
            czekajace--;
        } else {
            mutex.V();
        }
    }


    public void V() {
        mutex.P();

        wartosc++;
        if (czekajace > 0) {
            kolejka.V();
        }

        mutex.V();
    }

}
