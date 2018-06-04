package monitor.plugin.javassist.inject.interactive

import javassist.CtClass
import javassist.CtMethod
import monitor.plugin.utils.Logger

/**
 * Created by ZhouKeWen on 2017/5/16.
 */
class ViewOnClickHandler implements IInterfaceHandler {

    public static final String NAME = "android.view.View\$OnClickListener"
    private static final String METHOD_NAME = "onClick"

    @Override
    boolean handleInterface(CtClass clazz) {
        CtMethod[] declaredMethods = clazz.getDeclaredMethods()
        for (CtMethod method : declaredMethods) {
            if (method.name == METHOD_NAME && method.parameterTypes.length == 1 && method.parameterTypes[0].name ==
                    "android.view.View") {
                Logger.i("inject onClick---------->" + clazz.name)
                method.addLocalVariable("__interactive_switch", CtClass.booleanType)
                method.insertBefore("""
                  __interactive_switch = andr.perf.monitor.injected.InteractiveSample.shouldMonitor();
                  if(__interactive_switch) {
                      andr.perf.monitor.injected.InteractiveSample.onViewClick(\$0,\$1);
                  }
                """)
                return true
            }
        }

        return false

    }

}