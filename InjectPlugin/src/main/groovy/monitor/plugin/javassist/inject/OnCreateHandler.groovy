package monitor.plugin.javassist.inject;

import javassist.CtClass;
import javassist.CtMethod
import monitor.plugin.javassist.JavassistHandler;

/**
 * <p>Created by ZhouKeWen on 17-4-4.</p>
 */
public class OnCreateHandler implements IMethodHandler {

    private CtClass bundleClass
    private boolean hasCreateMethod = false

    public OnCreateHandler() {
        bundleClass = JavassistHandler.makeClass("android.os.Bundle")
    }

    @Override
    public boolean handleMethod(CtClass clazz, CtMethod ctMethod) {
        if ("onCreate" == ctMethod.name && ctMethod.parameterTypes.size() == 1 &&
                ctMethod.parameterTypes[0] == bundleClass) {
            MemoryCodeInject.insertCreateSampleCode(clazz, ctMethod)
            hasCreateMethod = true
            return true
        } else {
            return false
        }

    }

    @Override
    public void checkMethodAndAdd(CtClass clazz) {
        if (hasCreateMethod) {
            //重置状态
            hasCreateMethod = false
        } else {
            System.out.println("没有create方法！！！！！>>>>>> ${clazz.name}")
            MemoryCodeInject.addObjectCreateMethod(clazz)
        }
    }
}
