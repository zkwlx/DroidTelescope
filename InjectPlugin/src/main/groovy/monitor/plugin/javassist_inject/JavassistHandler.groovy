package monitor.plugin.javassist_inject

import javassist.ClassPool
import javassist.CtBehavior
import javassist.CtClass
import javassist.CtConstructor
import javassist.CtMethod
import javassist.Modifier

/**
 * Created by ZhouKeWen on 17/3/23.
 */
class JavassistHandler {

    private static ClassPool classPool;

    static void setClassPath(Set<File> files) {
        classPool = new ClassPool(true);
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

        def bytes = injectMonitorCode(inputStream);
        outputStream.write(bytes)
        inputStream.close()
        outputStream.close()
        if (file.exists()) {
            file.delete()
        }
        optClass.renameTo(file)
        return bytes
    }

    static byte[] injectMonitorCode(InputStream inputStream) {
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
        clazz.defrost()
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
        printLog("inecjt method:::>>>> ${clazz.name}.${ctBehavior.name}")
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
                  }
                """)
        ctBehavior.insertAfter(
                """
                   if(__bl_icl) {
                       andr.perf.monitor.MethodSampler.methodExit(__bl_stn, __bl_stt, "${clazz.name}", "${
                    ctBehavior.name
                }", "${generateParamTypes(ctBehavior.parameterTypes)}");
                   }
                """)
    }

    static String generateParamTypes(CtClass[] paramTypes) {
        StringBuilder argTypesBuilder = new StringBuilder();
        for (int i = 0; i < paramTypes.length; i++) {
            CtClass paramCls = paramTypes[i]
            argTypesBuilder.append(paramCls.name)
            if (i != paramTypes.length - 1) {
                argTypesBuilder.append(",")
            }
        }
        return argTypesBuilder.toString()
    }


    private static void printLog(String content) {
        System.out.println(content)
    }

}
