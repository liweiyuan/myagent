package com.wade.core.recorder;

/**
 * @author :lwy
 * @date 2018/8/1 15:42
 */
public abstract class Recorder {

    private  String requestId;

    private final int MethodTagId;

    volatile boolean hasRecord;

    public Recorder( int methodTagId) {
        MethodTagId = methodTagId;
    }

    public int getMethodTagId() {
        return MethodTagId;
    }

    public boolean isHasRecord() {
        return hasRecord;
    }



    public void setHasRecord(boolean hasRecord) {
        this.hasRecord = hasRecord;
    }

    //指定时间区间内记录
    public abstract void recordTime(long startTime,long endTime);

    /**
     * 为了节省内存的使用，利用int[]作为返回结果
     *
     * @param arr : arr.length为effectiveRecordCount的两倍!!! 其中，第0位存timeCost，第1位存count，第2位存timeCost，第3位存count，以此类推
     */
    public abstract void fillSortedRecords(int[] arr);

    /**
     * 获取有效的记录的个数
     */
    public abstract int getEffectiveCount();

    public abstract void resetRecord();

    /**
     * 记录数据的阈值
     * @return
     */
    public abstract int getOutThresholdCount();


    @Override
    public String toString() {
        return "Recorder{" +
                "requestId='" + requestId + '\'' +
                ", MethodTagId=" + MethodTagId +
                ", hasRecord=" + hasRecord +
                '}';
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }
}
