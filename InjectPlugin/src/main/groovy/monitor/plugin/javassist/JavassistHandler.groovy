package monitor.plugin.javassist

import javassist.*
import monitor.plugin.javassist.DirClassPath
import monitor.plugin.javassist.JarClassPath
import monitor.plugin.javassist.inject.CpuCodeInject
import monitor.plugin.javassist.inject.MemoryCodeInject
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
               MemoryCodeInject.insertCreateSampleCode(clazz, ctMethod)
                hasCreateMethod = true
            } else if ("onDestroy" == ctMethod.name && ctMethod.parameterTypes.size() == 0) {
                MemoryCodeInject.insertDestroySampleCode(clazz, ctMethod)
                hasDestroyMethod = true
            }
            if (hasCreateMethod && hasDestroyMethod) {
                break
            }
        }
        //TODO 子类没有onCreate或onDestroy方法
        if (!hasCreateMethod) {
            printLog("没有create方法！！！！！>>>>>> ${clazz.name}")
            MemoryCodeInject.addObjectCreateMethod(clazz)
        }
        if (!hasDestroyMethod) {
            printLog("没有destroy方法？？？？>>>>>> ${clazz.name}")
            MemoryCodeInject.addObjectDestroyMethod(clazz)
        }
    }

    private static void printLog(String content) {
        System.out.println(content)
    }

}
