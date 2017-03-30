package andr.perf.monitor;

import android.os.SystemClock;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import andr.perf.monitor.cpu.SamplerFactory;

/**
 * 用于代码注入的接口类
 * Created by ZhouKeWen on 17/3/16.
 */
public class MethodSampler {

    private static final String TAG = "MethodSampler";

    public static boolean shouldMonitor(Object o) {
        Log.i("66666", "thisObject>>>>>>>>>>> " + o);
        return true;
    }

    public static boolean shouldMonitor() {
        return true;
    }

    public static void methodEnter(String cls, String method, String argTypes) {
        SamplerFactory.getSampler().onMethodEnter(cls, method, argTypes);
    }

    public static void methodExit(long startTimeNano, long startThreadTime, String cls, String method,
            String argTypes) {
        long useNanoTime = System.nanoTime() - startTimeNano;
        long threadTime = SystemClock.currentThreadTimeMillis() - startThreadTime;
        SamplerFactory.getSampler().onMethodExit(useNanoTime, threadTime, cls, method, argTypes);
    }

    public static void methodExitFinally(String cls, String method, String argTypes) {
        SamplerFactory.getSampler().onMethodExitFinally(cls, method, argTypes);
    }

}
