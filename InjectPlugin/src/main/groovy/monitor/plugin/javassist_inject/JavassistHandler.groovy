package monitor.plugin.javassist_inject

import javassist.ClassPool
import javassist.CtBehavior
import javassist.CtClass
import javassist.CtConstructor
import javassist.CtMethod
import javassist.CtNewMethod
import javassist.Modifier

/**
 * Created by ZhouKeWen on 17/3/23.
 */
class JavassistHandler {

    private static ClassPool classPool;

    static void setClassPath(Set<File> files) {
        classPool = new ClassPool(true);
        //TODO 实验选项，能节省编译时的内存消耗
        ClassPool.doPruning = true;
        for (File file : files) {
            if (file.exists()) {
                if (file.isDirectory()) {
                    classPool.appendClassPath(new DirClassPath(file.absolutePath));
                } else {
                    classPool.appendClassPath(new JarClassPath(file.absolutePath));
                }
            }
        }
    }

    public static byte[] handleClass(File file) {
        def optClass = new File(file.getParent(), file.name + ".opt")

        FileInputStream inputStream = new FileInputStream(file);
        FileOutputStream outputStream = new FileOutputStream(optClass)
        def bytes = handleClass(inputStream);
        outputStream.write(bytes)
        inputStream.close()
        outputStream.close()
        if (file.exists()) {
            file.delete()
        }
        optClass.renameTo(file)
        return bytes
    }

    public static byte[] handleMemoryCareClass(InputStream inputStream) {
        CtClass clazz = classPool.makeClass(inputStream);
        CtClass superClazz = clazz.getSuperclass();
        if ("android.support.v7.app.AppCompatActivity" == superClazz.name) {
            CtMethod[] ctMethods = clazz.getDeclaredMethods();
            boolean hasOnCreateMethod = false
            for (CtMethod ctMethod : ctMethods) {
                CtClass bundleClazz = classPool.makeClass("android.os.Bundle")
                if ("onCreate" == ctMethod.name && ctMethod.parameterTypes.size() == 1 && ctMethod.parameterTypes[0] ==
                        bundleClazz) {
                    printLog(">>>>>>>>>>>>>>>>>>>>>>>>isonCreate!~!!!!!!!>>> ${clazz.name}.${ctMethod.name}")
                    hasOnCreateMethod = true
                    break
                }
            }
            if (!hasOnCreateMethod) {
                printLog("don't have onCreate#########################")
                CtMethod m = CtNewMethod.make(
                        "protected void onCreate(android.os.Bundle s) {super.onCreate(s);andr.perf.monitor.MethodSampler.shouldMonitor(\$0);}",
                        clazz)
                clazz.addMethod(m)
            }
        }
        return clazz.toBytecode()
    }

    public static byte[] handleClass(InputStream inputStream) {
        CtClass clazz = classPool.makeClass(inputStream)
        if (clazz.isInterface() || clazz.isAnnotation() || clazz.isEnum() || clazz.isArray()) {
            return clazz.toBytecode()
        }

        injectForCpu(clazz)
        //TODO 同一个方法是否可以修改两次？
        //TODO 判断是否开启泄露监控功能
        injectForMemory(clazz)

        def bytes = clazz.toBytecode()
        //TODO 这里为何还要解冻？
//        clazz.defrost()
        return bytes
    }

    static void injectForCpu(CtClass clazz) {
        CtMethod[] ctMethods = clazz.getDeclaredMethods();
        for (CtMethod ctMethod : ctMethods) {
            insertCpuSampleCode(clazz, ctMethod);
        }
        CtConstructor[] ctConstructors = clazz.getConstructors()
        for (CtConstructor ctConstructor : ctConstructors) {
            insertCpuSampleCode(clazz, ctConstructor);
        }
        CtConstructor[] classInitializeres = clazz.getClassInitializer()
        for (CtConstructor classInitializer : classInitializeres) {
            insertCpuSampleCode(clazz, classInitializer)
        }
    }

    private static void injectForMemory(CtClass clazz) {
        CtClass superClazz = clazz.getSuperclass();
        //TODO 类过滤代码在这里添加
        if ("android.support.v7.app.AppCompatActivity" != superClazz.name) {
            return
        }
        boolean hasCreateMethod = false
        boolean hasDestroyMethod = false
        CtMethod[] ctMethods = clazz.getDeclaredMethods();
        CtClass bundleClazz = classPool.makeClass("android.os.Bundle")
        for (CtMethod ctMethod : ctMethods) {
            int modifiers = ctMethod.getModifiers();
            if (Modifier.isStatic(modifiers) || Modifier.isNative(modifiers)) {
                printLog("static or native!!!!! method=--========----------- ${clazz.name}.${ctMethod.name}")
                continue
            }
            //TODO 方法匹配代码写好点，各种情况都要考虑，例如不同Fragment和不同Activity
            if ("onCreate" == ctMethod.name && ctMethod.parameterTypes.size() == 1 && ctMethod.parameterTypes[0] ==
                    bundleClazz) {
                insertMemorySampleCode(clazz, ctMethod, true)
                hasCreateMethod = true
            } else if ("onDestroy" == ctMethod.name && ctMethod.parameterTypes.size() == 0) {
                insertMemorySampleCode(clazz, ctMethod, false)
                hasDestroyMethod = true
            }
            if (hasCreateMethod && hasDestroyMethod) {
                break
            }
        }
        //TODO 子类没有onCreate或onDestroy方法，注意Activity和Fragment的方法参数都不一样
        if (!hasCreateMethod) {
            printLog("没有create方法！！！！！>>>>>> ${clazz.name}")
            addObjectCreateMethodCode(clazz)
        }
        if (!hasDestroyMethod) {
            printLog("没有destroy方法？？？？>>>>>> ${clazz.name}")
            addObjectDestroyMethodCode(clazz)
        }
    }

