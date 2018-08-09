package com.wade.core.config;

import java.util.HashSet;
import java.util.Set;

/**
 * @author :lwy
 * @date 2018/7/30 13:06
 */
public class ProfilingFilter {


    /**
     * 需要注入的Package集合
     */
    private static Set<String> includePackage = new HashSet<>();


    /**
     * 不需要注入的Package集合
     */
    private static Set<String> excludePackage = new HashSet<>();

    /**
     * 不注入的ClassLoader集合
     */
    private static Set<String> excludeClassLoader = new HashSet<>();


    /**
     * 不注入的方法集合
     */
    private static Set<String> excludeMethods = new HashSet<>();

    static {
        // 默认不注入的package
        excludePackage.add("java/");
        excludePackage.add("javax/");
        excludePackage.add("sun/");
        excludePackage.add("com/sun/");
        excludePackage.add("org/");
        excludePackage.add("com/intellij/");

        // 不注入MyAgent本身
        excludePackage.add("com/wade");


        //默认不注入的method
        excludeMethods.add("main");
        excludeMethods.add("getClass");//java.lang.Object
        excludeMethods.add("hashCode");//java.lang.Object
        excludeMethods.add("equals");//java.lang.Object
        excludeMethods.add("clone");//java.lang.Object
        excludeMethods.add("toString");//java.lang.Object
        excludeMethods.add("notify");//java.lang.Object
        excludeMethods.add("wait");//java.lang.Object
        excludeMethods.add("finalize");//java.lang.Object
        excludeMethods.add("afterPropertiesSet");//spring
    }


    public static void addIncludePackages(String pkg) {
        if (pkg == null) {
            return;
        }
        if ("".equals(pkg.trim())) {
            return;
        }
        includePackage.add(pkg.replace('.', '/').trim());
    }

    public static void addExcludePackages(String pkg) {
        if (pkg == null) {
            return;
        }
        if ("".equals(pkg.trim())) {
            return;
        }
        excludePackage.add(pkg.replace('.', '/').trim());
    }

    public static void addExcludeClassLoader(String classLoader) {
        if (classLoader == null) {
            return;
        }
        if ("".equals(classLoader.trim())) {
            return;
        }
        excludeClassLoader.add(classLoader);
    }

    /**
     * 增加方法的排除
     */
    public static void addExculdMethods(String methodName) {
        if (methodName == null) {
            return;
        }
        if ("".equals(methodName.trim())) {
            return;
        }
        excludeMethods.add(methodName);
    }

    /**
     * @param className : 形如: cn/MyAgent/core/ProfilingFilter
     * @return : true->不需要修改字节码  false->需要修改字节码
     */
    public static boolean isNotNeedInjectExcludeClassName(String className) {
        if (className == null) {
            return false;
        }
        //TODO 不理解 增强类 比如suishen/wade/agent/test/springbootagent/SpringBootAgentApplication$$EnhancerBySpringCGLIB$$83c07896
        if (className.indexOf('$') >= 0) {
            return true;
        }

        for (String prefix : excludePackage) {
            if (className.startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }

    /**
     * includeClassName中的class
     *
     * @param className
     * @return
     */
    public static boolean isNeedInjectIncludeClassName(String className) {
        if (className == null) {
            return false;
        }

        for (String prefix : includePackage) {
            if (className.startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 是否是不需要注入的类加载器
     *
     * @param classLoader
     * @return : true->需要修改字节码  false->不需要修改字节码
     */
    public static boolean isNotNeedInjectClassLoader(String classLoader) {
        return excludeClassLoader.contains(classLoader);
    }

    /**
     * 不需要注入的方法
     * @param methodName
     * @return
     */
    public static boolean isNotNeedInjectMethod(String methodName) {
        return excludeMethods.contains(methodName);
    }
}
