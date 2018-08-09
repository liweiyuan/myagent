package com.wade.core.config;

import java.util.Properties;

/**
 * @author :lwy
 * @date 2018/7/28 18:20
 */
public class MyProperties {

    private static Properties properties;

    public synchronized static boolean initial(Properties prop) {
        if (properties != null || prop == null) {
            return false;
        }

        properties = prop;
        return true;
    }

    public static String getStr(String propKey, String defaultValue) {
        checkState();

        String result = getStr(propKey);
        if (result != null) {
            return result;
        }
        return defaultValue;
    }

    private static String getStr(String propKey) {
        checkState();

        String value = System.getProperty(propKey);
        if (value != null) {
            return value;
        }
        return properties.getProperty(propKey);
    }

    private static void checkState() {
        if (properties == null) {
            throw new IllegalStateException("MyProperties is not initial yet!!!");
        }
    }

    /**
     * 获取boolean 值
     *
     * @param key
     * @param defaultValue
     * @return
     */
    public static boolean getBoolean(String key, boolean defaultValue) {
        checkState();

        String result = getStr(key);
        if (result != null) {
            return result.equalsIgnoreCase("true");
        }
        return defaultValue;
    }

    /**
     * 获取int值
     *
     * @param backupRecordersCount
     * @param minBackupRecordersCount
     * @return
     */
    public static int getInteger(String backupRecordersCount, int minBackupRecordersCount) {
        checkState();

        String result = getStr(backupRecordersCount);
        if (result != null) {
            return Integer.valueOf(result);
        }
        return minBackupRecordersCount;
    }

    /**
     * 获取long值
     * @param millTimeSlice
     * @param defaultTimeSlice
     * @return
     */
    public static long getLong(String millTimeSlice, long defaultTimeSlice) {
        checkState();

        String result=getStr(millTimeSlice);
        if(result!=null){
            return Long.valueOf(result);
        }
        return defaultTimeSlice;
    }
}
