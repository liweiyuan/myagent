package com.wade.aop;

import com.wade.asm.ASMRecorderMaintainer;
import com.wade.base.MethodTag;
import com.wade.core.AbstractRecorderMaintainer;
import com.wade.core.MethodTagMaintainer;
import com.wade.core.config.ProfilingConfig;
import com.wade.threadlocal.RequestIdThreadLocal;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.AdviceAdapter;

import java.util.UUID;

/**
 * @author :lwy
 * @date 2018/7/30 19:27
 */
public class ProfilingMethodFilterVisitor extends AdviceAdapter {


    //TODO ProfilingAspect重点关注
    private static final String METHOD_EXIT_EXECUTE_NAME = Type.getInternalName(MethodExitAspectMaintainer.class);

    //TODO 方法监视器
    private static final MethodTagMaintainer methodTagMaintainer = MethodTagMaintainer.getInstance();

    //RecorderMaintainer记录初始化
    private AbstractRecorderMaintainer maintainer = ASMRecorderMaintainer.getInstance();

    private ProfilingConfig profilingConfig = ProfilingConfig.getInstance();

    //方法id
    private int methodTagId;

    private String innerClassName;

    private String methodName;

    //开始记录时间
    private int startTimeIdentifier;

    public ProfilingMethodFilterVisitor(int access, String methodName, String descriptor, MethodVisitor mv, String innerClassName) {
        super(ASM5, mv, access, methodName, descriptor);
        this.methodName = methodName;

        //这一步返回一个数组下标，也就是methodId,自增
        this.methodTagId = methodTagMaintainer.addMethodTag(getMethodTag(innerClassName, methodName));
        this.innerClassName = innerClassName;
    }


    //获取方法参数
    private MethodTag getMethodTag(String innerClassName, String methodName) {

        //包名+类名
        int index = innerClassName.replace(".", "/").lastIndexOf("/");

        String className = innerClassName.substring(index + 1, innerClassName.length());
        return MethodTag.newInstance(className, methodName);
    }


    //进入方法钱
    @Override
    protected void onMethodEnter() {
        //进入方法判断
        if (methodTagId >= 0) {
            // TODO 初始化AccurateRecoder


            maintainer.addRecorder(methodTagId, profilingConfig.getProfilingParam(innerClassName + "/" + methodName));

            mv.visitMethodInsn(INVOKESTATIC, "java/lang/System", "nanoTime", "()J", false);
            startTimeIdentifier = newLocal(Type.LONG_TYPE);
            mv.visitVarInsn(LSTORE, startTimeIdentifier);
        }
    }


    //退出方法前
    @Override
    protected void onMethodExit(int opcode) {
        if (methodTagId > 0 && ((IRETURN <= opcode && opcode <= RETURN) || opcode == ATHROW)) {
            mv.visitVarInsn(LLOAD, startTimeIdentifier);
            mv.visitLdcInsn(methodTagId);
            mv.visitMethodInsn(INVOKESTATIC, METHOD_EXIT_EXECUTE_NAME, "methodExit", "(JI)V", false);
        }
    }
}
