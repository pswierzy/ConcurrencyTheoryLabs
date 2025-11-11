package org.tw;

public class Producer extends Thread {
    int id;
    private Buffer _buf;

    public Producer(Buffer buf,  int id) {
        _buf = buf;
        this.id = id;
    }

    public void run() {

        long start = System.nanoTime();

        for (int i = 0; i < 100; ++i) {
            _buf.put(i);
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {}
        }

        long end = System.nanoTime();
        System.out.println("Czas producenta "+ id +": " + (end - start) + " ns");

    }
}
