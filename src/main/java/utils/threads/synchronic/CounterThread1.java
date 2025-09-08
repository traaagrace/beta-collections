package utils.threads.synchronic;

public class CounterThread1 implements Runnable{
    private Counter counter ;
    CounterThread1(Counter  counter) {
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
            counter.increment();
            System.out.println("in"+counter.value());
        }
    }
}

