package org.tw;

public class Consumer extends Thread {
    int id;
    private Buffer _buf;
    int consumerNumber;
    int producerNumber;

    public Consumer(Buffer buf,  int id, int consumerNumber, int producerNumber) {
        _buf = buf;
        this.id = id;
        this.consumerNumber = consumerNumber;
        this.producerNumber = producerNumber;
    }

    public void run() {

        long start = System.nanoTime();
        int total = producerNumber*100;

        for (int i = 0; i < total / consumerNumber + ((total%consumerNumber) > id ? 1 : 0); ++i) {
            System.out.println(_buf.get());

            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {}
        }

        long end = System.nanoTime();
        System.out.println("Czas konsumenta " + id + ": " + (end - start) + " ns");

    }
}
