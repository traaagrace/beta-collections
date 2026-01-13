package thread;

import java.util.concurrent.ThreadPoolExecutor;

import java.util.concurrent.*;

public class ThreadPoolExecutorDemo {

    public static void main(String[] args) {

        // 1. 创建线程池
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                2,                      // corePoolSize：核心线程数
                4,                      // maximumPoolSize：最大线程数
                60,                     // keepAliveTime
                TimeUnit.SECONDS,       // 时间单位
                new ArrayBlockingQueue<>(2), // 工作队列
                Executors.defaultThreadFactory(), // 线程工厂
                new ThreadPoolExecutor.AbortPolicy() // 拒绝策略
        );

        // 2. 提交任务
        for (int i = 1; i <= 6; i++) {
            final int taskId = i;
            executor.execute(() -> {
                System.out.println(
                        Thread.currentThread().getName()
                                + " 正在执行任务 " + taskId
                );
                try {
                    Thread.sleep(2000); // 模拟业务耗时
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }

        // 3. 关闭线程池
        executor.shutdown();
    }
}
