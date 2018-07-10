package monitor.plugin.javassist.inject.interactive

import javassist.CtClass
import javassist.CtMethod
import monitor.plugin.utils.JavassistUtils
import monitor.plugin.utils.Logger

/**
 * Created by ZhouKeWen on 2017/5/16.
 */
class ViewOnTouchHandler implements IInterfaceHandler {

    public static final String NAME = "android.view.View\$OnTouchListener"
    private static final String METHOD_NAME = "onTouch"

    private final String injectedMethod

    ViewOnTouchHandler(String className) {
        injectedMethod = "${className}.onViewTouch"
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
                    && types.length == 2
                    && types[0].name == "android.view.View"
                    && types[1].name == "android.view.MotionEvent") {
                Logger.i("inject onViewTouch---------->" + clazz.name)
                method.insertBefore("""
                      ${injectedMethod}(\$0,\$1,\$2);
                """)
                return true
            }
        }

        return false

    }

}