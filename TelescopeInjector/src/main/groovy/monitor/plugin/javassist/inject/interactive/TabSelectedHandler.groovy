package monitor.plugin.javassist.inject.interactive

import javassist.CtClass
import javassist.CtMethod
import monitor.plugin.utils.JavassistUtils
import monitor.plugin.utils.Logger

/**
 * Created by ZhouKeWen on 2017/5/19.
 */
class TabSelectedHandler implements IInterfaceHandler {

    public static
    final String NAME = "android.support.design.widget.TabLayout\$OnTabSelectedListener"
    private static final String METHOD_NAME = "onTabSelected"
//    private static final String METHOD_NAME2 = "onTabUnselected"
//    private static final String METHOD_NAME3 = "onTabReselected"

    private final String injectedMethod
//    private final String injectedMethod2
//    private final String injectedMethod3

    TabSelectedHandler(String className) {
        injectedMethod = "${className}.onTabSelected"
        //onTabSelected 和 onTabUnselected成对出现，所以只监听 selected
//        injectedMethod2 = "${className}.onTabUnselected"
//        injectedMethod3 = "${className}.onTabReselected"
    }

    @Override
    boolean handleInterface(CtClass clazz) {
        CtMethod[] declaredMethods = clazz.getDeclaredMethods()
        for (CtMethod method : declaredMethods) {
            CtClass[] types = JavassistUtils.getBehaviorParameterTypes(method)
            if (types == null) {
                continue
            }
            if (method.name == METHOD_NAME && onlyTabArgs(types)) {
                Logger.d("inject ${METHOD_NAME}---------->" + clazz.name)
                method.insertBefore("""
                      ${injectedMethod}(\$0,\$1);
                """)
                return true
            }
//            else if (method.name == METHOD_NAME2 && onlyTabArgs(types)) {
//                Logger.i("inject ${METHOD_NAME2}---------->" + clazz.name)
//                method.insertBefore("""
//                      ${injectedMethod2}(\$0,\$1);
//                """)
//                return true
//            }
//            else if (method.name == METHOD_NAME3 && onlyTabArgs(types)) {
//                Logger.i("inject ${METHOD_NAME3}---------->" + clazz.name)
//                method.insertBefore("""
//                      ${injectedMethod3}(\$0,\$1);
//                """)
//                return true
//            }
        }

        return false

    }

    private boolean onlyTabArgs(CtClass[] types) {
        return types.length == 1 && types[0].name == "android.support.design.widget.TabLayout\$Tab"
    }
}
