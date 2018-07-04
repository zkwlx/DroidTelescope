package monitor.plugin.javassist.inject.reference_leak

import javassist.CtClass
import javassist.CtMethod
import javassist.CtNewMethod
import javassist.Modifier
import javassist.NotFoundException
import monitor.plugin.utils.JavassistUtils
import monitor.plugin.utils.Logger

/**
 * 内存监控模块的代码注入器
 * <p>Created by ZhouKeWen on 17-4-3.</p>
 */
class ReferenceLeakCodeInject {

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

    private final static ArrayList<IMethodHandler> methodHandlers
    private final static ArrayList<IMethodHandler> v4MethodHandlers

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

    static void injectForReferenceLeak(CtClass clazz) {
        if (classNotCare(clazz)) {
            //不是内存监控模块关心的类，不注入
            return
        }
        long time = System.currentTimeMillis()
        List<IMethodHandler> handlerList
        if (isV4OrV7Class(clazz)) {
            handlerList = v4MethodHandlers.clone()
        } else {
            handlerList = methodHandlers.clone()
        }

        CtMethod[] ctMethods = clazz.getDeclaredMethods()
        //遍历这个类的所有方法
        for (CtMethod ctMethod : ctMethods) {
            Logger.d("scan memory method:::>>>> ${clazz.name}.${ctMethod.name}")
            int modifiers = ctMethod.getModifiers()
            if (Modifier.isStatic(modifiers) || Modifier.isNative(modifiers)) {
                Logger.d("static or native!!!Don't inject>> ${clazz.name}.${ctMethod.name}")
                continue
            }
            //对该方法遍历所有 IMethodHandler 并调用 modifyMethod
            Iterator<IMethodHandler> iterator = handlerList.iterator()
            while (iterator.hasNext()) {
                IMethodHandler handler = iterator.next()
                if (handler.modifyMethod(clazz, ctMethod)) {
                    Logger.d("inject memory code:::>>>> ${clazz.name}.${ctMethod.name}")
                    iterator.remove()
                    break
                }
            }
            if (handlerList.isEmpty()) {
                //所有关键方法注入成功，不再处理后续方法
                break
            }
        }

        //剩下的 handlers 说明对应的关键 method 没有实现，则add这个method
        for (IMethodHandler handler : handlerList) {
            handler.addMethod(clazz)
        }
        time = System.currentTimeMillis() - time
        Logger.i("Inject memory sample code duration: ${time}, class: ${clazz.name}")
    }

    static void addObjectCreateMethod(CtClass clazz) {
        CtMethod m = CtNewMethod.make(
                """protected void onCreate(android.os.Bundle s) {
                       super.onCreate(s);
                       andr.perf.monitor.injected.ObjectLeakSample.objectCreate(\$0);
                   }
                """,
                clazz)
        clazz.addMethod(m)
    }

    static void addObjectDestroyMethod(CtClass clazz) {
        CtMethod m = CtNewMethod.make(
                """protected void onDestroy() {
                       super.onDestroy();
                       andr.perf.monitor.injected.ObjectLeakSample.objectDestroy(\$0);
                   }
                """,
                clazz)
        clazz.addMethod(m)
    }

    static void insertCreateSampleCode(CtClass clazz, CtMethod ctMethod) {
        //TODO 这些shouldMonitor判断是否可以放到类的属性里，每个方法调用一次不太好
        ctMethod.insertBefore("""
                      andr.perf.monitor.injected.ObjectLeakSample.objectCreate(\$0);
                """)
    }

    static void insertDestroySampleCode(CtClass clazz, CtMethod ctMethod) {
        ctMethod.insertBefore("""
                      andr.perf.monitor.injected.ObjectLeakSample.objectDestroy(\$0);
                """)
    }

    static void insertLowMemoryCode(CtClass clazz, CtMethod ctMethod) {
        ctMethod.insertBefore("""
                      andr.perf.monitor.injected.ObjectLeakSample.objectLowMemory(\$0);
                """)
    }

    static void insertTrimMemoryCode(CtClass clazz, CtMethod ctMethod) {
        ctMethod.insertBefore("""
                      andr.perf.monitor.injected.ObjectLeakSample.objectTrimMemory(\$0, \$1);
                """)
    }

    static void addLowMemoryCode(CtClass clazz) {
        CtMethod m = CtNewMethod.make(
                """public void onLowMemory() {
                       super.onLowMemory();
                       andr.perf.monitor.injected.ObjectLeakSample.objectLowMemory(\$0);
                   }
                """,
                clazz)
        clazz.addMethod(m)
    }

    static void addTrimMemoryCode(CtClass clazz) {
        CtMethod m = CtNewMethod.make(
                """public void onTrimMemory(int level) {
                       super.onTrimMemory(level);
                       andr.perf.monitor.injected.ObjectLeakSample.objectTrimMemory(\$0, level);
                   }
                """,
                clazz)
        clazz.addMethod(m)
    }


    private static boolean classNotCare(CtClass ctClass) {
        //TODO 这里只监控一层
        CtClass superClazz = JavassistUtils.getSuperclass(ctClass)
        if (superClazz == null || superClazz == CtClass.voidType) {
            //父类为 Object 或父类抛出 NotFoundException
            return true
        } else {
            //TODO 类过滤代码在这里添加，注意Activity、Fragment、Service等都要考虑
            return !ACTIVITY_CLASSES.contains(superClazz.name) && !FRAGMENT_CLASSES.contains(superClazz.name)
        }
    }

    private static boolean isV4OrV7Class(CtClass ctClass) {
        CtClass superClazz = JavassistUtils.getSuperclass(ctClass)
        if (superClazz == null || superClazz == CtClass.voidType) {
            //父类为 Object 或父类抛出 NotFoundException
            return false
        } else {
            return superClazz.name.startsWith("android.support.v4") || superClazz.name.startsWith("android.support.v7")
        }
    }

}
