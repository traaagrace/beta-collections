package utils.threads.synchronic;

import java.util.concurrent.ExecutorService;

public class CounterThread2 implements Runnable{
    private Counter counter;
    CounterThread2(Counter  counter) {
        this.counter = counter;
    }
    @Override
    public void run() {
        for (int i = 0; i < 100; i++) {
            try {
                Thread.sleep(2);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            counter.decrement();
            System.out.println("de"+counter.value());
        }
    }
}