package monitor.plugin.javassist.inject.interactive

import javassist.CtClass

/**
 * interface的处理接口，用于interface的方法注入
 * Created by ZhouKeWen on 2017/5/16.
 */
public interface IInterfaceHandler {

    boolean handleInterface(CtClass clazz)

}
