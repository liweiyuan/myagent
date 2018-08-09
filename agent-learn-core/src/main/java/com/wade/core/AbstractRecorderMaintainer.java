package com.wade.core;

import com.wade.base.MethodTag;
import com.wade.base.PerfStats;
import com.wade.base.PerfStatsProcessor;
import com.wade.core.config.ProfilingParams;
import com.wade.core.constant.PropertiesValues;
import com.wade.core.recorder.AccurateRecorder;
import com.wade.core.recorder.Recorder;
import com.wade.core.recorder.Recorders;
import com.wade.core.util.Logger;
import com.wade.core.util.PerfStatsCalculator;
import com.wade.core.util.ThreadUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReferenceArray;

/**
 * @author :lwy
 * @date 2018/7/31 17:02
 * 收集记录监听器
 */
public abstract class AbstractRecorderMaintainer {


    //recorder集合
    protected List<Recorders> recordersList;

    private PerfStatsProcessor processor;


    protected MethodTagMaintainer methodTagMaintainer = MethodTagMaintainer.getInstance();

    private ThreadPoolExecutor backgroundExecutor;

    private int curIndex = 0;

    private volatile Recorders curRecorders;

    //统计类型
    private boolean isAccurateModel;

    private long millTimeSlice;

    //时间片
    private volatile long nextTimeSliceEndTime = 0L;

    //
    public boolean initial(PerfStatsProcessor processor, boolean isAccurateModel, int backupRecorderCount, long milliTimeSlice) {

        this.processor = processor;
        this.isAccurateModel = isAccurateModel;
        this.millTimeSlice = getFitMillTimeSlice(milliTimeSlice);

        backupRecorderCount = getFitBackupRecordersCount(backupRecorderCount);

        if (!initRecorder(backupRecorderCount)) {
            return false;
        }
        //类似于定时任务
        if (!initRoundRobinTask()) {
            return false;
        }

        if (!initBackgroundTask(backupRecorderCount)) {
            return false;
        }

        return initOther();
    }


    //比较获取合适的timeslice
    private long getFitMillTimeSlice(long milliTimeSlice) {
        if (milliTimeSlice <= PropertiesValues.MIN_TIME_SLICE) {
            return PropertiesValues.MIN_TIME_SLICE;
        } else if (milliTimeSlice >= PropertiesValues.MAX_TIME_SLICE) {
            return PropertiesValues.MAX_TIME_SLICE;
        }
        return milliTimeSlice;
    }

    /**
     * 获取合适的recoderCount
     *
     * @param backupRecorderCount
     * @return
     */
    private int getFitBackupRecordersCount(int backupRecorderCount) {

        if (backupRecorderCount < PropertiesValues.MIN_BACKUP_RECORDERS_COUNT) {
            return PropertiesValues.MIN_BACKUP_RECORDERS_COUNT;
        } else if (backupRecorderCount > PropertiesValues.MAX_BACKUP_RECORDERS_COUNT) {
            return PropertiesValues.MAX_BACKUP_RECORDERS_COUNT;
        }
        return backupRecorderCount;
    }

    /**
     * 初始化List逻辑
     *
     * @param backupRecorderCount
     * @return
     */
    private boolean initRecorder(int backupRecorderCount) {
        recordersList = new ArrayList<>(backupRecorderCount + 1);

        for (int i = 0;  i < backupRecorderCount + 1; ++i) {
            Recorders recorders = new Recorders(new AtomicReferenceArray<Recorder>(MethodTagMaintainer.MAX_NUM));
            recordersList.add(recorders);
        }
        //分成多个Recorders来存储数据--根据backupRecorderCount来设置
        curRecorders = recordersList.get(curIndex % recordersList.size());
        return true;
    }


    //子类需要实现的接口方法
    protected abstract boolean initOther();

    //后台任务
    private boolean initBackgroundTask(int backupRecorderCount) {
        try {
            backgroundExecutor = new ThreadPoolExecutor(1,
                    2,
                    1,
                    TimeUnit.MINUTES,
                    new LinkedBlockingQueue<Runnable>(backupRecorderCount),
                    ThreadUtils.newThreadFactory("MyAgent-BackgroundExecutor_"),
                    new ThreadPoolExecutor.DiscardOldestPolicy());
            return true;
        } catch (Exception e) {
            Logger.error("RecorderMaintainer.initBackgroundTask()", e);
        }
        return false;
    }

