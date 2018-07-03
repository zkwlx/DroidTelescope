package monitor.plugin.javassist.inject.interactive

import javassist.CtClass
import javassist.CtMethod
import monitor.plugin.utils.Logger

/**
 * Created by ZhouKeWen on 2017/5/19.
 */
class ItemOnSelectedHandler implements IInterfaceHandler {

    public static final String NAME = "android.widget.AdapterView\$OnItemSelectedListener"
    private static final String METHOD_NAME = "onItemSelected"

    @Override
    boolean handleInterface(CtClass clazz) {
        CtMethod[] declaredMethods = clazz.getDeclaredMethods()
        for (CtMethod method : declaredMethods) {
            //TODO 没判断第一个参数AdapterView，是个抽象类
            if (method.name == METHOD_NAME && method.parameterTypes.length == 4 && method.parameterTypes[0].name ==
                    "android.widget.AdapterView" && method.parameterTypes[1].name ==
                    "android.view.View" && method.parameterTypes[2].name == "int" && method.parameterTypes[3].name ==
                    "long") {
                Logger.i(
                        "inject onItemClick---------->" + clazz.name)
                method.insertBefore("""
                      andr.perf.monitor.injected.InteractiveSample.onItemSelected(\$0,\$1,\$2,\$3,\$4);
                """)
                return true
            }
        }

        return false

    }
}
