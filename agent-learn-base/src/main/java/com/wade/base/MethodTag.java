package com.wade.base;

/**
 * @author :lwy
 * @date 2018/7/31 11:41
 * 采集方法指标
 */
public class MethodTag {
    private String className;

    private String methodName;

    public MethodTag(String className, String methodName) {
        this.className = className;
        this.methodName = methodName;
    }

    public MethodTag() {
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getSimpleDesc() {
        return className + "." + methodName;
    }

    @Override
    public String toString() {
        return "MethodTag{" +
                "methodName='" + methodName + '\'' +
                ", className='" + className + '\'' +
                '}';
    }

    public static MethodTag newInstance(String className, String methodName){
        return new MethodTag(className,methodName);
    }

}
