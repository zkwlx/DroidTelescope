package monitor.plugin.javassist.inject.interactive

import javassist.CtClass
import javassist.CtMethod
import monitor.plugin.utils.JavassistUtils
import monitor.plugin.utils.Logger

/**
 * Created by ZhouKeWen on 2017/5/19.
 */
class MenuItemClickHandler implements IInterfaceHandler {

    public static final String NAME1 = "android.support.v7.widget.PopupMenu\$OnMenuItemClickListener"
    public static final String NAME2 = "android.widget.PopupMenu\$OnMenuItemClickListener"
    public static final String NAME3 = "android.support.v7.widget.Toolbar\$OnMenuItemClickListener"
    public static final String NAME4 = "android.view.MenuItem\$OnMenuItemClickListener"
    private static final String METHOD_NAME = "onMenuItemClick"

    private final String injectedMethod

    MenuItemClickHandler(String className) {
        injectedMethod = "${className}.onMenuItemClick"
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
                    && types[0].name == "android.view.MenuItem") {
                Logger.d("inject ${METHOD_NAME}---------->" + clazz.name)
                method.insertBefore("""
                      ${injectedMethod}(\$0,\$1);
                """)
                return true
            }
        }

        return false

    }
}
