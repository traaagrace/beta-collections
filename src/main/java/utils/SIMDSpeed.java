package utils;

import java.util.concurrent.*;
import java.util.concurrent.atomic.DoubleAdder;
import java.util.*;

public class SIMDSpeed {

    static final int DIM  = 1024;    // 向量维度
    static final int RUNS = 100_000; // 运行次数

    // ========== 1. 普通循环 ==========
    static double plain(double[] v1, double[] v2) {
        double sum = 0;
        for (int i = 0; i < v1.length; i++) {
            double d = v1[i] - v2[i];
            sum += d * d;
        }
        return Math.sqrt(sum);
    }

    // ========== 2. 循环展开 ==========
    static double unrolled(double[] v1, double[] v2) {
        int i = 0, len = v1.length;
        double sum = 0;
        for (; i <= len - 4; i += 4) {
            double d0 = v1[i]   - v2[i];
            double d1 = v1[i+1] - v2[i+1];
            double d2 = v1[i+2] - v2[i+2];
            double d3 = v1[i+3] - v2[i+3];
            sum += d0*d0 + d1*d1 + d2*d2 + d3*d3;
        }
        for (; i < len; i++) { double d = v1[i]-v2[i]; sum += d*d; }
        return Math.sqrt(sum);
    }

//    // ========== 3. Vector API (SIMD) ==========
//    static double simd(double[] v1, double[] v2) {
//        int i = 0, upper = SPECIES.loopBound(v1.length);
//        DoubleVector sumVec = DoubleVector.zero(SPECIES);
//        for (; i < upper; i += SPECIES.length()) {
//            DoubleVector a    = DoubleVector.fromArray(SPECIES, v1, i);
//            DoubleVector b    = DoubleVector.fromArray(SPECIES, v2, i);
//            DoubleVector diff = a.sub(b);
//            sumVec = diff.fma(diff, sumVec);
//        }
//        double sum = sumVec.reduceLanes(VectorOperators.ADD);
//        for (; i < v1.length; i++) { double d = v1[i]-v2[i]; sum += d*d; }
//        return Math.sqrt(sum);
//    }

    // ========== 4. 多线程 ==========
    static double parallel(double[] v1, double[] v2) throws Exception {
        int cores = Runtime.getRuntime().availableProcessors();
        int chunk = v1.length / cores;
        List<Future<Double>> futures = new ArrayList<>();
        ExecutorService pool = ForkJoinPool.commonPool();
        for (int t = 0; t < cores; t++) {
            final int s = t * chunk;
            final int e = (t == cores - 1) ? v1.length : s + chunk;
            futures.add(pool.submit(() -> {
                double sum = 0;
                for (int i = s; i < e; i++) { double d = v1[i]-v2[i]; sum += d*d; }
                return sum;
            }));
        }
        double total = 0;
        for (Future<Double> f : futures) total += f.get();
        return Math.sqrt(total);
    }

    // ========== Benchmark 核心 ==========
    static long bench(String name, ThrowingRunnable fn) throws Exception {
        // 预热
        for (int i = 0; i < 1000; i++) fn.run();

        long start = System.nanoTime();
        for (int i = 0; i < RUNS; i++) fn.run();
        long elapsed = System.nanoTime() - start;

        System.out.printf("%-20s 总耗时: %6d ms  均值: %5.2f μs%n",
                name, elapsed / 1_000_000, elapsed / 1_000.0 / RUNS);
        return elapsed;
    }

    @FunctionalInterface
    interface ThrowingRunnable { void run() throws Exception; }

    // ========== 主函数 ==========
    public static void main(String[] args) throws Exception {
        Random rng = new Random(42);
        double[] v1 = new double[DIM];
        double[] v2 = new double[DIM];
        for (int i = 0; i < DIM; i++) { v1[i] = rng.nextDouble(); v2[i] = rng.nextDouble(); }

        System.out.printf("维度: %d  运行次数: %,d%n", DIM, RUNS);
        System.out.println("=".repeat(55));

        long base  = bench("普通循环",   () -> plain(v1, v2));
        bench("循环展开",   () -> unrolled(v1, v2));
        bench("多线程",     () -> parallel(v1, v2));

        System.out.println("=".repeat(55));

    }
}