package andr.perf.monitor;

import android.os.SystemClock;
import android.util.Log;

/**
 * Created by ZhouKeWen on 17/3/16.
 */

public class CpuMonitor {

    private static final String TAG = "CpuMonitor";

    public static boolean shouldMonitor() {
        return true;
    }

    public static void startMethodMonitor(long startTimeNano, long startThreadTime, String cls, String method,
            String argTypes) {
        long useNanoTime = System.nanoTime() - startTimeNano;
        Log.i(TAG, "------->" + cls + "." + method + "(" + argTypes + ")" + " [[[useTime:" + useNanoTime);
    }

}
