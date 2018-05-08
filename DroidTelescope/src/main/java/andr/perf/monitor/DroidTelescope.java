package andr.perf.monitor;

import android.content.Context;
import android.os.SystemClock;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import andr.perf.monitor.cpu.BlockMonitor;
import andr.perf.monitor.cpu.BlockMonitorManager;
import andr.perf.monitor.cpu.models.BlockInfo;
import andr.perf.monitor.cpu.models.MethodInfo;
import andr.perf.monitor.memory.models.LeakInfo;
import andr.perf.monitor.persist.ConvertUtils;
import andr.perf.monitor.stack_traces.TracesMonitor;
import andr.perf.monitor.utils.Logger;

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

    public static void install() {
        install(new Config());
    }

    public static void install(Config config) {
        monitorConfig = (config == null ? new Config() : config);
    }

    public static void startBlockMonitor() {
        if (monitorConfig == null) {
            return;
        }
        BlockMonitor monitor = BlockMonitorManager.getMonitor(monitorConfig);
        if (monitor != null) {
            monitor.startBlockMonitoring();
        }
    }

    public static void stopBlockMonitor() {
        if (monitorConfig == null) {
            return;
        }
        BlockMonitor monitor = BlockMonitorManager.getMonitor(monitorConfig);
        if (monitor != null) {
            monitor.stopBlockMonitoring();
        }
    }

    public static void startMethodTracing() {
        TracesMonitor.startTracing();
    }

    public static JSONObject stopMethodTracing() {
        return TracesMonitor.stopTracing();
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
