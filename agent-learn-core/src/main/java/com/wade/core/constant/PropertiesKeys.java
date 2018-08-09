package com.wade.core.constant;

/**
 * @author :lwy
 * @date 2018/7/28 18:15
 */
public interface PropertiesKeys {
    //启动参数路径
    String PRO_FILE_NAME = "agentProperty";

    //统计进程
    String PERF_STATS_PROCESSOR = "PerfStatsProcessor";

    //日志统计打印
    String DEBUG_PRINT_DEBUG_LOG = "Debug.PrintDebugLog";

    //统计监控的包名
    String FILTER_INCLUDE_PACKAGES = "IncludePackages";

    //需要排除的包名
    String FILTER_EXCLUDE_PACKAGES = "ExcludePackages";

    //需要排除的classLoader
    String FILTER_INCLUDE_CLASS_LOADERS = "ExcludeClassLoaders";

    //统计记录的值
    String BACKUP_RECORDERS_COUNT = "BackupRecordersCount";

    //agent记录模式
    String RECORDER_MODE = "RecorderMode";

    //线程运行间隔
    String MILL_TIME_SLICE = "MillTimeSlice";

    String PROFILING_PARAMS_FILE_NAME = "ProfilingParamsFile";

    String PROFILING_TIME_THRESHOLD = "ProfilingTimeThreshold";

    String PROFILING_OUT_THRESHOLD_COUNT = "ProfilingOutThresholdCount";

    //排除的方法 如 equals()
    String FILTER_EXCLUDE_METHODS = "ExcludeMethods";

    //排除的私有的方法
    String EXCLUDE_PRIVATE_METHODS = "ExcludePrivateMethod";
}
