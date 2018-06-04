package monitor.plugin.javassist.inject;

import javassist.CtClass;
import javassist.CtMethod

/**
 * <p>Created by ZhouKeWen on 17-4-4.</p>
 */
class OnCreateHandler implements IMethodHandler {

    private boolean hasCreateMethod = false

    private static final String BUNDLE = "android.os.Bundle"

    OnCreateHandler() {
    }

    @Override
    boolean handleMethod(CtClass clazz, CtMethod ctMethod) {
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
    void checkMethodAndAdd(CtClass clazz) {
        if (hasCreateMethod) {
            //重置状态
            hasCreateMethod = false
        } else {
            System.out.println("没有create方法！！！！！>>>>>> ${clazz.name}")
            MemoryCodeInject.addObjectCreateMethod(clazz)
        }
    }
}
