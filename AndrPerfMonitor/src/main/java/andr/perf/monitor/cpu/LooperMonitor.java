package andr.perf.monitor.cpu;

import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;
import android.util.Printer;

import java.util.List;

import andr.perf.monitor.AndroidMonitor;
import andr.perf.monitor.Config;
import andr.perf.monitor.SamplerFactory;
import andr.perf.monitor.cpu.models.BlockInfo;
import andr.perf.monitor.cpu.models.MethodInfo;

/**
 * Created by ZhouKeWen on 17/3/24.
 */
public class LooperMonitor {

    private static final String TAG = "LooperMonitor";

    private static final long THRESHOLD_MS = 100;//100ms
    private static final long THRESHOLD_THREAD_MS = THRESHOLD_MS;

    private static Config config;

    public static LooperMonitor getInstance() {
        return SingletonHolder.instance;
    }

    private static class SingletonHolder {
        private static LooperMonitor instance = new LooperMonitor();
    }

    private LooperMonitor() {
        config = AndroidMonitor.getConfig();
    }

    private final InnerBlockListener innerBlockListener = new InnerBlockListener() {
        @Override
        public void onBlock(long useMsTime, long useThreadTime) {
            Log.e(TAG, "Oops!!!! block!!!___" + "msTime:" + useMsTime + " threadTime:" +
                    useThreadTime);
            List<MethodInfo> methodInfoList = SamplerFactory.getMethodSampler().getRootMethodList();
            BlockInfo blockInfo = new BlockInfo();
            blockInfo.setUseThreadTime(useThreadTime);
            blockInfo.setUseMsTime(useMsTime);
            blockInfo.setRootMethodList(methodInfoList);
            AndroidMonitor.BlockListener listener = AndroidMonitor.getBlockListener();
            if (listener != null) {
                listener.onBlock(blockInfo);
            }
        }
    };

    //TODO 注意使用范围，目前是进程全局的
    private final Printer looperListener = new Printer() {

        private long startMsTime;

        private long startThreadTime;

        private boolean isStart = true;

        @Override
        public void println(String x) {
            if (isStart) {
                isStart = false;
                startMsTime = System.currentTimeMillis();
                startThreadTime = SystemClock.currentThreadTimeMillis();
            } else {
                isStart = true;
                long useMsTime = System.currentTimeMillis() - startMsTime;
                long useThreadTime = SystemClock.currentThreadTimeMillis() - startThreadTime;
                if (config.isBlock(useMsTime, useThreadTime)) {
                    innerBlockListener.onBlock(useMsTime, useThreadTime);
                }
                //TODO 这里会影响性能，注意，考虑idle handler
                SamplerFactory.getMethodSampler().cleanRootMethodList();
            }
        }
    };

    public void installLooperListener() {
        //TODO 注意Looper的选择，是否考虑子线程的looper
        Looper.getMainLooper().setMessageLogging(looperListener);
    }

    public interface InnerBlockListener {
        void onBlock(long useNanoTime, long useThreadTime);
    }
}