    private static void addObjectCreateMethodCode(CtClass clazz) {
        CtMethod m = CtNewMethod.make(
                """protected void onCreate(android.os.Bundle s) {
                       super.onCreate(s);
                       andr.perf.monitor.injected.ObjectLeakSample.objectCreate(\$0);
                   }
                """,
                clazz)
        clazz.addMethod(m)
    }

    private static void addObjectDestroyMethodCode(CtClass clazz) {
        CtMethod m = CtNewMethod.make(
                """protected void onDestroy() {
                       super.onDestroy();
                       andr.perf.monitor.injected.ObjectLeakSample.objectDestroy(\$0);
                   }
                """,
                clazz)
        clazz.addMethod(m)
    }

    private static void insertMemorySampleCode(CtClass clazz, CtMethod ctMethod, boolean isCreate) {

        printLog("insertMemorySampleCode method:::>>>> ${clazz.name}.${ctMethod.name}, isCreate:${isCreate}")

        ctMethod.addLocalVariable("__memory_switch", CtClass.booleanType);
        //TODO 这些shouldMonitor判断是否可以放到类的属性里，每个方法调用一次不太好
        if (isCreate) {
            ctMethod.insertBefore("""
                  __memory_switch = andr.perf.monitor.injected.ObjectLeakSample.shouldMonitor();
                  if(__memory_switch) {
                      andr.perf.monitor.injected.ObjectLeakSample.objectCreate(\$0);
                  }
                """)
        } else {
            ctMethod.insertBefore("""
                  __memory_switch = andr.perf.monitor.injected.ObjectLeakSample.shouldMonitor();
                  if(__memory_switch) {
                      andr.perf.monitor.injected.ObjectLeakSample.objectDestroy(\$0);
                  }
                """)
        }

    }

    private static void insertCpuSampleCode(CtClass clazz, CtBehavior ctBehavior) {
        printLog("inject method:::>>>> ${clazz.name}.${ctBehavior.name}")
        if (ctBehavior.isEmpty() || Modifier.isNative(ctBehavior.getModifiers())) {
            return;
        }

        ctBehavior.addLocalVariable("__cpu_use_ns", CtClass.longType);
        ctBehavior.addLocalVariable("__cpu_use_thread_ms", CtClass.longType);
        ctBehavior.addLocalVariable("__cpu_switch", CtClass.booleanType);
        ctBehavior.insertBefore(
                """
                  __cpu_use_ns = 0L;
                  __cpu_use_thread_ms = 0L;
                  __cpu_switch = andr.perf.monitor.injected.TimeConsumingSample.shouldMonitor();
                  if(__cpu_switch) {
                      __cpu_use_ns = java.lang.System.nanoTime();
                      __cpu_use_thread_ms = android.os.SystemClock.currentThreadTimeMillis();
                      andr.perf.monitor.injected.TimeConsumingSample.methodEnter("${clazz.name}", "${
                    ctBehavior.name
                }", "${generateParamTypes(ctBehavior.parameterTypes)}");
                  }
                """)
        //TODO 只在正常return之前插入代码，如果是异常退出，不会回调methodExit
        ctBehavior.insertAfter(
                """
                   if(__cpu_switch) {
                       andr.perf.monitor.injected.TimeConsumingSample.methodExit(__cpu_use_ns, __cpu_use_thread_ms, "${
                    clazz.name
                }", "${
                    ctBehavior.name
                }", "${generateParamTypes(ctBehavior.parameterTypes)}");
                   }
                """)
        //TODO 方法异常退出会导致方法堆栈记录混乱，被迫注入finally代码
        ctBehavior.insertAfter(
                """
                   andr.perf.monitor.injected.TimeConsumingSample.methodExitFinally("${
                    clazz.name
                }", "${
                    ctBehavior.name
                }", "${generateParamTypes(ctBehavior.parameterTypes)}");
                """, true)
    }

    private static String generateParamTypes(CtClass[] paramTypes) {
        StringBuilder argTypesBuilder = new StringBuilder();
        if (paramTypes.length > 0) {
            CtClass firstParam = paramTypes[0]
            argTypesBuilder.append(firstParam.name);
            for (int i = 1; i < paramTypes.length; i++) {
                argTypesBuilder.append(",")
                CtClass paramCls = paramTypes[i]
                argTypesBuilder.append(paramCls.name)
            }
        }
        return argTypesBuilder.toString()
    }


    private static void printLog(String content) {
        System.out.println(content)
    }

}
