package org.example;

import org.jcsp.lang.*;

import java.util.concurrent.CopyOnWriteArrayList;

public class Main {
    static void main() {
        int N = 10;

        int p_i=0;
        CSProcess[] processes = new CSProcess[N + 4];

        final One2OneChannelInt ch1 = Channel.one2oneInt();
        Producer producer = new Producer(ch1);
        final Guard[] guards = new Guard[N];
        for(int i = 0; i<N; i++) {
            guards[i] = (Guard)(Channel.one2oneInt());
        }
        MultiProducer mprod = new MultiProducer(guards, ch1);
        final Guard[] guards_out = new Guard[N];
        for(int i = 0; i<N; i++) {
            guards_out[i] = (Guard)(Channel.one2oneInt());
        }
        for (int i = 0; i<N; i++) {
            Buffor buf = new Buffor((One2OneChannelInt) guards[i], (One2OneChannelInt) guards_out[i]);
            processes[i] = buf;
        }
        final One2OneChannelInt ch2 = Channel.one2oneInt();
        MultiConsumer mcp = new MultiConsumer(guards_out, ch2);
        Consumer[] consumers = new Consumer[ch2];

        Parallel par =  new Parallel(processes);
        par.run();
    }
}
