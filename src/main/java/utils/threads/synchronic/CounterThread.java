package utils.threads.synchronic;

import utils.HttpUtil;

import java.util.concurrent.locks.Condition;

public class CounterThread {
    public static void main(String[] args) throws InterruptedException {
/*        Counter counter = new Counter();
        new Thread(new CounterThread1(counter)).start();
        new Thread(new CounterThread2(counter)).start();
        Thread.sleep(1000);
        System.out.println("Counter value: " + counter.value());*/
        HttpUtil httpUtil = new HttpUtil();
        System.out.println(httpUtil.TestA != "p2p");
    }
}
