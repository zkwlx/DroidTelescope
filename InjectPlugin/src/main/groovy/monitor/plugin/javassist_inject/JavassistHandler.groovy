package monitor.plugin.javassist_inject

import javassist.ClassPool
import javassist.CtBehavior
import javassist.CtClass
import javassist.CtConstructor
import javassist.CtMember
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

        def bytes = handleMemoryCareClass(inputStream)
//        inputStream.close()
//
//        inputStream = new FileInputStream(file);
//        def bytes = handleClass(inputStream);
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
        CtClass clazz = classPool.makeClass(inputStream);
        if (clazz.isInterface() || clazz.isAnnotation() || clazz.isEnum() || clazz.isArray()) {
            return clazz.toBytecode();
        }
        CtMethod[] ctMethods = clazz.getDeclaredMethods();
        for (CtMethod ctMethod : ctMethods) {
            injectMethodSamplerCode(clazz, ctMethod);
        }
        CtConstructor[] ctConstructors = clazz.getConstructors()
        for (CtConstructor ctConstructor : ctConstructors) {
            injectConstructorSamplerCode(clazz, ctConstructor);
        }
        CtConstructor[] classInitializeres = clazz.getClassInitializer()
        for (CtConstructor classInitializer : classInitializeres) {
            injectConstructorSamplerCode(clazz, classInitializer)
        }
        def bytes = clazz.toBytecode()
        //TODO 这里为何还要解冻？
//        clazz.defrost()
        return bytes
    }

    static void injectMethodSamplerCode(CtClass clazz, CtMethod ctMethod) {
        if (ctMethod.isEmpty() || Modifier.isNative(ctMethod.getModifiers())) {
            return;
        }
        insertSamplerCode(clazz, ctMethod)
    }

    static void injectConstructorSamplerCode(CtClass clazz, CtConstructor ctConstructor) {
        if (ctConstructor.isEmpty()) {
            return;
        }
        insertSamplerCode(clazz, ctConstructor)
    }

    static void insertSamplerCode(CtClass clazz, CtBehavior ctBehavior) {
        printLog("inject method:::>>>> ${clazz.name}.${ctBehavior.name}")
        int m = ctBehavior.getModifiers();
        if (java.lang.reflect.Modifier.isStatic(m) || ctBehavior instanceof CtConstructor) {
            printLog("static or constructor!!!!! method=--========----------- ${clazz.name}.${ctBehavior.name}")
            return
        }
        ctBehavior.addLocalVariable("__bl_stn", CtClass.longType);
        ctBehavior.addLocalVariable("__bl_stt", CtClass.longType);
        ctBehavior.addLocalVariable("__bl_icl", CtClass.booleanType);
        ctBehavior.insertBefore(
                """
                  __bl_stn = 0L;
                  __bl_stt = 0L;
                  __bl_icl = andr.perf.monitor.MethodSampler.shouldMonitor();
                  if(__bl_icl) {
                      __bl_stn = java.lang.System.nanoTime();
                      __bl_stt = android.os.SystemClock.currentThreadTimeMillis();
                      andr.perf.monitor.MethodSampler.methodEnter("${clazz.name}", "${
                    ctBehavior.name
                }", "${generateParamTypes(ctBehavior.parameterTypes)}");
                  }
                """)
        //TODO 只在正常return之前插入代码，如果是异常退出，不会回调methodExit
        ctBehavior.insertAfter(
                """
                   if(__bl_icl) {
                       andr.perf.monitor.MethodSampler.methodExit(__bl_stn, __bl_stt, "${
                    clazz.name
                }", "${
                    ctBehavior.name
                }", "${generateParamTypes(ctBehavior.parameterTypes)}");
                   }
                """)
        //TODO 方法异常退出会导致方法堆栈记录混乱，被迫注入finally代码
        ctBehavior.insertAfter(
                """
                   andr.perf.monitor.MethodSampler.methodExitFinally("${
                    clazz.name
                }", "${
                    ctBehavior.name
                }", "${generateParamTypes(ctBehavior.parameterTypes)}");
                """, true)
    }

    static String generateParamTypes(CtClass[] paramTypes) {
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
