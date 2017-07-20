package andr.perf.monitor.cpu;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.support.annotation.Nullable;

import andr.perf.monitor.Config;
import andr.perf.monitor.DroidTelescope;

/**
 * Created by ZhouKeWen on 2017/7/5.
 */
public class BlockMonitorManager {

    private static BlockMonitor looperMonitor;
    private static BlockMonitor choreographerMonitor;

    /**
     * @param context
     * @return
     */
    public static BlockMonitor getMonitor(Context context) {
        Config config = DroidTelescope.getConfig();
        if (isDebug(context) && config.useChoreographerMonitor()) {
            return getMonitor(DroidTelescope.BLOCK_MONITOR_CHOREOGRAPHER);
        } else {
            return getMonitor(DroidTelescope.BLOCK_MONITOR_LOOPER);
        }
    }

    /**
     * 根据type返回对应的BlockMonitor实现，如果没有对应type，则返回Null
     *
     * @param type
     * @return
     */
    @Nullable
    public static BlockMonitor getMonitor(int type) {
        BlockMonitor monitor;
        switch (type) {
            case DroidTelescope.BLOCK_MONITOR_LOOPER:
                if (choreographerMonitor == null) {
                    choreographerMonitor = new ChoreographerMonitor();
                }
                monitor = choreographerMonitor;
                break;
            case DroidTelescope.BLOCK_MONITOR_CHOREOGRAPHER:
                if (looperMonitor == null) {
                    looperMonitor = new LooperMonitor();
                }
                monitor = looperMonitor;
                break;
            default:
                monitor = null;
                break;
        }
        return monitor;
    }

    private static boolean isDebug(Context context) {
        if (context == null) {
            return true;
        }
        ApplicationInfo info = context.getApplicationInfo();
        return (info.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
    }

}
