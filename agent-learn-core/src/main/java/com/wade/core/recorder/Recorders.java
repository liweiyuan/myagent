package com.wade.core.recorder;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReferenceArray;

/**
 * @author :lwy
 * @date 2018/8/1 15:50
 * 记录集合
 */
public class Recorders {

    //存储的数组
    private AtomicReferenceArray<Recorder> recorderArr;

    private AtomicInteger recorderCount;

    private volatile boolean writing = false;

    private volatile long startTime;

    private volatile long stopTime;

    public Recorders(AtomicReferenceArray<Recorder> recorderArr) {
        this.recorderArr = recorderArr;
        this.recorderCount = new AtomicInteger(0);
    }


    public Recorder getRecorder(int index) {
        return this.recorderArr.get(index);
    }

    public void setRecorder(int index, Recorder recorder) {
        this.recorderArr.set(index, recorder);
        this.recorderCount.incrementAndGet();
    }


    public int size(){
        return recorderArr.length();
    }

    public boolean isWriting() {
        return writing;
    }

    public void setWriting(boolean writing) {
        this.writing = writing;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getStopTime() {
        return stopTime;
    }

    public void setStopTime(long stopTime) {
        this.stopTime = stopTime;
    }

    /**
     * 重置
     */
    public void resetRecorder(){
        int count = recorderCount.get();
        for (int i = 0; i < count; ++i) {
            Recorder recorder = recorderArr.get(i);
            if (recorder != null) {
                recorder.resetRecord();
            }
        }
    }


    @Override
    public String toString() {
        return "Recorders{" +
                "recorderArr=" + recorderArr +
                ", recorderCount=" + recorderCount +
                ", writing=" + writing +
                ", startTime=" + startTime +
                ", stopTime=" + stopTime +
                '}';
    }
}
