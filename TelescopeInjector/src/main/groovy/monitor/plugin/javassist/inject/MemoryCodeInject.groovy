package monitor.plugin.javassist.inject

import javassist.CtClass
import javassist.CtMethod
import javassist.CtNewMethod

/**
 * 内存监控模块的代码注入器
 * <p>Created by ZhouKeWen on 17-4-3.</p>
 */
class MemoryCodeInject {

    /**
     * 内存监控模块关心的Activity相关类
     */
    private final static String[] ACTIVITY_CLASSES = ["android.app.Activity",
                                                      "android.app.ActivityGroup",
                                                      "android.support.v7.app.AppCompatActivity",
                                                      "android.accounts.AccountAuthenticatorActivity",
                                                      "android.support.v7.app.ActionBarActivity",
                                                      "android.app.ActivityGroup",
                                                      "android.app.AliasActivity",
                                                      "android.support.v7.app.AppCompatActivity",
                                                      "android.app.ExpandableListActivity",
                                                      "android.support.v4.app.FragmentActivity",
                                                      "android.app.LauncherActivity",
                                                      "android.app.ListActivity",
                                                      "android.app.NativeActivity",
                                                      "android.preference.PreferenceActivity",
                                                      "android.support.v4.app.SupportActivity",
                                                      "android.app.TabActivity"]

    /**
     * 内存监控模块关心的Fragment相关类
     */
    private final static String[] FRAGMENT_CLASSES = ["android.app.Fragment",
                                                      "android.app.DialogFragment",
                                                      "android.app.ListFragment",
                                                      "android.preference.PreferenceFragment",
                                                      "android.webkit.WebViewFragment",
                                                      "android.support.v4.app.Fragment",
                                                      "android.support.v7.app.AppCompatDialogFragment",
                                                      "android.support.v4.app.DialogFragment",
                                                      "android.support.v4.app.ListFragment"]


    public static boolean classNotCare(CtClass ctClass) {
        CtClass superClazz = ctClass.getSuperclass();
        //TODO 类过滤代码在这里添加，注意Activity、Fragment、Service等都要考虑
        return !ACTIVITY_CLASSES.contains(superClazz.name) && !FRAGMENT_CLASSES.contains(superClazz.name)
    }

    public static boolean isV4OrV7Class(CtClass ctClass) {
        CtClass superClazz = ctClass.getSuperclass();
        return superClazz.name.startsWith("android.support.v4") || superClazz.name.startsWith("android.support.v7")
    }

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

    public static void insertLowMemoryCode(CtClass clazz, CtMethod ctMethod) {
        ctMethod.addLocalVariable("__memory_switch", CtClass.booleanType);
        ctMethod.insertBefore("""
                  __memory_switch = andr.perf.monitor.injected.ObjectLeakSample.shouldMonitor();
                  if(__memory_switch) {
                      andr.perf.monitor.injected.ObjectLeakSample.objectLowMemory(\$0);
                  }
                """)
    }

    public static void insertTrimMemoryCode(CtClass clazz, CtMethod ctMethod) {
        ctMethod.addLocalVariable("__memory_switch", CtClass.booleanType);
        ctMethod.insertBefore("""
                  __memory_switch = andr.perf.monitor.injected.ObjectLeakSample.shouldMonitor();
                  if(__memory_switch) {
                      andr.perf.monitor.injected.ObjectLeakSample.objectTrimMemory(\$0, \$1);
                  }
                """)
    }

    public static void addLowMemoryCode(CtClass clazz) {
        CtMethod m = CtNewMethod.make(
                """public void onLowMemory() {
                       super.onLowMemory();
                       andr.perf.monitor.injected.ObjectLeakSample.objectLowMemory(\$0);
                   }
                """,
                clazz)
        clazz.addMethod(m)
    }

    public static void addTrimMemoryCode(CtClass clazz) {
        CtMethod m = CtNewMethod.make(
                """public void onTrimMemory(int level) {
                       super.onTrimMemory(level);
                       andr.perf.monitor.injected.ObjectLeakSample.objectTrimMemory(\$0, level);
                   }
                """,
                clazz)
        clazz.addMethod(m)
    }

}
