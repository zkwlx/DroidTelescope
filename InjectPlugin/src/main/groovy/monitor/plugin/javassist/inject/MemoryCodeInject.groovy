package monitor.plugin.javassist.inject

import javassist.CtClass
import javassist.CtMethod
import javassist.CtNewMethod

/**
 * <p>Created by ZhouKeWen on 17-4-3.</p>
 */
class MemoryCodeInject {

    public static void addObjectCreateMethod(CtClass clazz) {
        CtMethod m = CtNewMethod.make(
                """protected void onCreate(android.os.Bundle s) {
                       super.onCreate(s);
                       andr.perf.monitor.injected.ObjectLeakSample.objectCreate(\$0);
                   }
                """,
                clazz)
        clazz.addMethod(m)
    }

    public static void addObjectDestroyMethod(CtClass clazz) {
        CtMethod m = CtNewMethod.make(
                """protected void onDestroy() {
                       super.onDestroy();
                       andr.perf.monitor.injected.ObjectLeakSample.objectDestroy(\$0);
                   }
                """,
                clazz)
        clazz.addMethod(m)
    }

    public static void insertCreateSampleCode(CtClass clazz, CtMethod ctMethod) {
        //TODO 这些shouldMonitor判断是否可以放到类的属性里，每个方法调用一次不太好
        ctMethod.addLocalVariable("__memory_switch", CtClass.booleanType);
        ctMethod.insertBefore("""
                  __memory_switch = andr.perf.monitor.injected.ObjectLeakSample.shouldMonitor();
                  if(__memory_switch) {
                      andr.perf.monitor.injected.ObjectLeakSample.objectCreate(\$0);
                  }
                """)
    }

    public static void insertDestroySampleCode(CtClass clazz, CtMethod ctMethod) {
        ctMethod.addLocalVariable("__memory_switch", CtClass.booleanType);
        ctMethod.insertBefore("""
                  __memory_switch = andr.perf.monitor.injected.ObjectLeakSample.shouldMonitor();
                  if(__memory_switch) {
                      andr.perf.monitor.injected.ObjectLeakSample.objectDestroy(\$0);
                  }
                """)
    }

}
