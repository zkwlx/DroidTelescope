package andr.perf.monitor;

import android.content.Context;

import andr.perf.monitor.cpu.BlockMonitor;
import andr.perf.monitor.cpu.BlockMonitorManager;
import andr.perf.monitor.cpu.models.BlockInfo;
import andr.perf.monitor.memory.models.LeakInfo;

/**
 * Created by ZhouKeWen on 17/3/24.
 */
public class DroidTelescope {

    public static final int BLOCK_MONITOR_LOOPER = 1;
    public static final int BLOCK_MONITOR_CHOREOGRAPHER = 2;

    private static BlockListener blockListener;

    private static LeakListener leakListener;

    private static Config monitorConfig;

    public interface BlockListener {
        void onBlock(BlockInfo blockInfo);
    }

    public interface LeakListener {
        void onLeak(LeakInfo leakInfo);
    }

    public static void install(Context context) {
        install(context, new Config());
    }

    public static void install(Context context, Config config) {
        monitorConfig = (config == null ? new Config() : config);
        BlockMonitorManager.getMonitor(context).startBlockMonitoring();
    }

    public static void startBlockMonitor(int monitorType) {
        BlockMonitor monitor = BlockMonitorManager.getMonitor(monitorType);
        if (monitor != null) {
            monitor.startBlockMonitoring();
        }
    }

    public static void stopBlockMonitor(int monitorType) {
        BlockMonitor monitor = BlockMonitorManager.getMonitor(monitorType);
        if (monitor != null) {
            monitor.stopBlockMonitoring();
        }
    }

    public static LeakListener getLeakListener() {
        return leakListener;
    }

    public static void setLeakListener(LeakListener leakListener) {
        DroidTelescope.leakListener = leakListener;
    }

    public static void setBlockListener(BlockListener listener) {
        blockListener = listener;
    }

    public static Config getConfig() {
        return monitorConfig;
    }

    public static BlockListener getBlockListener() {
        return blockListener;
    }

}
