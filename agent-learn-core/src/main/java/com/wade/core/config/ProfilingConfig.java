package com.wade.core.config;

import com.wade.core.constant.PropertiesValues;
import com.wade.core.util.MapUtils;

import java.util.Map;

/**
 * @author :lwy
 * @date 2018/7/29 17:26
 */
//简要配置
public class ProfilingConfig {

    private static final ProfilingConfig instance = new ProfilingConfig();

    //单例
    public static ProfilingConfig getInstance() {
        return instance;
    }

    //统计进程
    private String perStatsProcessor;

    //日志是否开启debug
    private boolean printDebugLog;

    //监控的包名
    private String includePackages;

    //需要排除的包名
    private String excludePackages;

    //需要排除的classLoader
    private String excludeClassLoaders;

    //统计记录
    private int backupRecorderCount;

    //agent运行模式 精确于粗糙
    private String recorderMode;

    //线程运行间隔
    private long milliTimeSlice;


    private String profilingParamsFile;

    private ProfilingParams commonProfilingParams;

    //需要排除的方法
    private String excludeMethods;

    //是否排除私有的方法
    private boolean excludePrivateMethod;

    private Map<String, ProfilingParams> profilingParamsMap = MapUtils.createHashMap(100);

    public String getPerStatsProcessor() {
        return perStatsProcessor;
    }

    public void setPerStatsProcessor(String perStatsProcessor) {
        this.perStatsProcessor = perStatsProcessor;
    }

    public boolean getPrintDebugLog() {
        return printDebugLog;
    }

    public void setPrintDebugLog(boolean printDebugLog) {
        this.printDebugLog = printDebugLog;
    }


    public String getIncludePackages() {
        return includePackages;
    }

    public void setIncludePackages(String includePackages) {
        this.includePackages = includePackages;
    }

    public String getExcludePackages() {
        return excludePackages;
    }

    public void setExcludePackages(String excludePackages) {
        this.excludePackages = excludePackages;
    }

    public String getExcludeClassLoaders() {
        return excludeClassLoaders;
    }

    public void setExcludeClassLoaders(String excludeClassLoaders) {
        this.excludeClassLoaders = excludeClassLoaders;
    }


    public int getBackupRecorderCount() {
        return backupRecorderCount;
    }

    public void setBackupRecorderCount(int backupRecorderCount) {
        this.backupRecorderCount = backupRecorderCount;
    }


    public String getRecorderMode() {
        return recorderMode;
    }

    public void setRecorderMode(String recorderMode) {
        this.recorderMode = recorderMode;
    }

    public long getMilliTimeSlice() {
        return milliTimeSlice;
    }

    public void setMilliTimeSlice(long milliTimeSlice) {
        this.milliTimeSlice = milliTimeSlice;
    }

    public String getProfilingParamsFile() {
        return profilingParamsFile;
    }

    public void setProfilingParamsFile(String profilingParamsFile) {
        this.profilingParamsFile = profilingParamsFile;
    }

    /**
     * 判断记录模式 accurate
     */
    public boolean isAccurateMode() {
        return recorderMode.equals(PropertiesValues.RECORDER_MODE_ACCURATE);
    }

    public void addProfilingParam(String methodName, int timeThreshold, int outThresholdCount) {
        profilingParamsMap.put(methodName, ProfilingParams.of(timeThreshold, outThresholdCount));
    }

    public void setCommonProfilingParams(int timeThreshold, int outThresholdCount) {
        this.commonProfilingParams = ProfilingParams.of(timeThreshold, outThresholdCount);
    }

    public ProfilingParams getCommonProfilingParams() {
        return commonProfilingParams;
    }


    public String getExcludeMethods() {
        return excludeMethods;
    }

    public void setExcludeMethods(String excludeMethods) {
        this.excludeMethods = excludeMethods;
    }

    public boolean isExcludePrivateMethod() {
        return excludePrivateMethod;
    }

    public void setExcludePrivateMethod(boolean excludePrivateMethod) {
        this.excludePrivateMethod = excludePrivateMethod;
    }

    public ProfilingParams getProfilingParam(String methodName) {
        ProfilingParams params = profilingParamsMap.get(methodName);
        if (params != null) {
            return params;
        }

        return commonProfilingParams;
    }
}
