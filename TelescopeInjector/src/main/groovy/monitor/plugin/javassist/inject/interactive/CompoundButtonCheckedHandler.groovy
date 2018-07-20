package monitor.plugin.javassist.inject.interactive

import javassist.CtClass
import javassist.CtMethod
import monitor.plugin.utils.JavassistUtils
import monitor.plugin.utils.Logger

/**
 * Created by ZhouKeWen on 2017/5/16.
 */
class CompoundButtonCheckedHandler implements IInterfaceHandler {

    public static final String NAME = "android.widget.CompoundButton\$OnCheckedChangeListener"
    private static final String METHOD_NAME = "onCheckedChanged"

    private final String injectedMethod

    CompoundButtonCheckedHandler(String className) {
        injectedMethod = "${className}.onCheckedChanged"
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
                    && types[0].name == "android.widget.CompoundButton"
                    && types[1] == CtClass.booleanType) {
                Logger.d("inject ${METHOD_NAME}---------->" + clazz.name)
                method.insertBefore("""
                      ${injectedMethod}(\$0,\$1,\$2);
                """)
                return true
            }
        }

        return false

    }

}