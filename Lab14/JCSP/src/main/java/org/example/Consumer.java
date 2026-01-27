package org.example;

import org.jcsp.lang.CSProcess;
import org.jcsp.lang.One2OneChannelInt;

import static java.lang.Thread.sleep;

public class Consumer implements CSProcess {
    private One2OneChannelInt channel;

    public Consumer(One2OneChannelInt channel) {
        this.channel = channel;
    }

    public void run() {
        while(true) {
            int item = channel.in().read();
            System.out.println(item);
            try {
                sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
