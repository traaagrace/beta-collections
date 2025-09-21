package juc;

/*
✅ 期望结果是 2000，但实际经常 <2000。
    说明 volatile 保证了可见性，但 不能保证复合操作的原子性。
* */
public class VolatileAtomicityTest {
    private static volatile int counter = 0;

    public static void main(String[] args) throws InterruptedException {
        Runnable task = () -> {
            for (int i = 0; i < 1000; i++) {
                counter++;  // 不是原子操作
            }
        };

        Thread t1 = new Thread(task);
        Thread t2 = new Thread(task);
        t1.start();
        t2.start();
        t1.join();
        t2.join();

        System.out.println("Final counter = " + counter);
    }
}

