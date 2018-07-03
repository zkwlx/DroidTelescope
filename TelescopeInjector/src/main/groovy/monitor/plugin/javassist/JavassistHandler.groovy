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

    private static ClassPool classPool
    private static final ArrayList<IMethodHandler> methodHandlers
    private static final ArrayList<IMethodHandler> v4MethodHandlers

    static {
        methodHandlers = new ArrayList<>()
        methodHandlers.add(new OnCreateHandler())
        methodHandlers.add(new OnDestroyHandler())
        methodHandlers.add(new OnTrimMemoryHandler())

        v4MethodHandlers = new ArrayList<>()
        v4MethodHandlers.add(new OnCreateHandler())
        v4MethodHandlers.add(new OnDestroyHandler())
        v4MethodHandlers.add(new OnLowMemoryHandler())
    }

    private static ArrayList<IMethodHandler> getMethodHandlers() {
        return methodHandlers
    }

    private static ArrayList<IMethodHandler> getV4MethodHandlers() {
        return v4MethodHandlers
    }

    static void addClassPath(List<File> files) {
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
        //TODO 当发生修改冻结class时，说明有重复类
//        clazz.defrost()
        return bytes
    }

    static void injectForCpu(CtClass clazz) {
        long time = System.currentTimeMillis()
        CtMethod[] ctMethods = clazz.getDeclaredMethods()
        for (CtMethod ctMethod : ctMethods) {
            CpuCodeInject.insertCpuSampleCode(clazz, ctMethod)
        }
        CtConstructor[] ctConstructors = clazz.getConstructors()
        for (CtConstructor ctConstructor : ctConstructors) {
            CpuCodeInject.insertCpuSampleCode(clazz, ctConstructor)
        }
        CtConstructor[] classInitializeres = clazz.getClassInitializer()
        for (CtConstructor classInitializer : classInitializeres) {
            CpuCodeInject.insertCpuSampleCode(clazz, classInitializer)
        }
        time = System.currentTimeMillis() - time
        Logger.i("Inject cpu sample code in ${time}ms, class: ${clazz.name}")
    }

    private static void injectForMemory(CtClass clazz) {
        if (MemoryCodeInject.classNotCare(clazz)) {
            //不是内存监控模块关心的类，不注入
            return
        }
        long time = System.currentTimeMillis()
        List<IMethodHandler> methodHandlers
        if (MemoryCodeInject.isV4OrV7Class(clazz)) {
            methodHandlers = getV4MethodHandlers().clone()
        } else {
            methodHandlers = getMethodHandlers().clone()
        }

        CtMethod[] ctMethods = clazz.getDeclaredMethods()
        //遍历这个类的所有方法
        for (CtMethod ctMethod : ctMethods) {
            Logger.d("scan memory method:::>>>> ${clazz.name}.${ctMethod.name}")
            int modifiers = ctMethod.getModifiers()
            if (Modifier.isStatic(modifiers) || Modifier.isNative(modifiers)) {
                Logger.d(
                        "static or native!!!Don't inject>> ${clazz.name}.${ctMethod.name}")
                continue
            }
            //对该方法遍历调用 modifyMethod
            Iterator<IMethodHandler> iterator = methodHandlers.iterator()
            while (iterator.hasNext()) {
                IMethodHandler handler = iterator.next()
                if (handler.modifyMethod(clazz, ctMethod)) {
                    Logger.d("inject memory code:::>>>> ${clazz.name}.${ctMethod.name}")
                    iterator.remove()
                    break
                }
            }
            if (methodHandlers.isEmpty()) {
                //所有关键方法注入成功，不再处理后续方法
                break
            }
        }

        //剩下的 handlers 说明对应的关键 method 没有实现，则add这个method
        for (IMethodHandler handler : methodHandlers) {
            handler.addMethod(clazz)
        }
        time = System.currentTimeMillis() - time
        Logger.i("Inject memory sample code duration: ${time}, class: ${clazz.name}")
    }

    private static void injectForInteractive(CtClass clazz) {
        InteractiveCodeInject.injectForViewEvent(clazz)
    }

    /**
     *  View 生命周期，测试功能
     * @param clazz
     */
    private static void injectForView(CtClass clazz) {
        if (ViewInject.isNotView(clazz)) {
            return
        }
        ViewInject.injectForView(clazz)
    }

    private static boolean isMonitorSubclass(CtClass clazz) {
        //TODO 暂时只判断一层超类，注意！！！
//        if (clazz.superclass.packageName.startsWith("andr.perf.monitor")) {
//        }
        //TODO 暂时只判断 Config 的子类，注意！！
        if (clazz.superclass.name == "andr.perf.monitor.Config") {
            Logger.d("Found Config subclass:>>>" + clazz.name)
            return true
        } else {
            return false
        }
    }

}
