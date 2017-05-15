package monitor.plugin.javassist.inject;

import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import monitor.plugin.utils.LogUtils;

/**
 * 用户交互行为监控模块的代码注入器
 * Created by ZhouKeWen on 2017/5/11.
 */
public class InteractiveCodeInject {

    public static void injectForViewClick(CtClass clazz) {
        CtClass[] interfaces;
        try {
            interfaces = clazz.getInterfaces();
        } catch (NotFoundException e) {
            return;
        }
        if (interfaces == null || interfaces.length == 0) {
            return;
        }
        boolean shouldInject
        for (CtClass interfaceClass : interfaces) {
            if ("android.view.View\$OnClickListener" == interfaceClass.getName()) {
                shouldInject = true;
                break;
            }
        }
        if (shouldInject) {
            injectOnClick(clazz)

        }
    }

    private static void injectOnClick(CtClass clazz) {
        CtMethod[] declaredMethods = clazz.getDeclaredMethods();
        for (CtMethod method : declaredMethods) {
            if (method.name == "onClick" && method.parameterTypes.length == 1 && method.parameterTypes[0].name ==
                    "android.view.View") {
                LogUtils.printLog("inject onClick---------->" + clazz.name)
                method.addLocalVariable("__interactive_switch", CtClass.booleanType);
                method.insertBefore("""
                  __interactive_switch = andr.perf.monitor.injected.InteractiveSample.shouldMonitor();
                  if(__interactive_switch) {
                      andr.perf.monitor.injected.InteractiveSample.onViewClick(\$0,\$1);
                  }
                """)
            }
        }

    }
}
