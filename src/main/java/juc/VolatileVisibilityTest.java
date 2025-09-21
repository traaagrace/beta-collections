package juc;

/**
 ✅ 如果 flag 没有加 volatile，有可能 reader 线程永远看不到 true（死循环）。
 ✅ 加了 volatile，reader 就一定能看到 flag=true，这就是 happen-before。
* */
public class VolatileVisibilityTest {
    // 不加，卡死了
    private static String flag = "false";

    public static void main(String[] args) throws InterruptedException {
        Thread reader = new Thread(() -> {
            System.out.println("Reader thread started...");
            while (flag.equals(false)) {

            }
            System.out.println("Reader thread detected flag = true");
        });

        reader.start();

        Thread.sleep(1000);
        System.out.println("Main thread is setting flag = true");
        flag = "true";
    }
}