package monitor.plugin.javassist.inject.interactive

import javassist.CtClass
import javassist.CtMethod
import monitor.plugin.utils.JavassistUtils
import monitor.plugin.utils.Logger

/**
 * Created by ZhouKeWen on 2017/5/16.
 */
class DialogOnKeyHandler implements IInterfaceHandler {

    public static final String NAME = "android.content.DialogInterface\$OnKeyListener"
    private static final String METHOD_NAME = "onKey"

    private final String injectedMethod

    DialogOnKeyHandler(String className) {
        injectedMethod = "${className}.onDialogKey"
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
                    && types.length == 3
                    && types[0].name == "android.content.DialogInterface"
                    && types[1] == CtClass.intType
                    && types[2].name == "android.view.KeyEvent") {
                Logger.d("inject onDialogKey---------->" + clazz.name)
                method.insertBefore("""
                      ${injectedMethod}(\$0,\$1,\$2,\$3);
                """)
                return true
            }
        }

        return false

    }

}