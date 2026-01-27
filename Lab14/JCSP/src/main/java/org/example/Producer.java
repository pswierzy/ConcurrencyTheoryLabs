package org.example;

import org.jcsp.lang.CSProcess;
import org.jcsp.lang.One2OneChannelInt;

import static java.lang.Thread.sleep;

public class Producer implements CSProcess {
    private One2OneChannelInt channel;

    public Producer(One2OneChannelInt channel) {
        this.channel = channel;
    }

    public void run() {
        while(true){
            int item = (int)(Math.random() * 100) + 1;
            channel.out().write(item);
            try {
                sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
