package monitor.plugin.javassist.inject.interactive

import javassist.CtClass
import javassist.CtMethod
import monitor.plugin.utils.JavassistUtils
import monitor.plugin.utils.Logger

/**
 * Created by ZhouKeWen on 2017/5/19.
 */
class SwipeRefreshHandler implements IInterfaceHandler {

    public static
    final String NAME = "android.support.v4.widget.SwipeRefreshLayout\$OnRefreshListener"
    private static final String METHOD_NAME = "onRefresh"

    private final String injectedMethod

    SwipeRefreshHandler(String className) {
        injectedMethod = "${className}.onSwipeRefresh"
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
                    && types.length == 0) {
                Logger.i("inject ${METHOD_NAME}---------->" + clazz.name)
                method.insertBefore("""
                      ${injectedMethod}(\$0);
                """)
                return true
            }
        }

        return false

    }
}
