package com.wade.threadlocal;

/**
 * @author :lwy
 * @date 2018/8/7 14:13
 * <p>
 * 请求链路 requestID
 */
public class RequestIdThreadLocal {

    public static ThreadLocal<String> requestId = new ThreadLocal<>();
}
