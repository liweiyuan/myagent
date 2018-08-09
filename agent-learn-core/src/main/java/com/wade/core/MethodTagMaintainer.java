package com.wade.core;

import com.wade.base.MethodTag;
import com.wade.core.util.Logger;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReferenceArray;

/**
 * @author :lwy
 * @date 2018/7/31 11:44
 * 具体的采集哪些方法的实现策略
 */
public class MethodTagMaintainer extends AbstractMethodTagMaintainer {

    static final int MAX_NUM = 1024 * 128;

    private final AtomicInteger index = new AtomicInteger(0);

    private final AtomicReferenceArray<MethodTag> methodTagArr = new AtomicReferenceArray<>(MAX_NUM);


    private static MethodTagMaintainer instance = new MethodTagMaintainer();

    public MethodTagMaintainer() {
    }

    //单利模式
    public static MethodTagMaintainer getInstance() {
        return instance;
    }

    //添加操作
    @Override
    public int addMethodTag(MethodTag methodTag) {
        //标签
        //TODO 注意调用的方法
        //int methodId = index.incrementAndGet();

        int methodId=index.getAndIncrement();
        if (methodId > MAX_NUM) {
            Logger.warn("MethodTagMaintainer.addMethodTag(" + methodTag + "): methodId > MAX_NUM: "
                    + methodId + " > " + MAX_NUM + ", ignored!!!");
            return -1;
        }
        methodTagArr.set(methodId, methodTag);
        return methodId;
    }

    //获取操作
    @Override
    public MethodTag getMethodTag(int methodId) {
        if (methodId > 0 && methodId < MAX_NUM) {
            return methodTagArr.get(methodId);
        }
        return null;
    }

    //计数
    @Override
    public int getMethodTagCount() {
        return index.get();
    }
}
