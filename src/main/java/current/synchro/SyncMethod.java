package current.synchro;

// 测试方法同步时锁的对象。 一个线程拿到锁之后另外一个线程不执行，说明线程占用锁，锁的对象是类
// 如果不加synchronized, 或者只添加一个方法 , 达不到同步的目的
public class SyncMethod {

    private int a = 0;
    private int b = 0;
    private synchronized void inc() throws InterruptedException {
        a++;
        b++;
        System.out.println("inc a = " + a + "; b = " + b);
        Thread.sleep(1000);
    }

    private synchronized void compare() {
        if (a != b) {
            System.out.println("a = " + a + "; b = " + b);
        }
        // 一旦拿到锁就不放
        while ( true ) {
            System.out.println("wite....");
        }
    }
    public static void main(String[] args) {
        SyncMethod syncMethod = new SyncMethod();
        new Thread(() -> {
            for (int i = 0; i < 10000; i++) {
                try {
                    syncMethod.inc();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
        new Thread(() -> {
            for (int i = 0; i < 10000; i++) {
                syncMethod.compare();
            }
        }).start();
    }
}
