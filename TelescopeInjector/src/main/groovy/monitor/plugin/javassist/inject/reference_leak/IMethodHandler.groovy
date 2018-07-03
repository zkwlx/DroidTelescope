package monitor.plugin.javassist.inject.reference_leak;

import javassist.CtClass;
import javassist.CtMethod;

/**
 * <p>Created by ZhouKeWen on 17-4-4.</p>
 */
interface IMethodHandler {

    /**
     * 修改方法
     * @param clazz 方法所在类
     * @param ctMethod
     * @return 如果方法存在并修改成功返回 true，如果方法不存在则返回 false
     */
    boolean modifyMethod(CtClass clazz, CtMethod ctMethod)

    /**
     * 向类中添加新方法
     * @param clazz
     */
    void addMethod(CtClass clazz)

}
