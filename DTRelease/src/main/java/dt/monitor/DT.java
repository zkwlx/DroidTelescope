package dt.monitor;

import android.content.Context;

import java.lang.ref.WeakReference;

import dt.monitor.injected.InteractiveInjected;
import dt.monitor.interactive.UserInteractiveTracing;

/**
 * @author zhoukewen
 * @since 2018/7/6
 */
public class DT {

    public static WeakReference<Context> weakContext;

    /**
     * 初始化 DT 框架
     *
     * @param context
     */
    public static void init(Context context) {
        weakContext = new WeakReference<>(context.getApplicationContext());
        clean();
    }

    /**
     * 设置交互监听器，当记录一条交互事件时回调
     *
     * @param listener
     */
    public static void setInteractiveListener(UserInteractiveTracing.InteractiveListener listener) {
        InteractiveInjected.setInteractiveListener(listener);
    }

    private static void clean() {
        UIEventRecorder.clean();
    }

}
