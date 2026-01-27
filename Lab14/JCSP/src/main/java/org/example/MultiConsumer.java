package org.example;

import org.jcsp.lang.*;

public class MultiConsumer implements CSProcess {
    private final Guard[] channels;
    private final One2OneChannelInt outChannel;
    public MultiConsumer(Guard[] channels, One2OneChannelInt outChannel) {
        this.channels = channels;
        this.outChannel = outChannel;
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
            num = alt.priSelect();
            if (num < guards.length-1) {
                buf = ((One2OneChannelInt) (guards[num])).in().read();
                outChannel.out().write(buf);
            }
        }
    }
}
