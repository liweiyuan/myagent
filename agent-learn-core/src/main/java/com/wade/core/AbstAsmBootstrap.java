package com.wade.core;

import com.sun.xml.internal.bind.v2.TODO;
import com.wade.base.MethodTag;
import com.wade.base.PerfStats;
import com.wade.base.PerfStatsProcessor;
import com.wade.core.config.MyProperties;
import com.wade.core.config.ProfilingConfig;
import com.wade.core.config.ProfilingFilter;
import com.wade.core.constant.PropertiesKeys;
import com.wade.core.constant.PropertiesValues;
import com.wade.core.recorder.Recorder;
import com.wade.core.recorder.Recorders;
import com.wade.core.util.IOUtils;
import com.wade.core.util.Logger;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

/**
 * @author :lwy
 * @date 2018/7/30 15:31
 */
public abstract class AbstAsmBootstrap {

    protected AsyncPerfStatsProcessor processor;

    protected AbstractRecorderMaintainer maintainer;

    //其他的配置初始化
    public abstract boolean initOther();

    //maintainer初始化
    public abstract AbstractRecorderMaintainer doInitRecorderMaintainer();

    public boolean initConfig() {

        if (!doInital()) {
            //打印日志，日志模块
            Logger.error("AbstractBootstrap doInitial() FAILURE!!!");
            return false;
        }
        return true;
    }

    private boolean doInital() {

        //读取配置文件校验
        if (!initProperties()) {
            Logger.error("AbstractBootstrap initProperties() FAILURE!!!");
            return false;
        }
        //读取配置文件参数校验
        if (!initProfilingConfig()) {
            Logger.error("AbstractBootstrap initProfilingConfig() FAILURE!!!");
            return false;
        }

        //日志组件初始化
        if (!initLogger()) {
            Logger.error("AbstractBootstrap initLogger() FAILURE!!!");
            return false;
        }

        //监控包名过滤
        if (!initPackageFilter()) {
            Logger.error("AbstractBootstrap initPackageFilter() FAILURE!!!");
            return false;
        }

        //classLoader过滤
        if (!initClassLoaderFilter()) {
            Logger.error("AbstractBootstrap initClassLoaderFilter() FAILURE!!!");
            return false;
        }

        //TODO method filter
        //method过滤
        if (!initMethodFilter()) {
            Logger.error("AbstractBootstrap initMethodFilter() FAILURE!!!");
            return false;
        }

        //perStatsProcessor初始化
        if (!initPerStatsProcessor()) {
            Logger.error("AbstractBootstrap initPerStatsProcessor() FAILURE!!!");
            return false;
        }

        if (!initProfilingParams()) {
            Logger.error("AbstractBootstrap initProfilingParams() FAILURE!!!");
            return false;
        }

        //initMaintainer
        if (!initRecorderMaintainer()) {
            Logger.error("AbstractBootstrap initRecorderMaintainer() FAILURE!!!");
            return false;
        }


        //初始化钩子方法
        if (!initShutDownHook()) {
            Logger.error("AbstractBootstrap initMethodFilter() FAILURE!!!");
            return false;
        }

        //initOther
        if (!initOther()) {
            Logger.error("AbstractBootstrap initOther() FAILURE!!!");
            return false;
        }
        return true;
    }


    /**
     * 检查配置文件，并初始化
     *
     * @return
     */
    private boolean initProperties() {
        InputStream in = null;
        try {
            String configPath = System.getProperty(PropertiesKeys.PRO_FILE_NAME, PropertiesValues.DEFAULT_PRO_FILE);
            in = new FileInputStream(configPath);

            Properties properties = new Properties();
            properties.load(in);
            return MyProperties.initial(properties);
        } catch (IOException e) {
            Logger.error("AbstractBootstrap init properties FAILURE!!!", e);
            return false;
        }

    }

