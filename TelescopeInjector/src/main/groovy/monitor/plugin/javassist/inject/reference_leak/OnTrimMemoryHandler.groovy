package monitor.plugin.javassist.inject.reference_leak

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
            ReferenceLeakCodeInject.insertTrimMemoryCode(clazz, ctMethod)
            return true
        } else {
            return false
        }
    }

    @Override
    void addMethod(CtClass clazz) {
        Logger.d("没有trimMemory方法, add>>>>>> ${clazz.name}")
        ReferenceLeakCodeInject.addTrimMemoryCode(clazz)
    }
}
