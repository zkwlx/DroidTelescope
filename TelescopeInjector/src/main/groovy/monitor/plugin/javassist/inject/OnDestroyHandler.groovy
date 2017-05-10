package monitor.plugin.javassist.inject

import javassist.CtClass
import javassist.CtMethod

/**
 * <p>Created by ZhouKeWen on 17-4-4.</p>
 */
class OnDestroyHandler implements IMethodHandler {

    private boolean hasDestroyMethod = false

    @Override
    boolean handleMethod(CtClass clazz, CtMethod ctMethod) {
        if ("onDestroy" == ctMethod.name && ctMethod.parameterTypes.size() == 0) {
            MemoryCodeInject.insertDestroySampleCode(clazz, ctMethod)
            hasDestroyMethod = true
            return true
        } else {
            return false
        }
    }

    @Override
    void checkMethodAndAdd(CtClass clazz) {
        if (hasDestroyMethod) {
            hasDestroyMethod = false
        } else {
            System.out.println("没有destroy方法？？？？>>>>>> ${clazz.name}")
            MemoryCodeInject.addObjectDestroyMethod(clazz)
        }
    }
}
