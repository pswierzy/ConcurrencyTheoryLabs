# Ćwiczenie 3 - Teoria Współbieżności
Piotr Świerzy

## Implementacja klas

```java
public class Producer extends Thread {
    private Buffer _buf;

    public Producer(Buffer buf) {
        _buf = buf;
    }

    public void run() {

        long start = System.nanoTime();

        for (int i = 0; i < 100; ++i) {
            _buf.put(i);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {}
        }

        long end = System.nanoTime();
        System.out.println("Czas producenta: " + (end - start) + " ns");

    }
}
```

```java
public class Consumer extends Thread {
    private Buffer _buf;

    public Consumer(Buffer buf) {
        _buf = buf;
    }

    public void run() {

        long start = System.nanoTime();

        for (int i = 0; i < 100; ++i) {
            System.out.println(_buf.get());

            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {}
        }

        long end = System.nanoTime();
        System.out.println("Czas konsumenta: " + (end - start) + " ns");

    }
}
```

```java
public class Buffer {
    private final int[] data;
    private int size = 0;
    private int head = 0;
    private int tail = 0;

    public Buffer(int size) {
        this.data = new int[size];
    }

    public synchronized void put(int i) {
        while (size == data.length) {
            try {
                wait();
            } catch (InterruptedException e) {}
        }

        try {Thread.sleep(20);} catch (InterruptedException e) {}

        data[tail] = i;
        tail = (tail + 1) % data.length;
        size++;

        notifyAll();
    }

    public synchronized int get() {
        while (size == 0) {
            try {
                wait();
            } catch (InterruptedException e) {}
        }

        try {Thread.sleep(20);} catch (InterruptedException e) {}

        int ret = data[head];
        head = (head + 1) % data.length;
        size--;

        notifyAll();
        return ret;
    }
}
```

## Testy

### 1 producent i 1 konsument

```java
public class PKmon {
    public static void main(String[] args) throws InterruptedException {

        long start = System.nanoTime();

        Buffer buf = new Buffer(10);

        Producer p = new Producer(buf);
        Consumer c = new Consumer(buf);

        p.start();
        c.start();

        p.join();
        c.join();

        long end = System.nanoTime();
        System.out.println("Total time: " + (end - start) + " ns");
    }
}
```

Wynik: Pokolei wypisane liczby od 0 do 99, a na koniec wynik odliczania czasu:
```
Czas konsumenta: 12088122200 ns
Czas producenta: 12116857900 ns
Total time: 12119370800 ns
```
Ze względu na to, że "produkowanie" trwa dłużej niż "konsumpcja" to widzimy, że różnica jest nie wielka

---

Po zamianie czasu `sleep` tak, że dłużej trwa "konsumpcja" widać różnice.

Najpierw wypisywane są po kolei liczby, a na koniec:
```
(...)
Czas producenta: 11025040800 ns
90
91
92
93
94
95
96
97
98
99
Czas konsumenta: 12267284400 ns
Total time: 12269272900 ns
```
Czyli poprawnie widzimy, że producent skończył pracę chwilę wcześniej niż konsument.

Resztę testów przeprowadzę z równymi `sleep` i z dodanym `id`, żeby rozróżnić instancje klasy od siebie.

---

### Więcej producentów

Aby wszystkie produkty zostały skonsumowane w pętli w Consumer, zmieniamy warunek na:
```java
for (int i = 0; i < total / consumerNumber + ((total%consumerNumber) > id ? 1 : 0); ++i) {}
```
Dzięki temu nie dojdzie do zablokowania programu przez zbyt małą ilość konsumentów.

```java
public class PKmon {
    public static void main(String[] args) throws InterruptedException {

        long start = System.nanoTime();

        Buffer buf = new Buffer(10);
        ArrayList<Producer> producers = new ArrayList<>();
        ArrayList<Consumer> consumers = new ArrayList<>();

        int producerNumber = 5;
        int consumerNumber = 3;

        for (int i = 0; i < producerNumber; i++) {
            producers.add(new Producer(buf, i));
        }
        for (int i = 0; i < consumerNumber; i++) {
            consumers.add(new Consumer(buf, i, consumerNumber, producerNumber));
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
```

Pomijając (już nie po kolei) wypisane liczby, wynik wygląda następująco:
```
Czas producenta 4: 12895115200 ns
Czas producenta 2: 19002149500 ns
Czas producenta 0: 20656436200 ns
Czas producenta 1: 20676679900 ns
Czas konsumenta 2: 20861855700 ns
Czas producenta 3: 20923100600 ns
Czas konsumenta 0: 21026158500 ns
Czas konsumenta 1: 21046713400 ns
Total time: 21048941500 ns
```

Wszystkie wątki się skończyły, więc wszystko działa dobrze.

---

### Więcej konsumentów

```java
int producerNumber = 3;
int consumerNumber = 5;
```

Wyniki są następujące:
```
Czas konsumenta 4: 7533517500 ns
Czas konsumenta 2: 11342002800 ns
Czas producenta 0: 12414656200 ns
Czas producenta 1: 12436132800 ns
Czas konsumenta 1: 12476530100 ns
Czas konsumenta 0: 12496853300 ns
Czas producenta 2: 12517730600 ns
Czas konsumenta 3: 12537787900 ns
Total time: 12540136000 ns
```

## Przetwarzanie potokowe

Dodana klasa `Prosumer`:

```java
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
```

A main wygląda tak:
```java
public class PKmon {
    public static void main(String[] args) throws InterruptedException {

        long start = System.nanoTime();

        Buffer firstBuf = new Buffer(10);
        ArrayList<Producer> producers = new ArrayList<>();
        ArrayList<Consumer> consumers = new ArrayList<>();

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
                new Prosumer(i*10+j, lastBuf, nextBuf).start();
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
```
Prosument xy nalezy do x-tej warstwy i jest y-tym prosumentem w tej warstwie.

Wyniki to liczby od 5000 do 5099, bo każda warstwa prosumencka dodaje 1000 do finalnej liczby.

Wynik:
```
Czas producenta 0: 20720325700 ns
Czas producenta 1: 20925793300 ns
Czas producenta 2: 20968314000 ns
Czas konsumenta 2: 22680980300 ns
Czas konsumenta 1: 23170135900 ns
Czas konsumenta 0: 23355340800 ns
Total time: 23358229000 ns
```

W porównaniu od wcześniejszych testów nie mamy teraz pewności, że produkty wyprodukowane przez jednego producenta będą skonsumowane po kolei.