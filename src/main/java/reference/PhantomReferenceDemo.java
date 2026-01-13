package reference;

import java.lang.ref.PhantomReference;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;

/**
 * 幻象引用完整示例
 * - 1、幻象引用，持有对象的应用，需要配合应用队列使用
 * - 2、作用是当对象回收时释放资源。
 * - 3、注意：无法通过引用获取对象，只能通过引用队列获取对象
 * - 4、将cleaner线程设置为守护线程，jvm不会因为守护线程而阻塞停止
 * - 5、如果使用remove方法，队列里没有对象，会阻塞线程
 * - 6、如果使用poll方法，队列里没有对象，不会阻塞线程
 * - 7、只有对象回收时才会将幻想引用放到引用queue中
 */
public class PhantomReferenceDemo {

    /**
     * 模拟一个持有“昂贵资源”的对象
     */
    static class Resource {
        private final String name;

        Resource(String name) {
            this.name = name;
            System.out.println("Resource created: " + name);
        }

        void close() {
            System.out.println("Cleaning resource: " + name);
        }
    }

    public static void main(String[] args) throws Exception {

        // 1️⃣ 创建引用队列
        ReferenceQueue<Resource> referenceQueue = new ReferenceQueue<>();

        // 2️⃣ 创建对象
        Resource resource = new Resource("DB-Connection");

        // 3️⃣ 创建幻象引用
        PhantomReference<Resource> phantomReference =
                new PhantomReference<>(resource, referenceQueue);

        // 4️⃣ 启动监听线程（模拟资源回收管理器）
        Thread cleanerThread = new Thread(() -> {
        // 阻塞等待对象进入引用队列
        Reference<? extends Resource> ref = referenceQueue.poll();
        System.out.println("PhantomReference enqueued.");

        // ⚠️ 注意：无法通过 ref.get() 拿到对象
        // ref.get() 永远是 null

        // 在这里执行“资源释放逻辑”
        System.out.println("Perform custom cleanup logic here.");
        });

        cleanerThread.setDaemon(true);
        cleanerThread.start();

        // 5️⃣ 断开强引用
//        resource = null;

        // 6️⃣ 主动触发 GC（仅用于演示）
        System.out.println("Requesting GC...");
        Thread.sleep(1000);
        System.gc();

        // 给 GC 和后台线程一点时间
        Thread.sleep(2000);
        System.out.println("Main thread finished.");
    }
}
