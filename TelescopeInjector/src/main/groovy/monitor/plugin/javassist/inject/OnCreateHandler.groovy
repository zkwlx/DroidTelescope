package monitor.plugin.javassist.inject;

import javassist.CtClass;
import javassist.CtMethod

/**
 * <p>Created by ZhouKeWen on 17-4-4.</p>
 */
class OnCreateHandler implements IMethodHandler {

    private static final String BUNDLE = "android.os.Bundle"

    OnCreateHandler() {
    }

    @Override
    boolean modifyMethod(CtClass clazz, CtMethod ctMethod) {
        if ("onCreate" == ctMethod.name && ctMethod.parameterTypes.size() == 1 &&
                ctMethod.parameterTypes[0].name == BUNDLE) {
            MemoryCodeInject.insertCreateSampleCode(clazz, ctMethod)
            return true
        } else {
            return false
        }

    }

    @Override
    void addMethod(CtClass clazz) {
        System.out.println("没有create方法, add>>>>>> ${clazz.name}")
        MemoryCodeInject.addObjectCreateMethod(clazz)
    }
}
