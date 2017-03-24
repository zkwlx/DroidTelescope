package andr.perf.monitor.cpu;

import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;
import android.util.Printer;

import java.util.List;

import andr.perf.monitor.persist.InfoStorage;

/**
 * Created by ZhouKeWen on 17/3/24.
 */

public class CpuMonitor {

    private static final String TAG = "CpuMonitor";

    private static final long THRESHOLD_NANO = 500000000;//500ms
    private static final long THRESHOLD_MS = THRESHOLD_NANO / 1000000;

    private static final CpuMonitor ourInstance = new CpuMonitor();

    private final BlockListener blockListener = new BlockListener() {
        @Override
        public void onBlock() {
            Log.e(TAG, "Oops!!!! block!!!");
            List<MethodInfo> methodInfoList = MethodSampleManager.getInstance().getMethodInfoList();
            InfoStorage.onStorageForMethod(methodInfoList);
        }
    };

    //TODO 注意使用范围，目前是进程全局的
    private final Printer LOOPER_LISTENER = new Printer() {

        private long startNanoTime;

        private long startThreadTime;

        private boolean isStart = true;

        @Override
        public void println(String x) {
            if (isStart) {
                isStart = false;
                startNanoTime = System.nanoTime();
                startThreadTime = SystemClock.currentThreadTimeMillis();
            } else {
                isStart = true;
                long useNanoTime = System.nanoTime() - startNanoTime;
                long useThreadTime = SystemClock.currentThreadTimeMillis() - startThreadTime;
                Log.i(TAG, "handle nanoTime:" + useNanoTime + " threadTime:" + useThreadTime);
                if (useNanoTime > THRESHOLD_NANO || useThreadTime > THRESHOLD_MS) {
                    blockListener.onBlock();
                }
                //TODO 这里会影响性能，注意，考虑idle handler
                MethodSampleManager.getInstance().clean();
            }
        }
    };

    public static CpuMonitor getInstance() {
        return ourInstance;
    }

    private CpuMonitor() {

    }

    public void installLooperListener() {
        //TODO 注意Looper的选择，是否考虑子线程的looper
        Looper.getMainLooper().setMessageLogging(LOOPER_LISTENER);
    }

    public interface BlockListener {
        void onBlock();
    }
}
