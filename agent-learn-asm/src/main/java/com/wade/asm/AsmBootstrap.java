package com.wade.asm;

import com.wade.aop.MethodExitAspectMaintainer;
import com.wade.core.AbstAsmBootstrap;
import com.wade.core.AbstractRecorderMaintainer;
import com.wade.core.config.ProfilingConfig;
import com.wade.core.util.Logger;

/**
 * @author :lwy
 * @date 2018/7/27 18:29
 */
class AsmBootstrap extends AbstAsmBootstrap {

    private static AsmBootstrap bootstrap = new AsmBootstrap();

    public static AsmBootstrap getBootstrap() {
        return bootstrap;
    }


    //初始化RecordMaintainer
    @Override
    public AbstractRecorderMaintainer doInitRecorderMaintainer() {

        boolean isAccurateModel = ProfilingConfig.getInstance().isAccurateMode();
        long milliTimeSlice = ProfilingConfig.getInstance().getMilliTimeSlice();
        int backupRecorderCount = ProfilingConfig.getInstance().getBackupRecorderCount();
        ASMRecorderMaintainer maintainer = ASMRecorderMaintainer.getInstance();
        if (maintainer.initial(processor, isAccurateModel, backupRecorderCount, milliTimeSlice)) {
            return maintainer;
        }
        return null;
    }


    //其他配置初始化
    @Override
    public boolean initOther() {
        //监视器配置
        try {
            MethodExitAspectMaintainer.setMaintainer((ASMRecorderMaintainer) maintainer);
            MethodExitAspectMaintainer.setRunning(true);
            return true;
        } catch (Exception e) {
            Logger.error("ASMBootstrap.initProfilerAspect()", e);
        }
        return false;
    }
}
