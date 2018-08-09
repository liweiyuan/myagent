package com.wade.core.constant;

import com.wade.base.pop.DefaultStdoutProcessor;

/**
 * @author :lwy
 * @date 2018/7/28 18:15
 */
public interface PropertiesValues {

    String DEFAULT_PRO_FILE = "/data/myAgent/agent.properties";

    //默认的统计是打印在console
    String DEFAULT_PERF_STATS_PROCESSOR = DefaultStdoutProcessor.class.getName();

    //包名之间的分隔符
    String FILTER_SEPARATOR = ";";

    //min
    int MIN_BACKUP_RECORDERS_COUNT = 1;

    //max
    int MAX_BACKUP_RECORDERS_COUNT = 8;

    //精确
    String RECORDER_MODE_ACCURATE = "accurate";

    //粗糙
    String RECORDER_MODE_ROUGH = "rough";

    //默认为10miao
    long DEFAULT_TIME_SLICE = 60 * 1000L;


    long MIN_TIME_SLICE = 1000L;

    long MAX_TIME_SLICE = 10 * 60 * 1000L;
}
