package monitor.plugin.javassist

import javassist.*
import monitor.plugin.javassist.inject.*
import monitor.plugin.utils.LogUtils

/**
 * Created by ZhouKeWen on 17/3/23.
 */
class JavassistHandler {

    private static ClassPool classPool;
    private static ArrayList<IMethodHandler> methodHandlers;

    private final
    static String[] ACTIVITY_CLASSES = ["android.app.Activity", "android.app.ActivityGroup", "android.support.v7.app.AppCompatActivity", "android.accounts.AccountAuthenticatorActivity"]

    private static void initMethodHandlers() {
        if (!methodHandlers) {
            methodHandlers = new ArrayList<>()
            methodHandlers.add(new OnCreateHandler())
            methodHandlers.add(new OnDestroyHandler())
            methodHandlers.add(new OnLowMemoryHandler())
            methodHandlers.add(new OnTrimMemoryHandler())
        }
    }

    public static CtClass makeClass(String className) {
        if (classPool) {
            return classPool.makeClass(className)
        } else {
            throw IllegalAccessException("classPoll not initialize")
        }
    }

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

    public static byte[] handleClass(InputStream inputStream) {
        CtClass clazz = classPool.makeClass(inputStream)
        if (clazz.isInterface() || clazz.isAnnotation() || clazz.isEnum() || clazz.isArray()) {
            return clazz.toBytecode()
        }

        injectForCpu(clazz)
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
            CpuCodeInject.insertCpuSampleCode(clazz, ctMethod);
        }
        CtConstructor[] ctConstructors = clazz.getConstructors()
        for (CtConstructor ctConstructor : ctConstructors) {
            CpuCodeInject.insertCpuSampleCode(clazz, ctConstructor);
        }
        CtConstructor[] classInitializeres = clazz.getClassInitializer()
        for (CtConstructor classInitializer : classInitializeres) {
            CpuCodeInject.insertCpuSampleCode(clazz, classInitializer)
        }
    }

    private static void injectForMemory(CtClass clazz) {
        CtClass superClazz = clazz.getSuperclass();
        //TODO 类过滤代码在这里添加，注意Activity、Fragment、Service等都要考虑
        if (!ACTIVITY_CLASSES.contains(superClazz.name)) {
            return
        }
        //初始化
        initMethodHandlers()

        CtMethod[] ctMethods = clazz.getDeclaredMethods();
        int successCount = 0
        for (CtMethod ctMethod : ctMethods) {
            LogUtils.printLog("scan memory method:::>>>> ${clazz.name}.${ctMethod.name}")
            int modifiers = ctMethod.getModifiers();
            if (Modifier.isStatic(modifiers) || Modifier.isNative(modifiers)) {
                LogUtils.printLog(
                        "static or native!!!!! method=--========----------- ${clazz.name}.${ctMethod.name}")
                continue
            }
            for (IMethodHandler handler : methodHandlers) {
                if (handler.handleMethod(clazz, ctMethod)) {
                    LogUtils.printLog("inject memory code:::>>>> ${clazz.name}.${ctMethod.name}")
                    successCount++
                }
            }
            if (successCount == methodHandlers.size()) {
                //所有关键方法注入成功，不再处理后续方法
                break
            }
        }

        //如果发现关键method没有实现，则add这个method
        for (IMethodHandler handler : methodHandlers) {
            handler.checkMethodAndAdd(clazz)
        }
    }
}
