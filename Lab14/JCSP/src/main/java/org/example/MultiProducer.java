package org.example;

import org.jcsp.lang.*;

public class MultiProducer implements CSProcess {
    private final Guard[] channels;
    private final One2OneChannelInt inChannel;
    public MultiProducer(Guard[] channels, One2OneChannelInt inChannel) {
        this.channels = channels;
        this.inChannel = inChannel;
    }

    public void run() {
        final Skip skip = new Skip();
        final Guard[] guards = new Guard[channels.length + 1];
        for (int i = 0; i < guards.length; i++) {
            guards[i] = channels[i];
        }
        guards[guards.length - 1] = skip;

        final Alternative alt = new Alternative(guards);

        int num;
        int buf;
        while (true) {
            buf = inChannel.in().read();

            num = alt.priSelect();
            while (true){
                if (num < guards.length-1) {
                    ((One2OneChannelInt) (guards[num])).out().write(buf);
                } else {
                    break;
                }
            }
        }
    }
}
