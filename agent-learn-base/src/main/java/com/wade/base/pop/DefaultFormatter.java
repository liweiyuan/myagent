package com.wade.base.pop;

import com.wade.base.PerfStats;
import com.wade.base.meta.ResultDataBean;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * @author :lwy
 * @date 2018/7/29 17:52
 */
public class DefaultFormatter {

    private static final long MB = 1048576L;

    private static final ThreadLocal<DateFormat> DEFAULT_DATE_FORMAT = new ThreadLocal<DateFormat>() {
        @Override
        protected DateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        }
    };

    /**
     * 格式化输出
     *
     * @param perfStatsList
     * @param injectMethodCount
     * @param startMillis
     * @param stopMillis
     * @return
     */
    public static List<ResultDataBean> getFormatStr(List<PerfStats> perfStatsList, int injectMethodCount, long startMillis, long stopMillis) {
        //int[] statisticsArr = getStatistics(perfStatsList);




        //TODO 获取jvm参数
        System.out.println("Jvm 堆内存参数：");
        MemoryMXBean memory = ManagementFactory.getMemoryMXBean();
        //堆内存
        MemoryUsage headMemory = memory.getHeapMemoryUsage();
        String info = String.format("\ninit: %s\t max: %s\t used: %s\t committed: %s\t use rate: %s\n",
                headMemory.getInit() / MB + "MB",
                headMemory.getMax() / MB + "MB",
                headMemory.getUsed() / MB + "MB",
                headMemory.getCommitted() / MB + "MB",
                headMemory.getUsed() * 100 / headMemory.getCommitted() + "%"

        );

        System.out.print(info);

        System.out.println("Jvm 非堆内存参数：");
        //非堆内存
        MemoryUsage nonheadMemory = memory.getNonHeapMemoryUsage();

        info = String.format("init: %s\t max: %s\t used: %s\t committed: %s\t use rate: %s\n",
                nonheadMemory.getInit() / MB + "MB",
                nonheadMemory.getMax() / MB + "MB",
                nonheadMemory.getUsed() / MB + "MB",
                nonheadMemory.getCommitted() / MB + "MB",
                nonheadMemory.getUsed() * 100 / nonheadMemory.getCommitted() + "%"

        );

        System.out.println(info);

        System.out.println("Jvm 垃圾收集器参数：");
        //Concurrent Mark-Sweep CMS垃圾回收器  标记清楚 老年代
        //Parallel Scavenge   并行垃圾回收器
        List<GarbageCollectorMXBean> garbages = ManagementFactory.getGarbageCollectorMXBeans();
        for (GarbageCollectorMXBean garbage : garbages) {
            String garbageInfo = String.format("name: %s\t count:%s\t took:%s\t pool name:%s",
                    garbage.getName(),
                    garbage.getCollectionCount(),
                    garbage.getCollectionTime(),
                    Arrays.deepToString(garbage.getMemoryPoolNames()));
            System.out.println(garbageInfo);
        }

        StringBuilder sb = new StringBuilder();
        sb.append("MyAgent Performance Statistics [").append(getStr(startMillis)).append(", ").append(getStr(stopMillis)).append("]").append(String.format("%n"));
        sb.append("Method[").append(perfStatsList.size()).append("/").append(injectMethodCount).append("]");
        System.out.println(sb);

        List<ResultDataBean> resultDataBeanList = new ArrayList<>();
        if (perfStatsList.isEmpty()) {
            return resultDataBeanList;
        }

        for (PerfStats perfStats : perfStatsList) {
            if (perfStats.getTotalCount() <= 0) {
                continue;
            }
            ResultDataBean bean = ResultDataBean.newBuilder()
                    .withCollectTime(getStr(startMillis) + "------" + getStr(stopMillis))
                    .withMethodName(perfStats.getMethodTag().getClassName() + "/" + perfStats.getMethodTag().getMethodName())
                    .withTotalCount(String.valueOf(perfStats.getTotalCount()))
                    .withMaxTime(String.valueOf(perfStats.getMaxTime()))
                    .withAverageTime(String.valueOf(perfStats.getAvgTime()))
                    .withMinTime(String.valueOf(perfStats.getMinTime()))
                    .withStartMillTime(getStr(startMillis))
                    .withStopMillTime(getStr(stopMillis))
                    .withRequestId(perfStats.getRequestId())
                    .build();
            resultDataBeanList.add(bean);
        }

        //TODO 后续修改
        return resultDataBeanList;
    }


    private static int[] getStatistics(List<PerfStats> perfStatsList) {
        int[] result = {1};
        for (PerfStats stats : perfStatsList) {
            if (stats == null || stats.getMethodTag() == null) {
                continue;
            }

            result[0] = Math.max(result[0], stats.getMethodTag().getSimpleDesc().length());
        }
        return result;
    }

    private static String getStr(long startMillis) {
        return DEFAULT_DATE_FORMAT.get().format(startMillis);
    }
}
