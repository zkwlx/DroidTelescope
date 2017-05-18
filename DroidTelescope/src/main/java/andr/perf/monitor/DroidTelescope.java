package andr.perf.monitor;

import andr.perf.monitor.cpu.LooperMonitor;
import andr.perf.monitor.cpu.models.BlockInfo;
import andr.perf.monitor.memory.models.LeakInfo;

/**
 * Created by ZhouKeWen on 17/3/24.
 */
public class DroidTelescope {

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
        LooperMonitor.getInstance().installLooperListener();
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
