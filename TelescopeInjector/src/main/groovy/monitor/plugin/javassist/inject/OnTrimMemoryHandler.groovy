package monitor.plugin.javassist.inject

import javassist.CtClass
import javassist.CtMethod
import monitor.plugin.utils.Logger

/**
 * <p>Created by ZhouKeWen on 17-4-4.</p>
 */
class OnTrimMemoryHandler implements IMethodHandler {

    @Override
    boolean modifyMethod(CtClass clazz, CtMethod ctMethod) {
        if ("onTrimMemory" == ctMethod.name && ctMethod.parameterTypes.size() == 1 &&
                ctMethod.parameterTypes[0] == CtClass.intType) {
            MemoryCodeInject.insertTrimMemoryCode(clazz, ctMethod)
            return true
        } else {
            return false
        }
    }

    @Override
    void addMethod(CtClass clazz) {
        Logger.i("没有trimMemory方法, add>>>>>> ${clazz.name}")
        MemoryCodeInject.addTrimMemoryCode(clazz)
    }
}
