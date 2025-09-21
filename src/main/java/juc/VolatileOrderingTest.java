package juc;

/*
*    因为 volatile 写 (v=1) happen-before volatile 读 (while(v==0))，
*    所以 reader 一旦读到 v==1，就一定能看到 a=1。
*    但注意 b=2 不一定立刻对 reader 可见（因为它在 volatile 写之后）。
* */
public class VolatileOrderingTest {
    private static String a = "0";
    private static volatile String v = "0";

    public static void main(String[] args) throws InterruptedException {
        Thread writer = new Thread(() -> {
            v = "2";          // volatile 写
            a = "1";          // 普通写
        });

        Thread reader = new Thread(() -> {
            while (v.equals("0")) {
                // 等待
            }
            // 一旦 v == 1，可见 a = 1 对 reader 可见（happen-before）
            System.out.println(a);
        });

        reader.start();
        writer.start();

        writer.join();
        reader.join();
    }
}
