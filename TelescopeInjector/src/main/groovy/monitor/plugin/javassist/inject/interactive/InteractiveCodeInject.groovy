package monitor.plugin.javassist.inject.interactive

import javassist.CtClass
import monitor.plugin.ConfigProvider
import monitor.plugin.utils.JavassistUtils
import monitor.plugin.utils.Logger

/**
 * 用户交互行为监控模块的代码注入器
 * Created by ZhouKeWen on 2017/5/11.
 */
class InteractiveCodeInject {

    private final static HashMap<String, IInterfaceHandler> handlerMap

    //TODO 要做的，在做之前要看看是否有继承关系，避免重复监控
    //TODO android.content.DialogInterface$OnKeyListener
    //TODO android.view.View$OnKeyListener
    //TODO android.view.MenuItem$OnMenuItemClickListener
    //TODO android.support.v7.widget.RecyclerView$OnItemTouchListener
    //TODO ?? android.widget.AbsListView$OnScrollListener
    //TODO ?? android.view.View$OnTouchListener
    //TODO android.support.v7.widget.ActionMenuView$OnMenuItemClickListener

    //TODO Activity 的交互在 app 的 BaseActivity 里写

    static {
        String className
        if (ConfigProvider.config.forRelease) {
            className = "dt.monitor.injected.InteractiveInjected"
        } else {
            className = "andr.perf.monitor.injected.InteractiveInjected"
        }
        handlerMap = new HashMap<>()
        handlerMap.put(ViewOnClickHandler.NAME, new ViewOnClickHandler(className))
        handlerMap.put(ViewOnLongClickHandler.NAME, new ViewOnLongClickHandler(className))
        handlerMap.put(DialogOnClickHandler.NAME, new DialogOnClickHandler(className))
        handlerMap.put(ItemOnClickHandler.NAME, new ItemOnClickHandler(className))
        handlerMap.put(ItemOnLongClickHandler.NAME, new ItemOnLongClickHandler(className))
        handlerMap.put(ItemOnSelectedHandler.NAME, new ItemOnSelectedHandler(className))
    }

    static void injectForViewEvent(CtClass clazz) {
        CtClass[] interfaces = JavassistUtils.getInterfaces(clazz)
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
