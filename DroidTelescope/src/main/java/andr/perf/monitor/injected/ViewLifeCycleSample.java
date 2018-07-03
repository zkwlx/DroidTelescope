package andr.perf.monitor.injected;

import android.view.View;

import andr.perf.monitor.utils.Logger;

/**
 * TODO View 生命周期，测试功能
 *
 * @author zhoukewen
 * @since 2018/6/28
 */
public class ViewLifeCycleSample {

    private static long start;

    public static void onNew(String view) {
        Logger.i("on view new ----------> " + view);
        start = System.currentTimeMillis();
    }

    public static void onFinishInflate(View view) {
        long inflateDuration = System.currentTimeMillis() - start;
        Logger.i("on view Finish Inflate ----------> " + view + ", duration: " + inflateDuration);
    }

    public static void onAttachedToWindow(View view) {
        Logger.i("on view Attached To Window ----------> " + view);
    }

    public static void onDetachedFromWindow(View view) {
        Logger.i("on view Detached From Window ----------> " + view);
    }

}