    /**
     * 参数配置
     *
     * @return
     */
    private boolean initProfilingConfig() {

        ProfilingConfig config = ProfilingConfig.getInstance();

        //收集器主要逻辑
        config.setPerStatsProcessor(MyProperties.getStr(PropertiesKeys.PERF_STATS_PROCESSOR,
                PropertiesValues.DEFAULT_PERF_STATS_PROCESSOR));

        //设置日志
        config.setPrintDebugLog(MyProperties.getBoolean(PropertiesKeys.DEBUG_PRINT_DEBUG_LOG, false));

        //设置需要监控的包名
        config.setIncludePackages(MyProperties.getStr(PropertiesKeys.FILTER_INCLUDE_PACKAGES, ""));

        //设置需要排除的包名
        config.setExcludePackages(MyProperties.getStr(PropertiesKeys.FILTER_EXCLUDE_PACKAGES, ""));


        //classLoader过滤
        config.setExcludeClassLoaders(MyProperties.getStr(PropertiesKeys.FILTER_INCLUDE_CLASS_LOADERS, ""));

        //统计记录提取
        config.setBackupRecorderCount(MyProperties.getInteger(PropertiesKeys.BACKUP_RECORDERS_COUNT, PropertiesValues.MIN_BACKUP_RECORDERS_COUNT));

        //统计模式
        config.setRecorderMode(MyProperties.getStr(PropertiesKeys.RECORDER_MODE, PropertiesValues.RECORDER_MODE_ACCURATE));//默认值

        //线程间隔数
        config.setMilliTimeSlice(MyProperties.getLong(PropertiesKeys.MILL_TIME_SLICE, PropertiesValues.DEFAULT_TIME_SLICE));

        //
        config.setProfilingParamsFile(MyProperties.getStr(PropertiesKeys.PROFILING_PARAMS_FILE_NAME, ""));

        //通用参数配置
        config.setCommonProfilingParams(MyProperties.getInteger(PropertiesKeys.PROFILING_TIME_THRESHOLD, 500), MyProperties.getInteger(PropertiesKeys.PROFILING_OUT_THRESHOLD_COUNT, 50));

        //method参数配置
        config.setExcludeMethods(MyProperties.getStr(PropertiesKeys.FILTER_EXCLUDE_METHODS, ""));

        //method私有排除
        config.setExcludePrivateMethod(MyProperties.getBoolean(PropertiesKeys.EXCLUDE_PRIVATE_METHODS, true));

        //TODO 后续添加
        return true;
    }

    /**
     * 日志组件初始化
     *
     * @return
     */
    private boolean initLogger() {
        try {
            Logger.setDebugEnable(ProfilingConfig.getInstance().getPrintDebugLog());
            return true;
        } catch (Exception e) {
            Logger.error("AbstractBootstrap.initLogger()", e);
            return false;
        }

    }

    /**
     * 监控包名
     *
     * @return
     */
    private boolean initPackageFilter() {
        try {
            String includePackagesStr = ProfilingConfig.getInstance().getIncludePackages();
            String[] includePackages = includePackagesStr.split(PropertiesValues.FILTER_SEPARATOR);

            if (includePackages.length > 0) {
                for (String includePackage : includePackages) {
                    //添加到监控组件中
                    includePackage = includePackage.trim();
                    ProfilingFilter.addIncludePackages(includePackage);
                }
            } else {
                return false;
            }

            String excludePackagesStr = ProfilingConfig.getInstance().getExcludePackages();
            String[] excludePackages = excludePackagesStr.split(PropertiesValues.FILTER_SEPARATOR);
            if (excludePackages.length > 0) {
                for (String excludePackage : excludePackages) {
                    //添加到监控组件中
                    excludePackage = excludePackage.trim();
                    ProfilingFilter.addExcludePackages(excludePackage);
                }
            }
            return true;
        } catch (Exception e) {
            Logger.error("AbstractBootstrap.initPackageFilter()", e);
            return false;
        }


    }

    /**
     * classLoader过滤
     *
     * @return
     */
    private boolean initClassLoaderFilter() {
        try {
            String excludeClassLoaders = ProfilingConfig.getInstance().getExcludeClassLoaders();
            String[] excludeArr = excludeClassLoaders.split(PropertiesValues.FILTER_SEPARATOR);
            if (excludeArr.length > 0) {
                for (String classLoader : excludeArr) {
                    ProfilingFilter.addExcludeClassLoader(classLoader);
                }
            }
            return true;
        } catch (Exception e) {
            Logger.error("AbstractBootstrap.initClassLoaderFilter()", e);
            return false;
        }

    }

