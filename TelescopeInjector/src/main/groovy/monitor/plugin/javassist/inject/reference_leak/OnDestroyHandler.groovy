package monitor.plugin.javassist.inject.reference_leak

import javassist.CtClass
import javassist.CtMethod
import monitor.plugin.utils.Logger

/**
 * <p>Created by ZhouKeWen on 17-4-4.</p>
 */
class OnDestroyHandler implements IMethodHandler {

    @Override
    boolean modifyMethod(CtClass clazz, CtMethod ctMethod) {
        if ("onDestroy" == ctMethod.name && ctMethod.parameterTypes.size() == 0) {
            ReferenceLeakCodeInject.insertDestroySampleCode(clazz, ctMethod)
            return true
        } else {
            return false
        }
    }

    @Override
    void addMethod(CtClass clazz) {
        Logger.d("没有destroy方法，add>>>>>> ${clazz.name}")
        ReferenceLeakCodeInject.addObjectDestroyMethod(clazz)
    }
}
