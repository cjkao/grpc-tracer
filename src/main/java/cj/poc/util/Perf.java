package cj.poc.util;

import lombok.extern.slf4j.Slf4j;

/**
 * trace performance
 */
@Slf4j
public class Perf {
    private static ThreadLocal<Long> threadLocal;

    static {
        threadLocal = new ThreadLocal<Long>();
    }

    public static void begin() {
        threadLocal.set(System.nanoTime());
    }

    public static void end(String desc) {
        log.info(desc + " " + ((System.nanoTime() - threadLocal.get())) / 1000_000 + "ms");
    }

    /**
     * @param desc  action
     * @param times loop count
     */
    public static void perf10(String desc, int times) {
        log.info(desc + " " + ((System.nanoTime() - threadLocal.get())) / times / 1000_1000.0 + "ms");
    }
}
