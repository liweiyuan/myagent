package com.wade.core;

import com.wade.base.MethodTag;

/**
 * @author :lwy
 * @date 2018/7/31 11:40
 * 方法监听抽象接口
 */
public abstract class AbstractMethodTagMaintainer {

    public abstract int addMethodTag(MethodTag methodTag);

    public abstract MethodTag getMethodTag(int methodId);

    public abstract int getMethodTagCount();
}
