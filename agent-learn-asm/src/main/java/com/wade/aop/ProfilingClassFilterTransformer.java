package com.wade.aop;

import com.wade.core.config.ProfilingFilter;
import com.wade.core.util.Logger;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

/**
 * @author :lwy
 * @date 2018/7/30 17:26
 */
public class ProfilingClassFilterTransformer implements ClassFileTransformer {


    //完成类的转换
    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
                            ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {

        if (className == null) {
            return null;
        }

        try {
            //排除的
            if (ProfilingFilter.isNotNeedInjectExcludeClassName(className)) {

                //TODO 测试
                if (className.startsWith("suishen")) {
                    System.out.println("exclude:" + className);
                }
                return classfileBuffer;
            }

            //包含的
            if (!ProfilingFilter.isNeedInjectIncludeClassName(className)) {

                //TODO 测试
                if (className.startsWith("suishen")) {
                    System.out.println("include:" + className);
                }
                return classfileBuffer;
            }
            if (loader != null && ProfilingFilter.isNotNeedInjectClassLoader(loader.getClass().getName())) {
                return classfileBuffer;
            }
            Logger.info("ProfilingTransformer.transform(" + loader + ", " + className + ", classBeingRedefined, protectionDomain, "
                    + classfileBuffer.length + ")...");
            return getBytes(loader, className, classfileBuffer);
        } catch (Throwable e) {
            Logger.error("ProfilingTransformer.transform(" + loader + ", " + className + ", " + classBeingRedefined + ", "
                    + protectionDomain + ", " + classfileBuffer.length + ")", e);
        }
        return classfileBuffer;
    }

    //做转换
    private byte[] getBytes(ClassLoader loader, String className, byte[] classfileBuffer) {

        //asm
        if (loader != null && loader.getClass().getName().equals("org.apache.catalina.loader.WebappClassLoader")) {
            ClassReader cr = new ClassReader(classfileBuffer);
            ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_MAXS);
            ClassVisitor cv = new ProfilingClassFilterClassAdapter(cw, className);
            cr.accept(cv, ClassReader.EXPAND_FRAMES);
            return cw.toByteArray();
        } else {
            ClassReader cr = new ClassReader(classfileBuffer);
            ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_FRAMES);
            ClassVisitor cv = new ProfilingClassFilterClassAdapter(cw, className);
            cr.accept(cv, ClassReader.EXPAND_FRAMES);
            return cw.toByteArray();
        }
    }
}
