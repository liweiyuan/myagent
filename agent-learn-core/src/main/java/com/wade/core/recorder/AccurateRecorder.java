package com.wade.core.recorder;

/**
 * @author :lwy
 * @date 2018/8/1 16:28
 */

import com.wade.core.util.MapUtils;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;

/**
 * MyAgent默认使用的是RoughRecorder，如果需要使用AccurateRecorder，则在启动参数里加上-DMyAgent.recorder.mode=accurate
 * 该类用于精确存储某一个api在指定时间片内的响应时间
 * 为了减小内存占用，利用数组+Map的方式:
 * 1、将小于mostTimeThreshold的响应时间记录在数组中；
 * 2、将大于等于mostTimeThreshold的响应时间记录到Map中。
 */
public class AccurateRecorder extends Recorder {

    private final AtomicIntegerArray accurateArr;

    private final ConcurrentHashMap<Integer, AtomicInteger> accurateMap;


    public AccurateRecorder(int methodTagId, int mostTimeThreshold, int outThresholdCount) {
        super( methodTagId);
        this.accurateArr = new AtomicIntegerArray(mostTimeThreshold + 1);
        this.accurateMap = new ConcurrentHashMap<>(MapUtils.getFitCapacity(outThresholdCount));
    }

    @Override
    public void recordTime(long startNanoTime, long endNanoTime) {
        if (startNanoTime > endNanoTime) {
            return;
        }
        //System.err.println(startNanoTime);
        //System.err.println(endNanoTime);

        //表示开始记录
        hasRecord = true;

        //获取时长，来作为key，存储
        int elapsedTime = (int) ((endNanoTime - startNanoTime) / 1000000);
        if (elapsedTime < accurateArr.length()) {
            accurateArr.incrementAndGet(elapsedTime);
            return;
        }

        //TODO 分段来统计时长

        //根据key,key就是时长，来获取count,如果有，则设置为+1.表示这个时长的执行了多少次
        AtomicInteger count = accurateMap.get(elapsedTime);
        if (count != null) {
            count.incrementAndGet();
            return;
        }

        AtomicInteger oldCounter = accurateMap.putIfAbsent(elapsedTime, new AtomicInteger(1));
        if (oldCounter != null) {
            oldCounter.incrementAndGet();
        }

        //System.err.println("时间：" + elapsedTime);
    }

    @Override
    public void fillSortedRecords(int[] arr) {
        int idx = 0;
        for (int i = 0; i < accurateArr.length(); ++i) {
            int count = accurateArr.get(i);
            if (count > 0) {
                arr[idx++] = i;
                arr[idx++] = count;
            }
        }
        fillMapRecord(arr, idx);
    }

    private void fillMapRecord(int[] arr, int offset) {
        int idx = offset;
        for (Map.Entry<Integer, AtomicInteger> entry : accurateMap.entrySet()) {
            if (entry.getValue().get() > 0) {
                arr[idx++] = entry.getKey();
            }
        }

        Arrays.sort(arr, offset, idx);
        for (int i = idx - 1; i >= offset; --i) {
            arr[2 * i - offset] = arr[i];
            arr[2 * i + 1 - offset] = accurateMap.get(arr[i]).get();
        }
    }

    @Override
    public int getEffectiveCount() {
        int result = 0;
        for (int i = 0; i < accurateArr.length(); ++i) {
            int count = accurateArr.get(i);
            if (count > 0) {
                result++;
            }
        }

        for (Map.Entry<Integer, AtomicInteger> entry : accurateMap.entrySet()) {
            if (entry.getValue().get() > 0) {
                result++;
            }
        }
        return result;
    }

    @Override
    public void resetRecord() {
        for (int i = 0; i < accurateArr.length(); ++i) {
            accurateArr.set(i, 0);
        }

        //map
        Iterator<Map.Entry<Integer, AtomicInteger>> iterator = accurateMap.entrySet().iterator();


        //TODO 后续要更改
        /*while (iterator.hasNext()) {
            Map.Entry<Integer, AtomicInteger> entry = iterator.next();
            //移除
            iterator.remove();
        }*/

        while (iterator.hasNext()) {
            Map.Entry<Integer, AtomicInteger> entry = iterator.next();
            if ((entry.getKey() > 1.5 * accurateArr.length())
                    || entry.getValue().get() <= 0) {
                iterator.remove();
            } else {
                entry.getValue().set(0);
            }
        }

        hasRecord = false;
    }

    @Override
    public int getOutThresholdCount() {
        return 0;
    }


    //初始化单例对象

    public static AccurateRecorder getInstance(int methodTagId, int mostTimeThreshold, int outThresholdCount) {
        return new AccurateRecorder(methodTagId, mostTimeThreshold, outThresholdCount);
    }
}
