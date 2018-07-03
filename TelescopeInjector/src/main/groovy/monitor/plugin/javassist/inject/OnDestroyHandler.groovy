package monitor.plugin.javassist.inject

import javassist.CtClass
import javassist.CtMethod

/**
 * <p>Created by ZhouKeWen on 17-4-4.</p>
 */
class OnDestroyHandler implements IMethodHandler {

    @Override
    boolean modifyMethod(CtClass clazz, CtMethod ctMethod) {
        if ("onDestroy" == ctMethod.name && ctMethod.parameterTypes.size() == 0) {
            MemoryCodeInject.insertDestroySampleCode(clazz, ctMethod)
            return true
        } else {
            return false
        }
    }

    @Override
    void addMethod(CtClass clazz) {
        System.out.println("没有destroy方法，add>>>>>> ${clazz.name}")
        MemoryCodeInject.addObjectDestroyMethod(clazz)
    }
}
