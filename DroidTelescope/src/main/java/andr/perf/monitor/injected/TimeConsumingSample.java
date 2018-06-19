package andr.perf.monitor.injected;

import andr.perf.monitor.DroidTelescope;
import andr.perf.monitor.SamplerFactory;

/**
 * 耗时采样类，用于代码注入的接口类
 * Created by ZhouKeWen on 17/3/16.
 */
public class TimeConsumingSample {

//    private static ThreadLocal<Boolean> threadLocal = new ThreadLocal<>();

    private static volatile boolean isInited = false;

    public static boolean shouldMonitor() {
        return true;
    }

    public static void methodEnter(String cls, String method, String argTypes) {
        if (!isInited) {
            if (DroidTelescope.getConfig() == null) {
                //未初始化完，直接退出
//            threadLocal.set(false);
                isInited = false;
                return;
            } else {
                isInited = true;
//            threadLocal.set(true);
            }
        }
        SamplerFactory.getMethodSampler().onMethodEnter(cls, method, argTypes);
    }

    public static void methodExit(String cls, String method, String argTypes) {
        if (!isInited) {
            return;
        }
        SamplerFactory.getMethodSampler().onMethodExit(cls, method, argTypes);
    }

    public static void methodExitFinally(String cls, String method, String argTypes) {
        if (!isInited) {
            return;
        }
        SamplerFactory.getMethodSampler().onMethodExitFinally(cls, method, argTypes);
    }

}
