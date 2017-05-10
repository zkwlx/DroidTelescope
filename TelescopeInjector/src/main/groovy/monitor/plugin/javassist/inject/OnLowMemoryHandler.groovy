package monitor.plugin.javassist.inject

import javassist.CtClass
import javassist.CtMethod

/**
 * <p>Created by ZhouKeWen on 17-4-4.</p>
 */
class OnLowMemoryHandler implements IMethodHandler {

    private boolean hasLowMemoryMethod = false

    @Override
    boolean handleMethod(CtClass clazz, CtMethod ctMethod) {
        if ("onLowMemory" == ctMethod.name && ctMethod.parameterTypes.size() == 0) {
            MemoryCodeInject.insertLowMemoryCode(clazz, ctMethod)
            hasLowMemoryMethod = true
            return true
        } else {
            return false
        }
    }

    @Override
    void checkMethodAndAdd(CtClass clazz) {
        if (hasLowMemoryMethod) {
            hasLowMemoryMethod = false
        } else {
            System.out.println("没有lowMemory方法？？？？>>>>>> ${clazz.name}")
            MemoryCodeInject.addLowMemoryCode(clazz)
        }
    }
}
