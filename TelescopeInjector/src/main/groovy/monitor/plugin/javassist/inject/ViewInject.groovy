package monitor.plugin.javassist.inject;

import javassist.CtClass
import javassist.CtConstructor
import javassist.CtMethod
import javassist.CtNewConstructor
import javassist.Modifier
import javassist.NotFoundException
import javassist.bytecode.Descriptor
import monitor.plugin.javassist.JavassistHandler
import monitor.plugin.utils.JavassistUtils
import monitor.plugin.utils.Logger;

/**
 * @author zhoukewen
 * @since 2018/6/28
 */
class ViewInject {

    private static final String CONTEXT = "android.content.Context"
    private static final String ATTRIBUTE_SET = "android.util.AttributeSet"

    static int viewCount
    static int hasConViewCount

    static boolean isNotView(CtClass ctClass) {
        CtClass superClazz = JavassistUtils.getSuperclass(ctClass)
        boolean isView
        int c = 0
        long time = System.currentTimeMillis()
        while (true) {
            c++
            if (superClazz == null || superClazz == CtClass.voidType) {
                isView = false
                break
            } else if (superClazz.name == "android.view.View" || superClazz.name == "android.view.ViewGroup") {
                isView = true
                break
            }
            superClazz = JavassistUtils.getSuperclass(superClazz)
        }
        time = System.currentTimeMillis() - time
        if (isView) {
            Logger.d("inject view-----> ${ctClass.name} isView:${isView}, count=${c}, time=${time}")
        }
        return !isView
    }

    static void injectForView(CtClass viewClass) {
        viewCount++
//        injectConstructor(viewClass)
        injectLifeCycleMethod(viewClass)
    }

    private static void injectConstructor(CtClass viewClass) {
        CtClass contextClass = JavassistHandler.classPool.makeClass(CONTEXT)
        CtClass attributeClass = JavassistHandler.classPool.makeClass(ATTRIBUTE_SET)
//        CtClass[] classes = [contextClass, attributeClass, CtClass.intType, CtClass.intType]
        CtClass[] classes = [contextClass]
        CtConstructor constructor
        try {
            constructor = viewClass.getConstructor(Descriptor.ofConstructor(classes))
        } catch (NotFoundException e) {
            //忽略
        }

        if (constructor) {
            hasConViewCount++
            Logger.i("----->" + constructor.name)
            constructor.insertBefore("""
                    andr.perf.monitor.injected.ViewLifeCycleSample.onNew("${viewClass.name}");
            """)
        } else {
            CtConstructor ct = CtNewConstructor.make(classes, null, """{
                    andr.perf.monitor.injected.ViewLifeCycleSample.onNew("${viewClass.name}");
            super(\$1,\$2,\$3,\$4);
        }
        """, viewClass)
            viewClass.addConstructor(ct)
        }
    }

    private static void injectLifeCycleMethod(CtClass viewClass) {
        //TODO 测一下有参数的同名方法是否被返回
        CtMethod finishInflateMethod
        CtMethod detachedFromWindowMethod
        CtMethod attachedToWindowMethod
        CtMethod[] methods = viewClass.getDeclaredMethods()
        for (CtMethod method : methods) {
            int modifiers = method.getModifiers()
            if (Modifier.isStatic(modifiers) || Modifier.isNative(modifiers)) {
                Logger.d(
                        "static or native!!!Don't inject>> ${viewClass.name}.${method.name}")
                continue
            }
            CtClass[] types = JavassistUtils.getBehaviorParameterTypes(method)
            if (types == null) {
                Logger.i("Method error: ${viewClass.name}.${method.name}")
                continue
            }
            if ("onFinishInflate" == method.name && types.size() == 0) {
                finishInflateMethod = method
            }
            if ("onDetachedFromWindow" == method.name && types.size() == 0) {
                detachedFromWindowMethod = method
            }
            if ("onAttachedToWindow" == method.name && types.size() == 0) {
                attachedToWindowMethod = method
            }
            if (finishInflateMethod && detachedFromWindowMethod && attachedToWindowMethod) {
                break
            }
        }

        if (finishInflateMethod) {
            finishInflateMethod.insertBefore("""
                    andr.perf.monitor.injected.ViewLifeCycleSample.onFinishInflate(\$0);
            """)
        } else {
            finishInflateMethod = CtMethod.make("""
            public void onFinishInflate() {
                andr.perf.monitor.injected.ViewLifeCycleSample.onFinishInflate(\$0);
                super.onFinishInflate();
            }
            """, viewClass)
            viewClass.addMethod(finishInflateMethod)
        }

        if (detachedFromWindowMethod) {
            detachedFromWindowMethod.insertBefore("""
                    andr.perf.monitor.injected.ViewLifeCycleSample.onDetachedFromWindow(\$0);
            """)
        } else {
            detachedFromWindowMethod = CtMethod.make("""
            public void onDetachedFromWindow() {
                andr.perf.monitor.injected.ViewLifeCycleSample.onDetachedFromWindow(\$0);
                super.onDetachedFromWindow();
            }
            """, viewClass)
            viewClass.addMethod(detachedFromWindowMethod)
        }

        if (attachedToWindowMethod) {
            attachedToWindowMethod.insertBefore("""
                    andr.perf.monitor.injected.ViewLifeCycleSample.onAttachedToWindow(\$0);
            """)
        } else {
            attachedToWindowMethod = CtMethod.make("""
            public void onAttachedToWindow() {
                andr.perf.monitor.injected.ViewLifeCycleSample.onAttachedToWindow(\$0);
                super.onAttachedToWindow();
            }
            """, viewClass)
            viewClass.addMethod(attachedToWindowMethod)
        }

    }

}