package juc;

/**
 * increase锁住了应用的对象
 * 并没有锁住对象对应的变量，
 * 所以可以对变量操作
 * 安全的方式是对OnlyVolitaleNotAtomic类对象加锁
* */
public class OnlyVolitaleNotAtomic {
    private static OnlyVolitaleNotAtomic onlyVolitaleNotAtomic = new OnlyVolitaleNotAtomic();

    public static void increase() throws InterruptedException {
        synchronized (onlyVolitaleNotAtomic) {
            System.out.println("Counter: " + onlyVolitaleNotAtomic);
            Thread.sleep(2000);
            System.out.println("Counter: " + onlyVolitaleNotAtomic);
        }
    }

    public static void decrease() {
        onlyVolitaleNotAtomic =  null;
    }

    public static void main(String[] args) throws InterruptedException {
        new Thread(() -> {
            try {
                increase();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();
        Thread.sleep(1000);
        decrease();
    }
}
