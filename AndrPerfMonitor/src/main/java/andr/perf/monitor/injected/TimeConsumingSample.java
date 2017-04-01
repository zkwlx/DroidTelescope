package andr.perf.monitor.injected;

import android.os.SystemClock;

import andr.perf.monitor.SamplerFactory;

/**
 * 用于代码注入的接口类
 * Created by ZhouKeWen on 17/3/16.
 */
public class TimeConsumingSample {

    public static boolean shouldMonitor() {
        return true;
    }

    public static void methodEnter(String cls, String method, String argTypes) {
        SamplerFactory.getMethodSampler().onMethodEnter(cls, method, argTypes);
    }

    public static void methodExit(long startTimeNano, long startThreadTime, String cls, String method,
            String argTypes) {
        long useNanoTime = System.nanoTime() - startTimeNano;
        long threadTime = SystemClock.currentThreadTimeMillis() - startThreadTime;
        SamplerFactory.getMethodSampler().onMethodExit(useNanoTime, threadTime, cls, method, argTypes);
    }

    public static void methodExitFinally(String cls, String method, String argTypes) {
        SamplerFactory.getMethodSampler().onMethodExitFinally(cls, method, argTypes);
    }

}
