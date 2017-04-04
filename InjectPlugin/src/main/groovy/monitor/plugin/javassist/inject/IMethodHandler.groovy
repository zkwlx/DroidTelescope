package monitor.plugin.javassist.inject;

import javassist.CtClass;
import javassist.CtMethod;

/**
 * <p>Created by ZhouKeWen on 17-4-4.</p>
 */
public interface IMethodHandler {

    boolean handleMethod(CtClass clazz, CtMethod ctMethod);

    void checkMethodAndAdd(CtClass clazz);
}
