package monitor.plugin.javassist.inject.interactive

import javassist.CtClass
import javassist.CtMethod
import monitor.plugin.ConfigProvider
import monitor.plugin.utils.JavassistUtils
import monitor.plugin.utils.Logger

/**
 * Created by ZhouKeWen on 2017/5/16.
 */
class DialogOnClickHandler implements IInterfaceHandler {

    public static final String NAME = "android.content.DialogInterface\$OnClickListener"
    private static final String METHOD_NAME = "onClick"

    private final String injectedMethod

    DialogOnClickHandler(String className) {
        injectedMethod = "${className}.onDialogClick"
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
                    && types[0].name == "android.content.DialogInterface"
                    && types[1].name == "int") {
                Logger.i("inject dialog onClick---------->" + clazz.name)
                method.insertBefore("""
                      ${injectedMethod}(\$0,\$1,\$2);
                """)
                return true
            }
        }

        return false

    }

}