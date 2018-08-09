package com.wade.core.config;

/**
 * @author :lwy
 * @date 2018/8/1 17:31
 */
public class ProfilingParams {
    private int mostTimeThreshold;//单位:ms

    private int outThresholdCount;

    private ProfilingParams(int mostTimeThreshold, int outThresholdCount) {
        this.mostTimeThreshold = mostTimeThreshold;
        this.outThresholdCount = outThresholdCount;
    }

    public int getMostTimeThreshold() {
        return mostTimeThreshold;
    }

    public void setMostTimeThreshold(int mostTimeThreshold) {
        this.mostTimeThreshold = mostTimeThreshold;
    }

    public int getOutThresholdCount() {
        return outThresholdCount;
    }

    public void setOutThresholdCount(int outThresholdCount) {
        this.outThresholdCount = outThresholdCount;
    }

    public static ProfilingParams of(int mostTimeThreshold, int outThresholdCount) {
        return new ProfilingParams(mostTimeThreshold, outThresholdCount);
    }
}
