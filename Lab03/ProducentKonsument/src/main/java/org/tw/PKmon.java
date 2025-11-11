package org.tw;

import java.util.ArrayList;

public class PKmon {
    public static void main(String[] args) throws InterruptedException {

        long start = System.nanoTime();

        Buffer firstBuf = new Buffer(10);
        ArrayList<Producer> producers = new ArrayList<>();
        ArrayList<Consumer> consumers = new ArrayList<>();
        ArrayList<Prosumer>  prosumers = new ArrayList<>();

        int producerNumber = 3;
        int consumerNumber = 3;
        int prosumerChainLen = 5;
        int prosumersPerChain = 3;

        for (int i = 0; i < producerNumber; i++) {
            producers.add(new Producer(firstBuf, i));
        }

        Buffer lastBuf = firstBuf;

        for (int i = 0; i < prosumerChainLen; i++) {
            Buffer nextBuf = new Buffer(5);

            for (int j = 0; j < prosumersPerChain; j++) {
                Prosumer p = new Prosumer(i*10+j, lastBuf, nextBuf);
                p.start();
                prosumers.add(p);
            }

            lastBuf = nextBuf;
        }

        for (int i = 0; i < consumerNumber; i++) {
            consumers.add(new Consumer(lastBuf, i, consumerNumber, producerNumber));
        }


        for(Producer p : producers) {
            p.start();
        }
        for(Consumer c : consumers) {
            c.start();
        }

        for(Producer p : producers) {
            p.join();
        }
        for(Consumer c : consumers) {
            c.join();
        }

        long end = System.nanoTime();
        System.out.println("Total time: " + (end - start) + " ns");
    }
}
