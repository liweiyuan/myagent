package com.wade.core.util;

import com.wade.base.MethodTag;
import com.wade.base.PerfStats;
import com.wade.core.recorder.Recorder;

/**
 * @author :lwy
 * @date 2018/8/6 14:57
 * 计算结构
 */
public class PerfStatsCalculator {

    private static final ThreadLocal<int[]> threadLocalIntArr = new ThreadLocal<int[]>() {
        @Override
        protected int[] initialValue() {
            return new int[PerfStats.getPercentiles().length];
        }
    };

    public static PerfStats calPerfStats(String requestId, Recorder recorder, MethodTag methodTag, long startTime, long stopTime) {
        int[] sortedRecords = null;
        try {
            int effectiveCount = recorder.getEffectiveCount();
            sortedRecords = ChunkPool.getInstance().getChunk(effectiveCount * 2);
            recorder.fillSortedRecords(sortedRecords);
            return calPerfStats(requestId, methodTag, startTime, stopTime, sortedRecords, effectiveCount);
        } catch (Exception e) {
            Logger.error("PerfStatsCalculator.calPerfStats(" + recorder + ", " + methodTag + ", " + startTime + ", " + stopTime + ")", e);
        } finally {
            ChunkPool.getInstance().returnChunk(sortedRecords);
        }
        return PerfStats.getInstance(methodTag, startTime, stopTime);
    }

    private static PerfStats calPerfStats(String requestId, MethodTag methodTag, long startTime, long stopTime, int[] sortedRecords, int effectiveCount) {
        long[] pair = getTotalTimeAndTotalCount(sortedRecords);
        long totalTime = pair[0];
        int totalCount = (int) pair[1];
        PerfStats result = PerfStats.getInstance(methodTag);

        //TODO
        result.setRequestId(requestId);
        result.setTotalCount(totalCount);
        result.setStartMillTime(startTime);
        result.setStopMillTime(stopTime);

        if (totalCount <= 0 || effectiveCount <= 0) {
            return result;
        }

        double avgTime = ((double) totalTime) / totalCount;
        result.setAvgTime(avgTime);
        result.setMinTime(sortedRecords[0]);
        result.setMaxTime(sortedRecords[(effectiveCount - 1) * 2]);

        int[] topPerIndexArr = getTopPercentileIndexArr(totalCount);
        int[] topPerArr = result.getTpArr();
        int countMile = 0, perIndex = 0;
        double sigma = 0.0D;//∑
        for (int i = 0, length = sortedRecords.length; i < length; i = i + 2) {
            int timeCost = sortedRecords[i];
            int count = sortedRecords[i + 1];

            //sortedRecords中只有第0位的响应时间可以为0
            if (i > 0 && timeCost <= 0) {
                break;
            }

            countMile += count;
            int index = topPerIndexArr[perIndex];
            if (countMile >= index) {
                topPerArr[perIndex] = timeCost;
                perIndex++;
            }

            sigma += Math.pow(timeCost - avgTime, 2.0);
        }
        result.setStdDev(Math.sqrt(sigma / totalCount));

        return reviseStatistic(result);
    }

    /**
     * @param sortedRecords
     * @return : long[]: int[0]代表totalTimeCost, int[1]代表totalCount
     */
    private static long[] getTotalTimeAndTotalCount(int[] sortedRecords) {
        long[] result = {0L, 0L};
        if (sortedRecords == null || sortedRecords.length == 0) {
            return result;
        }

        for (int i = 0, length = sortedRecords.length; i < length; i = i + 2) {
            int timeCost = sortedRecords[i];
            int count = sortedRecords[i + 1];

            //sortedRecords中只有第0位的响应时间可以为0
            if (i > 0 && timeCost <= 0) {
                break;
            }

            result[0] += timeCost * count;
            result[1] += count;
        }
        return result;
    }

    private static PerfStats reviseStatistic(PerfStats perfStats) {
        int[] tpArr = perfStats.getTpArr();
        for (int i = 1; i < tpArr.length; ++i) {
            int last = tpArr[i - 1];
            int cur = tpArr[i];
            if (cur <= -1) {
                tpArr[i] = last;
            }
        }
        return perfStats;
    }

    private static int[] getTopPercentileIndexArr(int totalCount) {
        int[] result = threadLocalIntArr.get();
        double[] percentiles = PerfStats.getPercentiles();
        for (int i = 0; i < percentiles.length; ++i) {
            result[i] = getIndex(totalCount, percentiles[i]);
        }
        return result;
    }

    private static int getIndex(int totalCount, double percentile) {
        return (int) Math.ceil(totalCount * percentile);
    }
}
