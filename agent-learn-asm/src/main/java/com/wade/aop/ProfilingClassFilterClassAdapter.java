package com.wade.aop;

import com.wade.core.config.ProfilingConfig;
import com.wade.core.config.ProfilingFilter;
import com.wade.core.util.Logger;
import org.objectweb.asm.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.objectweb.asm.Opcodes.ASM5;

/**
 * @author :lwy
 * @date 2018/7/30 17:48
 */
public class ProfilingClassFilterClassAdapter extends ClassVisitor implements Opcodes {

    private String innerClassName;

    private boolean isInterface;

    private List<String> fieldNameList = new ArrayList<>();

    public ProfilingClassFilterClassAdapter(ClassVisitor cw, String className) {
        super(ASM5, cw);
        this.innerClassName = className;
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        Logger.debug("ProfilingClassFilterClassAdapter.visit(" + version + ", " + access + ", " + name + ", " + signature + ", " + superName + ", " + Arrays.toString(interfaces) + ")");

        super.visit(version, access, name, signature, superName, interfaces);
        this.isInterface = (access & ACC_INTERFACE) != 0;
    }


    @Override
    public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
        String upFieldName = name.substring(0, 1).toUpperCase() + name.substring(1, name.length());
        fieldNameList.add("get" + upFieldName);
        fieldNameList.add("set" + upFieldName);
        fieldNameList.add("is" + upFieldName);

        return super.visitField(access, name, descriptor, signature, value);
    }


    @Override
    public MethodVisitor visitMethod(int access, String nameName, String descriptor, String signature, String[] exceptions) {


        Logger.debug("ProfilingClassAdapter.visitMethod(" + access + ", " + nameName + ", "
                + descriptor + ", " + signature + ", " + Arrays.toString(exceptions) + ")");

        if (isInterface || !isNeedVisit(access, nameName)) {
            return super.visitMethod(access, nameName, descriptor, signature, exceptions);
        }

        MethodVisitor mv = cv.visitMethod(access, nameName, descriptor, signature, exceptions);

        if (mv == null) {
            return null;
        }

        return new ProfilingMethodFilterVisitor(access, nameName, descriptor, mv, innerClassName);
        //TODO 主要关注的方法
        //return super.visitMethod(access, name, descriptor, signature, exceptions);
    }

    private boolean isNeedVisit(int access, String nameName) {

        //私有方法不改写字节码  TODO 不需要注入的方法
        if ((access & ACC_PRIVATE) != 0 && ProfilingConfig.getInstance().isExcludePrivateMethod()) {
            return false;
        }


        //不对抽象方法和native方法进行注入
        if ((access & ACC_ABSTRACT) != 0 || (access & ACC_NATIVE) != 0) {
            return false;
        }

        //构造器方法不进行注入
        if ("<init>".equals(nameName) || "<clinit>".equals(nameName)) {
            return false;
        }

        //不需要注入的方法
        if (fieldNameList.contains(nameName) || ProfilingFilter.isNotNeedInjectMethod(nameName)) {
            return false;
        }

        return true;
    }
}
