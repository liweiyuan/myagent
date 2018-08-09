package com.wade.asm;

import com.wade.aop.ProfilingClassFilterTransformer;

import java.lang.instrument.Instrumentation;

/**
 * @author :lwy
 * @date 2018/7/27 18:20
 * https://www.ibm.com/developerworks/cn/java/j-lo-jse61/index.html
 *
 * http://www.infoq.com/cn/articles/javaagent-illustrated
 */
public class Permain {

    public static void premain(String option, Instrumentation instrumentation){

        //启动并初始化参数
        if(AsmBootstrap.getBootstrap().initConfig()){
            //启动参数正常
            instrumentation.addTransformer(new ProfilingClassFilterTransformer());
        }
        System.out.println("the premain is execute");
        //instrumentation.addTransformer();
    }
}
/**
 *
 * instrument agent实现了Agent_OnLoad和Agent_OnAttach两方法，也就是说在使用时，agent既可以在启动时加载，也可以在运行时动态加载
 *
 * 其中启动时加载还可以通过类似-javaagent:myagent.jar的方式来间接加载instrument agent，
 * 运行时动态加载依赖的是JVM的attach机制（JVM Attach机制实现），通过发送load命令来加载agent。
 */


/**
 * Class Transform的实现
 * 这里说的class transform其实是狭义的，主要是针对第一次类文件加载时就要求被transform的场景，在加载类文件的时候发出ClassFileLoad事件，
 * 然后交给instrumenat agent来调用javaagent里注册的ClassFileTransformer实现字节码的修改。
 *
 * Class Redefine的实现
 * 类重新定义，这是Instrumentation提供的基础功能之一，主要用在已经被加载过的类上，想对其进行修改，要做这件事，我们必须要知道两个东西，一个是要修改哪个类，
 * 另外一个是想将那个类修改成怎样的结构，有了这两个信息之后就可以通过InstrumentationImpl下面的redefineClasses方法操作了：
 */
