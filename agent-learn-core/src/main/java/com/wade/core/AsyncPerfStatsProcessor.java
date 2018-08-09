package com.wade.core;

import com.wade.base.PerfStats;
import com.wade.base.PerfStatsProcessor;
import com.wade.core.util.Logger;
import com.wade.core.util.ThreadUtils;

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author :lwy
 * @date 2018/7/30 15:45
 */
public class AsyncPerfStatsProcessor implements PerfStatsProcessor {


    private final ThreadPoolExecutor executor =
            new ThreadPoolExecutor(1, 2, 5, TimeUnit.MINUTES,
                    new LinkedBlockingQueue<Runnable>(500),
                    ThreadUtils.newThreadFactory("MyAgent-AsyncPerfStatsProcessor_"),
                    new ThreadPoolExecutor.DiscardPolicy());

    private static AsyncPerfStatsProcessor instance = null;

    private final PerfStatsProcessor target;


    public AsyncPerfStatsProcessor(PerfStatsProcessor target) {
        this.target = target;
    }

    @Override
    public void process(final List<PerfStats> perfStatsList, final int injectMethodCount, final long startMillis, final long stopMillis) {


        try{
            if(perfStatsList==null){
                return;
            }
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (target != null) {
                            target.process(perfStatsList, injectMethodCount, startMillis, stopMillis);
                        }
                    } catch (Exception e) {
                        Logger.error("AsyncPerfStatsProcessor.run()", e);
                    }
                }
            });
        }catch (Exception e) {
            Logger.error("AsyncPerfStatsProcessor.process(" + perfStatsList + ", " + injectMethodCount + ", " + startMillis + ", " + startMillis + "", e);
        }
    }

    /**
     * 初始化
     *
     * @param target
     * @return
     */
    public static synchronized AsyncPerfStatsProcessor initial(PerfStatsProcessor target) {
        if (instance != null) {
            return instance;
        }
        return instance = new AsyncPerfStatsProcessor(target);
    }
}
