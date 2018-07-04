package monitor.plugin.javassist.inject.interactive

import javassist.CtClass
import javassist.CtMethod
import monitor.plugin.ConfigProvider
import monitor.plugin.utils.Logger

/**
 * Created by ZhouKeWen on 2017/5/19.
 */
class ItemOnClickHandler implements IInterfaceHandler {

    public static final String NAME = "android.widget.AdapterView\$OnItemClickListener"
    private static final String METHOD_NAME = "onItemClick"

    private final String injectedMethod

    ItemOnClickHandler() {
        if (ConfigProvider.config.forRelease) {
            injectedMethod = "dt.monitor.injected.InteractiveSample.onItemClick"
        } else {
            injectedMethod = "andr.perf.monitor.injected.InteractiveSample.onItemClick"
        }
    }

    @Override
    boolean handleInterface(CtClass clazz) {
        CtMethod[] declaredMethods = clazz.getDeclaredMethods()
        for (CtMethod method : declaredMethods) {
            //TODO 没判断第一个参数AdapterView，是个抽象类
            if (method.name == METHOD_NAME
                    && method.parameterTypes.length == 4
                    && method.parameterTypes[0].name == "android.widget.AdapterView"
                    && method.parameterTypes[1].name == "android.view.View"
                    && method.parameterTypes[2].name == "int"
                    && method.parameterTypes[3].name == "long") {
                Logger.i("inject onItemClick---------->" + clazz.name)
                method.insertBefore("""
                      ${injectedMethod}(\$0,\$1,\$2,\$3,\$4);
                """)
                return true
            }
        }

        return false

    }
}
