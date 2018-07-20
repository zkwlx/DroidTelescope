package monitor.plugin.javassist.inject.interactive

import javassist.CtClass
import javassist.CtMethod
import monitor.plugin.ConfigProvider
import monitor.plugin.utils.JavassistUtils
import monitor.plugin.utils.Logger

/**
 * Created by ZhouKeWen on 2017/5/16.
 */
class ViewOnClickHandler implements IInterfaceHandler {

    public static final String NAME = "android.view.View\$OnClickListener"
    private static final String METHOD_NAME = "onClick"

    private final String injectedMethod

    ViewOnClickHandler(String className) {
        injectedMethod = "${className}.onViewClick"
    }

    @Override
    boolean handleInterface(CtClass clazz) {
        CtMethod[] declaredMethods = clazz.getDeclaredMethods()
        for (CtMethod method : declaredMethods) {
            CtClass[] types = JavassistUtils.getBehaviorParameterTypes(method)
            if (types == null) {
                continue
            }
            if (method.name == METHOD_NAME
                    && types.length == 1
                    && types[0].name == "android.view.View") {
                Logger.d("inject onClick---------->" + clazz.name)
                method.insertBefore("""
                      ${injectedMethod}(\$0,\$1);
                """)
                return true
            }
        }

        return false

    }

}