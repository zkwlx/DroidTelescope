package monitor.plugin.javassist.inject

import javassist.CtClass
import javassist.CtMethod
import monitor.plugin.utils.Logger

/**
 * <p>Created by ZhouKeWen on 17-4-4.</p>
 */
class OnLowMemoryHandler implements IMethodHandler {

    @Override
    boolean modifyMethod(CtClass clazz, CtMethod ctMethod) {
        if ("onLowMemory" == ctMethod.name && ctMethod.parameterTypes.size() == 0) {
            MemoryCodeInject.insertLowMemoryCode(clazz, ctMethod)
            return true
        } else {
            return false
        }
    }

    @Override
    void addMethod(CtClass clazz) {
        Logger.d("没有lowMemory方法, add>>>>>> ${clazz.name}")
        MemoryCodeInject.addLowMemoryCode(clazz)
    }
}
