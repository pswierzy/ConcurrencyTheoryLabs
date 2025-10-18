package org.semaphore;

public class Wyscig {
    private static int licznik = 0;
    private static final int ITERACJE = 100000;
    private static SemaforBinarny semaforBinarny = new SemaforBinarny();
    private static SemaforIF semaforIF = new SemaforIF();

    static class InkrementujacyWatek extends Thread {
        @Override
        public void run() {
            for(int i = 0; i < ITERACJE; i++) {
                semaforBinarny.P();
                licznik++;
                semaforBinarny.V();
            }
        }
    }
    static class InkrementujacyWatekIF extends Thread {
        @Override
        public void run() {
            for(int i = 0; i < ITERACJE; i++) {
                semaforIF.P();
                licznik++;
                semaforIF.V();
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        // Test 1: Bez semafora
        System.out.println("Test 1: BEZ synchronizacji");
        licznik = 0;
        Thread[] t = new Thread[5];
        for(int i = 0; i < 5; i++) {
            t[i] = new Thread(() -> {
                for (int j = 0; j < ITERACJE; j++) {
                    licznik++;
                }
            });
            t[i].start();
        }
        for (Thread thread : t) {
            thread.join();
        }
        System.out.println("Oczekiwana wartosc: " + (5 * ITERACJE));
        System.out.println("Faktyczna wartosc:  " + licznik);
        System.out.println("Blad wyscigu: " + ((5 * ITERACJE) - licznik) + "\n");

        // Test 2: Z semaforem
        System.out.println("Test 2: Z semaforem binarnym");
        licznik = 0;
        Thread[] t2 = new Thread[5];
        for (int i = 0; i < 5; i++) {
            t2[i] = new InkrementujacyWatek();
            t2[i].start();
        }
        for (Thread thread : t2) {
            thread.join();
        }
        System.out.println("Oczekiwana wartosc: " + (5 * ITERACJE));
        System.out.println("Faktyczna wartosc:  " + licznik);
        System.out.println("Blad: " + ((5 * ITERACJE) - licznik));
        System.out.println("Synchronizacja dziala poprawnie!");

        // Test 3: Semafor z if zamiast while
        System.out.println("Test 3: Z semaforem z if");
        licznik = 0;
        Thread[] t3 = new Thread[5];
        for (int i = 0; i < 5; i++) {
            t3[i] = new InkrementujacyWatekIF();
            t3[i].start();
        }
        for (Thread thread : t3) {
            thread.join();
        }
        System.out.println("Oczekiwana wartosc: " + (5 * ITERACJE));
        System.out.println("Faktyczna wartosc:  " + licznik);
        System.out.println("Blad: " + ((5 * ITERACJE) - licznik));
/**
 * Gdy uzywamy IF zamiast WHILE:
 *   Wątek A wywołuje wait() i zwalnia monitor
 *   Wątek B wywołuje notify() -> budzi wątek (np. A)
 *   Wątek A przechodzi do wejścia
 *   ALE w tym czasie chodzi wątek C i zmienia warunek
 *   Wątek A wchodzi, ale ze względu na ifa nie sprawdza warunku jeszcze raz
 *   Wątek A kontynuuje mimo że warunek nie jest spełniony
*/

    }
}
