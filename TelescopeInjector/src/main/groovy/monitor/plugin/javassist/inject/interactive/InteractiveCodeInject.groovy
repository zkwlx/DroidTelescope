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

    // 目前支持的 Listener 列表
    // android.view.View$OnClickListener
    // android.view.View$OnLongClickListener
    // android.content.DialogInterface$OnClickListener
    // android.widget.AdapterView$OnItemClickListener
    // android.widget.AdapterView$OnItemLongClickListener
    // android.widget.AdapterView$OnItemSelectedListener
    // android.support.v4.view.ViewPager$OnPageChangeListener 中的两个不耗时方法
    // android.support.v7.widget.PopupMenu$OnMenuItemClickListener
    // android.widget.PopupMenu$OnMenuItemClickListener
    // android.support.v7.widget.Toolbar$OnMenuItemClickListener
    // android.view.MenuItem$OnMenuItemClickListener
    // android.support.v4.widget.SwipeRefreshLayout$OnRefreshListener
    // android.widget.CompoundButton$OnCheckedChangeListener
    // android.support.design.widget.TabLayout$OnTabSelectedListener
    // android.content.DialogInterface$OnKeyListener 只监听 Key Up 的所有事件
    // android.view.View$OnTouchListener 只监听 Up 事件

    // 有需求再加
    // android.view.GestureDetector$OnDoubleTapListener 中的两个不耗时方法
    // android.support.v7.preference.Preference$OnPreferenceClickListener

    //TODO Activity 的交互在 app 的 BaseActivity 里写，包括 onBackPress

    static {
        String className
        handlerMap = new HashMap<>()
        if (ConfigProvider.config.forRelease) {
            className = "dt.monitor.injected.InteractiveInjected"
            handlerMap.put(ViewOnClickHandler.NAME, new ViewOnClickHandler(className))
            handlerMap.put(ViewOnLongClickHandler.NAME, new ViewOnLongClickHandler(className))
            handlerMap.put(DialogOnClickHandler.NAME, new DialogOnClickHandler(className))
            handlerMap.put(ItemOnClickHandler.NAME, new ItemOnClickHandler(className))
            handlerMap.put(ItemOnLongClickHandler.NAME, new ItemOnLongClickHandler(className))
            handlerMap.put(ItemOnSelectedHandler.NAME, new ItemOnSelectedHandler(className))
            handlerMap.put(CompoundButtonCheckedHandler.NAME, new CompoundButtonCheckedHandler(className))
            handlerMap.put(ViewPagerOnPageChangeHandler.NAME, new ViewPagerOnPageChangeHandler(className))
            handlerMap.put(MenuItemClickHandler.NAME1, new MenuItemClickHandler(className))
            handlerMap.put(MenuItemClickHandler.NAME2, new MenuItemClickHandler(className))
            handlerMap.put(MenuItemClickHandler.NAME3, new MenuItemClickHandler(className))
            handlerMap.put(MenuItemClickHandler.NAME4, new MenuItemClickHandler(className))
            handlerMap.put(SwipeRefreshHandler.NAME, new SwipeRefreshHandler(className))
            handlerMap.put(TabSelectedHandler.NAME, new TabSelectedHandler(className))
            handlerMap.put(DialogOnKeyHandler.NAME, new DialogOnKeyHandler(className))
            handlerMap.put(ViewOnTouchHandler.NAME, new ViewOnTouchHandler(className))
        } else {
//            className = "andr.perf.monitor.injected.InteractiveInjected"
            //TODO 非 Release 包暂时关闭交互监控功能
        }

    }

    static void injectForViewEvent(CtClass clazz) {
        CtClass[] interfaces = JavassistUtils.getInterfaces(clazz)
        if (interfaces == null || interfaces.length == 0) {
            return
        }

        for (CtClass face : interfaces) {
//            recordListeners(face.name)
            IInterfaceHandler handler = handlerMap.get(face.name)
            if (handler != null) {
                handler.handleInterface(clazz)
            }
        }
    }

    private static HashMap<String, Integer> interfaceMap = new HashMap<>()

    /**
     * 查看项目使用了哪些有用的 listener，以便更全的注入代码
     * @param name
     */
    private static void recordListeners(String name) {
        if (name.startsWith("android") && name.endsWith("Listener")) {
            synchronized (InteractiveCodeInject) {
                if (interfaceMap.containsKey(name)) {
                    int count = interfaceMap.get(name) + 1
                    interfaceMap.put(name, count)
                } else {
                    interfaceMap.put(name, 1)
                }
            }
        }
    }

    /**
     * 查看项目使用了哪些有用的 listener，以便更全的注入代码
     * @param name
     */
    static void showListeners() {
        Logger.i("===================================")
        Logger.i(interfaceMap.toMapString())
    }


}