    private boolean initPerStatsProcessor() {
        try {
            String className = ProfilingConfig.getInstance().getPerStatsProcessor();
            if (className == null || className.isEmpty()) {
                Logger.error("AbstractBootstrap.initPerfStatsProcessor() MyAgent.PSP NOT FOUND!!!");
                return false;
            }

            Class clz = this.getClass().getClassLoader().loadClass(className);
            Object obj = clz.newInstance();
            if (!(obj instanceof PerfStatsProcessor)) {
                Logger.error("AbstractBootstrap.initPerStatsProcessor()");
                return false;
            }
            processor = AsyncPerfStatsProcessor.initial((PerfStatsProcessor) obj);
            return true;

        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            Logger.error("AbstractBootstrap.initPerStatsProcessor()", e);
            return false;
        }
    }

    //maintainer初始化
    private boolean initRecorderMaintainer() {

        return (maintainer = doInitRecorderMaintainer()) != null;
    }

    private boolean initProfilingParams() {
        InputStream in = null;
        try {
            ProfilingConfig config = ProfilingConfig.getInstance();
            String profilingParamFile = config.getProfilingParamsFile();
            if (profilingParamFile == null || profilingParamFile.isEmpty()) {
                Logger.warn("profilingParamFile is empty!");
                return true;
            }

            in = new FileInputStream(profilingParamFile);
            Properties properties = new Properties();
            properties.load(in);

            Set<String> keys = properties.stringPropertyNames();
            for (String key : keys) {
                String value = properties.getProperty(key);
                if (value == null) {
                    continue;
                }

                String[] strings = value.split(":");
                if (strings.length != 2) {
                    continue;
                }

                int timeThreshold = getInt(strings[0].trim(), 500);
                int outThresholdCount = getInt(strings[1].trim(), 50);
                config.addProfilingParam(key.replace('.', '/'), timeThreshold, outThresholdCount);
            }

            return true;
        } catch (Exception e) {
            Logger.error("AbstractBootstrap.initProfilingParams()", e);
        } finally {
            IOUtils.closeQuietly(in);
        }
        return false;
    }

    /**
     * 方法过滤
     *
     * @return
     */
    private boolean initMethodFilter() {
        try {
            String excludeMethods = ProfilingConfig.getInstance().getExcludeMethods();
            if ("".equals(excludeMethods)) {
                return true;
            }
            String[] excludeArr = excludeMethods.split(PropertiesValues.FILTER_SEPARATOR);
            for (String method : excludeArr) {
                ProfilingFilter.addExculdMethods(method);
            }
            return true;
        } catch (Exception e) {
            Logger.error("AbstractBootstrap.initMethodFilter()", e);
        }
        return false;
    }


    /**
     * 初始化钩子方法
     *
     * @return
     */
    private boolean initShutDownHook() {
        try {
            Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
                @Override
                public void run() {
                    Logger.info("enter ShutdownHook...");
                    try {
                        MethodTagMaintainer methodTagMaintainer = MethodTagMaintainer.getInstance();
                        Recorders recorders = maintainer.getRecorders();
                        List<PerfStats> perfStatsList = new ArrayList<>(recorders.size());
                        int methonTagCount = methodTagMaintainer.getMethodTagCount();
                        for (int i = 0; i < methonTagCount; i++) {
                            Recorder recorder = recorders.getRecorder(i);
                            if (recorder == null || !recorder.isHasRecord()) {
                                continue;
                            }

                            MethodTag methodTag=methodTagMaintainer.getMethodTag(recorder.getMethodTagId());

                            //TODO 后续实现聚合数据
                            //perfStatsList.add();
                        }
                        //TODO 后续添加
                    } catch (Exception e) {
                        Logger.error("", e);
                    } finally {
                        Logger.info("EXIT ShutdownHook...");
                    }
                }
            }, "the agent service is shutdown."));
            return true;
        } catch (Exception e) {
            Logger.error("AbstractBootstrap.initShutDownHook() is failed", e);
            return false;
        }


    }

    private int getInt(String str, int defaultValue) {
        try {
            return Integer.valueOf(str);
        } catch (Exception e) {
            Logger.error("AbstractBootstrap.getInt(" + str + ")", e);
        }
        return defaultValue;
    }
}
