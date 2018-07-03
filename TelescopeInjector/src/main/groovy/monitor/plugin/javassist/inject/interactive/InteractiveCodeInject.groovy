package monitor.plugin.javassist.inject.interactive

import javassist.CtClass
import monitor.plugin.utils.Logger

/**
 * 用户交互行为监控模块的代码注入器
 * Created by ZhouKeWen on 2017/5/11.
 */
class InteractiveCodeInject {

    private final static HashMap<String, IInterfaceHandler> handlerMap

    static {
        handlerMap = new HashMap<>()
        handlerMap.put(ViewOnClickHandler.NAME, new ViewOnClickHandler())
        handlerMap.put(ViewOnLongClickHandler.NAME, new ViewOnLongClickHandler())
        handlerMap.put(DialogOnClickHandler.NAME, new DialogOnClickHandler())
        handlerMap.put(ItemOnClickHandler.NAME, new ItemOnClickHandler())
        handlerMap.put(ItemOnLongClickHandler.NAME, new ItemOnLongClickHandler())
        handlerMap.put(ItemOnSelectedHandler.NAME, new ItemOnSelectedHandler())
    }

    static void injectForViewEvent(CtClass clazz) {
        CtClass[] interfaces = clazz.interfaces
        if (interfaces == null || interfaces.length == 0) {
            return
        }

        for (CtClass face : interfaces) {
            Logger.i("--------interface name: " + face.name)
            IInterfaceHandler handler = handlerMap.get(face.name)
            if (handler != null) {
                handler.handleInterface(clazz)
            }
        }
    }


}
