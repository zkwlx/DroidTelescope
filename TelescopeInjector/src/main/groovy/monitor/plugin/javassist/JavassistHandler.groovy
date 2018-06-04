package monitor.plugin.javassist

import javassist.*
import monitor.plugin.ConfigProvider
import monitor.plugin.config.InjectConfig
import monitor.plugin.javassist.inject.*
import monitor.plugin.javassist.inject.interactive.InteractiveCodeInject
import monitor.plugin.utils.Logger

/**
 * Created by ZhouKeWen on 17/3/23.
 */
class JavassistHandler {

    private static ClassPool classPool;
    private static ArrayList<IMethodHandler> methodHandlers;
    private static ArrayList<IMethodHandler> v4MethodHandlers;

    private static ArrayList<IMethodHandler> getMethodHandlers() {
        if (!methodHandlers) {
            methodHandlers = new ArrayList<>()
            methodHandlers.add(new OnCreateHandler())
            methodHandlers.add(new OnDestroyHandler())
            methodHandlers.add(new OnTrimMemoryHandler())
        }
        return methodHandlers
    }

    private static ArrayList<IMethodHandler> getV4MethodHandlers() {
        if (!v4MethodHandlers) {
            v4MethodHandlers = new ArrayList<>()
            v4MethodHandlers.add(new OnCreateHandler())
            v4MethodHandlers.add(new OnDestroyHandler())
            v4MethodHandlers.add(new OnLowMemoryHandler())
        }
        return v4MethodHandlers
    }

    static void setClassPath(List<File> files) {
        classPool = new ClassPool(true)
        //节省编译时的内存消耗
        ClassPool.doPruning = true
        for (File file : files) {
            if (file.exists()) {
                if (file.isDirectory()) {
                    classPool.appendClassPath(new DirClassPath(file.absolutePath))
                } else {
                    classPool.appendClassPath(new JarClassPath(file.absolutePath))
                }
            }
        }
    }

    static byte[] handleClass(File file) {
        def optClass = new File(file.getParent(), file.name + ".opt")

        FileInputStream inputStream = new FileInputStream(file);
        FileOutputStream outputStream = new FileOutputStream(optClass)
        def bytes = handleClass(file.name, inputStream);
        outputStream.write(bytes)
        inputStream.close()
        outputStream.close()
        if (file.exists()) {
            file.delete()
        }
        optClass.renameTo(file)
        return bytes
    }

    static byte[] handleClass(String className, InputStream inputStream) {
        CtClass clazz
        try {
            clazz = classPool.makeClass(inputStream)
        } catch (RuntimeException e) {
            e.printStackTrace()
            Logger.i("class frozen: :::::::>>> " + className + ", ignore that!")
            //TODO 重复打开已冻结类，说明有重复类，跳过修改
            return inputStream.getBytes()
        }

        if (clazz.isInterface() || clazz.isAnnotation() || clazz.isEnum() || clazz.isArray() || isMonitorSubclass(clazz)) {
            //跳过
            return clazz.toBytecode()
        }

        //TODO 通过开关控制开启哪些监控模块的注入
        InjectConfig config = ConfigProvider.config
        if (config.memoryLeakEnable) {
            injectForMemory(clazz)
        }
        if (config.cpuTimeEnable) {
            injectForCpu(clazz)
        }
        if (config.interactiveEnable) {
            injectForInteractive(clazz)
        }

        def bytes = clazz.toBytecode()
        //TODO javasisst不允许对一个class做两次writeFile()、toClass()、toBytecode()操作
        //TODO 以上操作会冻结class，可以强行解冻，但是这种方式不太好
        //TODO 当发生修改冻结class时，说明同包名同类名的类有两个
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
        if (MemoryCodeInject.classNotCare(clazz)) {
            //不是内存监控模块关心的类，不注入
            return
        }
        List<IMethodHandler> methodHandlers;
        if (MemoryCodeInject.isV4OrV7Class(clazz)) {
            methodHandlers = getV4MethodHandlers()
        } else {
            methodHandlers = getMethodHandlers()
        }

        CtMethod[] ctMethods = clazz.getDeclaredMethods();
        int successCount = 0
        for (CtMethod ctMethod : ctMethods) {
            Logger.i("scan memory method:::>>>> ${clazz.name}.${ctMethod.name}")
            int modifiers = ctMethod.getModifiers();
            if (Modifier.isStatic(modifiers) || Modifier.isNative(modifiers)) {
                Logger.i(
                        "static or native!!!Don't inject>> ${clazz.name}.${ctMethod.name}")
                continue
            }
            for (IMethodHandler handler : methodHandlers) {
                if (handler.handleMethod(clazz, ctMethod)) {
                    //TODO 这里对一个方法处理成功，是否退出循环，开始下一个方法？
                    Logger.i("inject memory code:::>>>> ${clazz.name}.${ctMethod.name}")
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

    private static void injectForInteractive(CtClass clazz) {
        InteractiveCodeInject.injectForViewEvent(clazz)
    }

    private static boolean isMonitorSubclass(CtClass clazz) {
        //TODO 暂时只判断一层超类，注意！！！
//        if (clazz.superclass.packageName.startsWith("andr.perf.monitor")) {
//        }
        //TODO 暂时只判断 Config 的子类，注意！！
        if (clazz.superclass.name == "andr.perf.monitor.Config") {
            Logger.i("Found Config subclass:>>>" + clazz.name)
            return true
        } else {
            return false
        }
    }

}
