package monitor.plugin.javassist.inject.interactive

import javassist.CtClass
import javassist.CtMethod
import monitor.plugin.ConfigProvider
import monitor.plugin.utils.JavassistUtils
import monitor.plugin.utils.Logger

/**
 * Created by ZhouKeWen on 2017/5/19.
 */
class ItemOnLongClickHandler implements IInterfaceHandler {

    public static final String NAME = "android.widget.AdapterView\$OnItemLongClickListener"
    private static final String METHOD_NAME = "onItemLongClick"

    private final String injectedMethod

    ItemOnLongClickHandler(String className) {
        injectedMethod = "${className}.onItemLongClick"
    }

    @Override
    boolean handleInterface(CtClass clazz) {
        CtMethod[] declaredMethods = clazz.getDeclaredMethods()
        for (CtMethod method : declaredMethods) {
            CtClass[] types = JavassistUtils.getBehaviorParameterTypes(method)
            if (types == null) {
                continue
            }
            //TODO 没判断第一个参数AdapterView，是个抽象类
            if (method.name == METHOD_NAME
                    && types.length == 4
                    && types[0].name == "android.widget.AdapterView"
                    && types[1].name == "android.view.View"
                    && types[2] == CtClass.intType
                    && types[3] == CtClass.longType) {
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
