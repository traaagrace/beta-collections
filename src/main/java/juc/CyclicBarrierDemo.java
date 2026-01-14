package juc;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * CyclicBarrierDemo
 * 线程会阻塞，直到所有线程都到达，然后一起继续执行
 * 线程会阻塞，直到所有线程都到达，然后一起继续执行
 * 增加超时机制，超时后抛出异常，可以对异常处理
 * */
public class CyclicBarrierDemo {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("开始");
        CyclicBarrier cyclicBarrier = new CyclicBarrier(3, () -> {
            System.out.println(Thread.currentThread().getName() + "召唤神龙");
        });

        for (int i = 1; i <= 7; i++) {
            int finalI = i;
            Thread.sleep(1000);
            new Thread(() -> {
                System.out.println(Thread.currentThread().getName() + "收集到第" + finalI + "龙珠");
                try {
                    cyclicBarrier.await(10000, TimeUnit.MILLISECONDS);
                    System.out.println(Thread.currentThread().getName() + "已获得锁");
                } catch (TimeoutException e) {
                    e.printStackTrace();
                } catch (BrokenBarrierException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }).start();
        }
    }
}
