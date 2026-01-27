package org.example;

import org.jcsp.lang.CSProcess;
import org.jcsp.lang.One2OneChannelInt;

public class Buffor implements CSProcess {
    private final One2OneChannelInt in;
    private final One2OneChannelInt out;

    private int buff;

    public Buffor(One2OneChannelInt in, One2OneChannelInt out) {
        this.in = in;
        this.out = out;
    }
    public void run() {
        while (true) {
            buff = in.in().read();
            out.out().write(buff);
        }
    }
}
