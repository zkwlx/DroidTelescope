package monitor.plugin.javassist.inject.interactive

import javassist.CtClass
import javassist.CtMethod
import monitor.plugin.utils.JavassistUtils
import monitor.plugin.utils.Logger

/**
 * Created by ZhouKeWen on 2017/5/19.
 */
class ViewPagerOnPageChangeHandler implements IInterfaceHandler {

    public static final String NAME = "android.support.v4.view.ViewPager\$OnPageChangeListener"
    private static final String METHOD_NAME = "onPageSelected"
    private static final String METHOD_NAME2 = "onPageScrollStateChanged"

    private final String injectedMethod
    private final String injectedMethod2

    ViewPagerOnPageChangeHandler(String className) {
        injectedMethod = "${className}.onPageSelected"
        injectedMethod2 = "${className}.onPageScrollStateChanged"
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
                    && types[0] == CtClass.intType) {
                Logger.d("inject ${METHOD_NAME}---------->" + clazz.name)
                method.insertBefore("""
                      ${injectedMethod}(\$0,\$1);
                """)
                return true
            } else if (method.name == METHOD_NAME2
                    && types.length == 1
                    && types[0] == CtClass.intType) {
                Logger.d("inject ${METHOD_NAME2}---------->" + clazz.name)
                method.insertBefore("""
                      ${injectedMethod2}(\$0,\$1);
                """)
                return true
            }
        }

        return false

    }
}
