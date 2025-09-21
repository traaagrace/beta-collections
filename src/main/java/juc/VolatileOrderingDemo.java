package juc;

public class VolatileOrderingDemo {

    // 场景 1：普通写在 volatile 之前
    private static String a1 = "0";
    private static String v1 = "0";

    // 场景 2：普通写在 volatile 之后
    private static String a2 = "0";
    private static volatile String v2 = "0";

    public static void main(String[] args) throws InterruptedException {
//        System.out.println("=== Scenario 1: a before volatile ===");
//        scenario1();
//
//        Thread.sleep(1000);

        System.out.println("\n=== Scenario 2: a after volatile ===");
        scenario2();
    }

    // 普通写在 volatile 之前 → guaranteed 可见
    private static void scenario1() throws InterruptedException {
        Thread writer = new Thread(() -> {
            a1 = "1";      // 普通写
            v1 = "2";      // volatile 写
            System.out.println("Writer finished writing a1 and v1");
        });

        Thread reader = new Thread(() -> {
            while (v1.equals("0")) {
                // busy wait
            }
            System.out.println("Reader sees: a1=" + a1 + ", v1=" + v1);
        });

        reader.start();
        writer.start();

        writer.join();
        reader.join();
    }

    // 普通写在 volatile 之后 → 不保证可见
    private static void scenario2() throws InterruptedException {
        Thread writer = new Thread(() -> {
            v2 = "2";      // volatile 写
            a2 = "1";      // 普通写
        });

        Thread reader = new Thread(() -> {
            while (v2.equals("0")) {
                // busy wait
            }
            System.out.println("Reader sees: a2=" + a2 + ", v2=" + v2);
        });

        reader.start();
        writer.start();

        writer.join();
        reader.join();
    }
}
