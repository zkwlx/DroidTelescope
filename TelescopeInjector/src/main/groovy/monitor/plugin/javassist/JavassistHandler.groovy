package monitor.plugin.javassist

import javassist.*
import monitor.plugin.ConfigProvider
import monitor.plugin.config.InjectConfig
import monitor.plugin.javassist.inject.*
import monitor.plugin.javassist.inject.interactive.InteractiveCodeInject
import monitor.plugin.javassist.inject.reference_leak.IMethodHandler
import monitor.plugin.javassist.inject.reference_leak.ReferenceLeakCodeInject
import monitor.plugin.javassist.inject.reference_leak.OnCreateHandler
import monitor.plugin.javassist.inject.reference_leak.OnDestroyHandler
import monitor.plugin.javassist.inject.reference_leak.OnLowMemoryHandler
import monitor.plugin.javassist.inject.reference_leak.OnTrimMemoryHandler
import monitor.plugin.utils.JavassistUtils
import monitor.plugin.utils.Logger

/**
 * Created by ZhouKeWen on 17/3/23.
 */
class JavassistHandler {

    private static ClassPool classPool

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

        if (clazz.isInterface()
                || clazz.isAnnotation()
                || clazz.isEnum()
                || clazz.isArray()
                || isMonitorSubclass(clazz)) {
            //跳过
            return clazz.toBytecode()
        }

        //TODO 通过开关控制开启哪些监控模块的注入
        InjectConfig config = ConfigProvider.config
        if (config.memoryLeakEnable) {
            injectForReferenceLeak(clazz)
        }
        if (config.cpuTimeEnable) {
            injectForCpu(clazz)
        }
        if (config.interactiveEnable && ConfigProvider.config.forRelease) {
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

    private static void injectForReferenceLeak(CtClass clazz) {
        ReferenceLeakCodeInject.injectForReferenceLeak(clazz)
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
        //TODO 暂时只判断一层超类
//        if (clazz.superclass.packageName.startsWith("andr.perf.monitor")) {
//        }
        //TODO 暂时只判断 Config 的子类
        CtClass superClazz = JavassistUtils.getSuperclass(clazz)
        if (superClazz == null || superClazz == CtClass.voidType) {
            //父类为 Object 或父类抛出 NotFoundException
            return false
        } else if (superClazz.name == "andr.perf.monitor.Config") {
            Logger.d("Found Config subclass:>>>" + clazz.name)
            return true
        } else {
            return false
        }
    }

}
