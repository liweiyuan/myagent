package com.wade.aop;

import com.wade.asm.ASMRecorderMaintainer;
import com.wade.core.MethodTagMaintainer;
import com.wade.core.recorder.Recorder;
import com.wade.core.util.Logger;
import com.wade.threadlocal.RequestIdThreadLocal;

import java.util.UUID;

/**
 * @author :lwy
 * @date 2018/7/31 16:10
 */
public class MethodExitAspectMaintainer {


    private static volatile boolean running = false;

    //记录监视器
    private static volatile ASMRecorderMaintainer recorderMaintainer;

    public static void methodExit(long startNanos, int methodTagId) {

        try {
            if (!running) {
                Logger.warn("ProfilingAspect.doProfiling(): methodTagId=" + methodTagId +
                        ", methodTag=" + MethodTagMaintainer.getInstance().getMethodTag(methodTagId) +
                        ", startNanos: " + startNanos + ", IGNORED!!!");
                return;
            }
            //TODO 记录操作 Record
            Recorder recorder = recorderMaintainer.getRecorder(methodTagId);
            if (recorder == null) {
                Logger.warn("ProfilingAspect.doProfiling(): methodTagId=" + methodTagId + ", methodTag=" + MethodTagMaintainer.getInstance().getMethodTag(methodTagId) + ", startNanos: " + startNanos + ", recorder is null IGNORED!!!");
                return;
            }


            long endNanos = System.nanoTime();
            //记录时间
            recorder.recordTime(startNanos, endNanos);


            //TODO
            //根据当前线程获取requestId
            /*String requestId = null;
            if (RequestIdThreadLocal.requestId.get() != null) {
                requestId = RequestIdThreadLocal.requestId.get();
            } else {
                requestId = UUID.randomUUID().toString();
            }
            System.err.println(Thread.currentThread().getId());
            recorder.setRequestId(requestId);*/
        } catch (Exception e) {
            Logger.error("ProfilingAspect.doProfiling(" + startNanos + ", " + methodTagId + ", " + MethodTagMaintainer.getInstance().getMethodTag(methodTagId) + ")", e);
        }
    }

    public static void setMaintainer(ASMRecorderMaintainer maintainer) {
        recorderMaintainer = maintainer;
    }

    public static void setRunning(boolean run) {
        running = run;
    }
}
