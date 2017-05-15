package andr.perf.monitor.injected;

import android.os.SystemClock;

import andr.perf.monitor.SamplerFactory;

/**
 * 耗时采样类，用于代码注入的接口类
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
        long wallClockTimeNs = System.nanoTime() - startTimeNano;
        long cpuTimeMs = SystemClock.currentThreadTimeMillis() - startThreadTime;
        SamplerFactory.getMethodSampler().onMethodExit(wallClockTimeNs, cpuTimeMs, cls, method, argTypes);
    }

    public static void methodExitFinally(String cls, String method, String argTypes) {
        SamplerFactory.getMethodSampler().onMethodExitFinally(cls, method, argTypes);
    }

}
