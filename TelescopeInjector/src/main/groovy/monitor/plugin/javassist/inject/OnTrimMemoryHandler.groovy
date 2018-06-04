package monitor.plugin.javassist.inject

import javassist.CtClass
import javassist.CtMethod
import monitor.plugin.utils.Logger

/**
 * <p>Created by ZhouKeWen on 17-4-4.</p>
 */
class OnTrimMemoryHandler implements IMethodHandler {

    private boolean hasTrimMemoryMethod = false

    @Override
    boolean handleMethod(CtClass clazz, CtMethod ctMethod) {
        if ("onTrimMemory" == ctMethod.name && ctMethod.parameterTypes.size() == 1 &&
                ctMethod.parameterTypes[0] == CtClass.intType) {
            MemoryCodeInject.insertTrimMemoryCode(clazz, ctMethod)
            hasTrimMemoryMethod = true
            return true
        } else {
            return false
        }
    }

    @Override
    void checkMethodAndAdd(CtClass clazz) {
        if (hasTrimMemoryMethod) {
            hasTrimMemoryMethod = false
        } else {
            Logger.i("没有trimMemory方法？？？？>>>>>> ${clazz.name}")
            MemoryCodeInject.addTrimMemoryCode(clazz)
        }
    }
}
