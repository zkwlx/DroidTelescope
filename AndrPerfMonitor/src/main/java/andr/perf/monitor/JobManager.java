package andr.perf.monitor;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;

/**
 * Created by ZhouKeWen on 17/4/1.
 */
public class JobManager {

    private HandlerThread workerThread = new HandlerThread("andr_perf_monitor_thread");

    private Handler workerThreadHandler;
    private Handler mainThreadHandler;

    private static class InstanceHolder {
        static JobManager instance = new JobManager();
    }

    public static JobManager getInstance() {
        return InstanceHolder.instance;
    }

    private JobManager() {
        workerThread.start();
        workerThreadHandler = new Handler(workerThread.getLooper());
        mainThreadHandler = new Handler(Looper.getMainLooper());
    }

    public void postWorkerThread(Runnable runnable) {
        workerThreadHandler.post(runnable);
    }

    public void postDelayWorkerThread(Runnable runnable, long delayMillis) {
        workerThreadHandler.postDelayed(runnable, delayMillis);
    }

    public void postMainThread(Runnable runnable) {
        mainThreadHandler.post(runnable);
    }

    public void postDelayMainThread(Runnable runnable, long delayMillis) {
        mainThreadHandler.postDelayed(runnable, delayMillis);
    }

    public void exitMonitor() {
        workerThread.quitSafely();
    }

}
