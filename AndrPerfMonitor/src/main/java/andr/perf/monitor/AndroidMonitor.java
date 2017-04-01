package andr.perf.monitor;

import andr.perf.monitor.cpu.LooperMonitor;
import andr.perf.monitor.cpu.models.BlockInfo;

/**
 * Created by ZhouKeWen on 17/3/24.
 */
public class AndroidMonitor {

    private static BlockListener blockListener;

    private static Config monitorConfig;

    public interface BlockListener {
        void onBlock(BlockInfo blockInfo);
    }

    public static void install() {
        install(new Config());
    }

    public static void install(Config config) {
        monitorConfig = (config == null ? new Config() : config);
        LooperMonitor.getInstance().installLooperListener();
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
