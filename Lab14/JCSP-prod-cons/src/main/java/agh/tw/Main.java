package agh.tw;

import org.jcsp.lang.Channel;

public class Main {
    static void main() {
        final One2OneChannelInt channel = Channel.one2oneInt();
    }
}
