package monitor.plugin.javassist.inject;

import javassist.CtClass;
import javassist.CtMethod
import monitor.plugin.javassist.JavassistHandler
import monitor.plugin.utils.LogUtils;

/**
 * <p>Created by ZhouKeWen on 17-4-4.</p>
 */
public class OnCreateHandler implements IMethodHandler {

    private boolean hasCreateMethod = false

    private static final String BUNDLE = "android.os.Bundle"

    public OnCreateHandler() {
    }

    @Override
    public boolean handleMethod(CtClass clazz, CtMethod ctMethod) {
        if ("onCreate" == ctMethod.name && ctMethod.parameterTypes.size() == 1 &&
                ctMethod.parameterTypes[0].name == BUNDLE) {
            MemoryCodeInject.insertCreateSampleCode(clazz, ctMethod)
            hasCreateMethod = true
            return true
        } else {
            return false
        }

    }

    @Override
    public void checkMethodAndAdd(CtClass clazz) {
        if (hasCreateMethod) {
            //重置状态
            hasCreateMethod = false
        } else {
            System.out.println("没有create方法！！！！！>>>>>> ${clazz.name}")
            MemoryCodeInject.addObjectCreateMethod(clazz)
        }
    }
}
