package monitor.plugin.javassist.inject.interactive

import javassist.CtClass
import javassist.CtMethod
import monitor.plugin.ConfigProvider
import monitor.plugin.utils.Logger

/**
 * Created by ZhouKeWen on 2017/5/16.
 */
class ViewOnLongClickHandler implements IInterfaceHandler {

    public static final String NAME = "android.view.View\$OnLongClickListener"
    private static final String METHOD_NAME = "onLongClick"

    private final String injectedMethod

    ViewOnLongClickHandler() {
        if (ConfigProvider.config.forRelease) {
            injectedMethod = "dt.monitor.injected.InteractiveSample.onViewLongClick"
        } else {
            injectedMethod = "andr.perf.monitor.injected.InteractiveSample.onViewLongClick"
        }
    }

    @Override
    boolean handleInterface(CtClass clazz) {
        CtMethod[] declaredMethods = clazz.getDeclaredMethods()
        for (CtMethod method : declaredMethods) {
            if (method.name == METHOD_NAME
                    && method.parameterTypes.length == 1
                    && method.parameterTypes[0].name == "android.view.View") {
                Logger.i("inject onLongClick---------->" + clazz.name)
                method.insertBefore("""
                      ${injectedMethod}(\$0,\$1);
                """)
                return true
            }
        }

        return false

    }

}