    //轮询执行任务
    private boolean initRoundRobinTask() {
        try {

            ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1,
                    ThreadUtils.newThreadFactory("Agent-RoundRobin-Schedule"), new ThreadPoolExecutor.DiscardPolicy());
            executor.scheduleAtFixedRate(new RoundRobinRunnable(), 0, 10, TimeUnit.MILLISECONDS);
            return true;
        } catch (Exception e) {
            Logger.error("RecorderMaintainer.initRoundRobinTask()", e);
        }
        return false;

    }

    public Recorders getRecorders() {
        return curRecorders;
    }

    //获取指定的记录
    public Recorder getRecorder(int methodTagId) {
        return curRecorders.getRecorder(methodTagId);
    }


    public abstract void addRecorder(int methodTagId, ProfilingParams params);

    protected Recorder createRecorder(int methodTagId, int mostTimeThreshold, int outThresholdCount) {
        if (isAccurateModel) {
            return AccurateRecorder.getInstance(methodTagId, mostTimeThreshold, outThresholdCount);
        }
        //TODO RoughRecorder
        //return RoughRecorder.getInstance(methodTagId, mostTimeThreshold);
        return null;
    }

    /**
     * 内部类，定时收集数据
     */
    private class RoundRobinRunnable implements Runnable {
        @Override
        public void run() {

            long currentMills = System.currentTimeMillis();
            if (nextTimeSliceEndTime == 0L) {
                nextTimeSliceEndTime = ((currentMills / millTimeSlice) * millTimeSlice) + millTimeSlice;
            }

            //还在当前的时间片里
            if (nextTimeSliceEndTime > currentMills) {
                return;
            }


            nextTimeSliceEndTime = ((currentMills / millTimeSlice) * millTimeSlice) + millTimeSlice;

            try {
                final Recorders tmpCurRecorders = curRecorders;
                tmpCurRecorders.setStartTime(nextTimeSliceEndTime - 2 * millTimeSlice);
                tmpCurRecorders.setStopTime(nextTimeSliceEndTime - millTimeSlice);

                curIndex = getNextIdx(curIndex);
                Logger.debug("RecorderMaintainer.roundRobinProcessor curIndex=" + curIndex % recordersList.size());

                Recorders nextRecorders = recordersList.get(curIndex % recordersList.size());
                nextRecorders.setStartTime(nextTimeSliceEndTime - millTimeSlice);
                nextRecorders.setStopTime(nextTimeSliceEndTime);
                nextRecorders.setWriting(true);
                nextRecorders.resetRecorder();
                curRecorders = nextRecorders;

                tmpCurRecorders.setWriting(false);
                backgroundExecutor.execute(
                        new Runnable() {
                            @Override
                            public void run() {
                                long start = System.currentTimeMillis();
                                try {
                                    if (tmpCurRecorders.isWriting()) {
                                        Logger.warn("RecorderMaintainer.backgroundExecutor the tmpCurRecorders is writing!!! Please increase `MillTimeSlice` or increase `RecorderTurntableNum`!!!P1");
                                        return;
                                    }
                                    int actualSize = methodTagMaintainer.getMethodTagCount();
                                    List<PerfStats> perfStatsList = new ArrayList<>(actualSize / 2);
                                    for (int i = 0; i < actualSize; ++i) {
                                        Recorder recorder = tmpCurRecorders.getRecorder(i);
                                        //System.out.println(recorder);
                                        if (recorder == null || !recorder.isHasRecord()) {
                                            continue;
                                        }

                                        if (tmpCurRecorders.isWriting()) {
                                            Logger.warn("RecorderMaintainer.backgroundExecutor the tmpCurRecorders is writing!!! Please increase `MillTimeSlice` or increase `RecorderTurntableNum`!!!P2");
                                            break;
                                        }

                                        //TODO 取出结果
                                        MethodTag methodTag = methodTagMaintainer.getMethodTag(recorder.getMethodTagId());
                                        String requestId=recorder.getRequestId();

                                        perfStatsList.add(PerfStatsCalculator.calPerfStats(requestId,recorder, methodTag, tmpCurRecorders.getStartTime(), tmpCurRecorders.getStopTime()));

                                    }
                                    processor.process(perfStatsList, actualSize, tmpCurRecorders.getStartTime(), tmpCurRecorders.getStopTime());
                                } catch (Exception e) {
                                    Logger.error("RecorderMaintainer.backgroundExecutor error", e);
                                } finally {
                                    Logger.debug("RecorderMaintainer.backgroundProcessor finished!!! cost: " + (System.currentTimeMillis() - start) + "ms");
                                }
                            }
                        }
                );
            } catch (Exception e) {
                Logger.error("RecorderMaintainer.roundRobinExecutor error", e);
            } finally {
                Logger.debug("RecorderMaintainer.roundRobinProcessor finished!!! cost: " + (System.currentTimeMillis() - currentMills) + "ms");
            }
        }

    }

    private int getNextIdx(int curIndex) {
        if (curIndex == Integer.MAX_VALUE) {
            return 0;
        }
        return curIndex + 1;
    }
}
