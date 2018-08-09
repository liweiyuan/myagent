package com.wade.core.util;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author :lwy
 * @date 2018/7/30 15:49
 */
public class ThreadUtils {

    public static ThreadFactory newThreadFactory(final String prefix) {
        return new ThreadFactory() {
            AtomicInteger atomicInteger = new AtomicInteger(0);

            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, prefix + atomicInteger.getAndIncrement());
            }
        };
    }
}
