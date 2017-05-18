package monitor.plugin.javassist.inject;

import javassist.CtClass
import monitor.plugin.javassist.inject.interactive.DialogOnClickHandler
import monitor.plugin.javassist.inject.interactive.IInterfaceHandler
import monitor.plugin.javassist.inject.interactive.ViewOnClickHandler
import monitor.plugin.javassist.inject.interactive.ViewOnLongClickHandler
import monitor.plugin.utils.LogUtils

/**
 * 用户交互行为监控模块的代码注入器
 * Created by ZhouKeWen on 2017/5/11.
 */
public class InteractiveCodeInject {

    private final static HashMap<String, IInterfaceHandler> handlerMap;

    static {
        handlerMap = new HashMap<>()
        handlerMap.put(ViewOnClickHandler.NAME, new ViewOnClickHandler())
        handlerMap.put(ViewOnLongClickHandler.NAME, new ViewOnLongClickHandler())
        handlerMap.put(DialogOnClickHandler.NAME, new DialogOnClickHandler())
    }

    public static void injectForViewClick(CtClass clazz) {
        CtClass[] interfaces = clazz.interfaces;
        if (interfaces == null || interfaces.length == 0) {
            return;
        }

        for (CtClass face : interfaces) {
            LogUtils.printLog("--------interface name: " + face.name)
            IInterfaceHandler handler = handlerMap.get(face.name)
            if (handler != null) {
                handler.handleInterface(clazz)
            }
        }
    }


}
