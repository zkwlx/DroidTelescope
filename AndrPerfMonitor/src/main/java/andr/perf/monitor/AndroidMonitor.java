package andr.perf.monitor;

import android.content.Context;

import andr.perf.monitor.cpu.CpuMonitor;

/**
 * Created by ZhouKeWen on 17/3/24.
 */

public class AndroidMonitor {

    public static void install(Context context) {
        CpuMonitor.getInstance().installLooperListener();
    }

}
