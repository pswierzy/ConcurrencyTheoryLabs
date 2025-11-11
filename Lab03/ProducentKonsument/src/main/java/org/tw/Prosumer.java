package org.tw;

public class Prosumer extends Thread {
    private int id;
    private final Buffer in;
    private final Buffer out;

    public Prosumer(int id, Buffer in, Buffer out) {
        this.id = id;
        this.in = in;
        this.out = out;
    }

    private int process(int x) {
        return x + 1000;
    }



    @Override
    public void run() {
        try {
            while (true) {
                int x = in.get();
                System.out.println("Prosumer " + id + " dostal: " + x);

                int y = process(x);
                System.out.println("Prosumer " + id + " zmienil na: " + y);

                Thread.sleep((long) (Math.random() * 300));

                out.put(y);
            }
        } catch (InterruptedException e) {}
    }
}
