package andr.perf.monitor.cpu;

import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;
import android.util.Printer;
import android.view.Choreographer;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import andr.perf.monitor.DroidTelescope;
import andr.perf.monitor.Config;
import andr.perf.monitor.SamplerFactory;
import andr.perf.monitor.cpu.models.BlockInfo;
import andr.perf.monitor.cpu.models.MethodInfo;
import andr.perf.monitor.reflect_utils.FieldUtils;
import andr.perf.monitor.reflect_utils.MethodUtils;
import andr.perf.monitor.utils.Logger;

/**
 * Looper监听类，监控每次loop的耗时
 * Created by ZhouKeWen on 17/3/24.
 */
public class LooperMonitor {

    private static final String TAG = "LooperMonitor";

    private static Config config;

    public static LooperMonitor getInstance() {
        return SingletonHolder.instance;
    }

    private static class SingletonHolder {
        private static LooperMonitor instance = new LooperMonitor();
    }

    private LooperMonitor() {
        config = DroidTelescope.getConfig();
    }

    private final InnerBlockListener innerBlockListener = new InnerBlockListener() {
        @Override
        public void onBlock(long wallClockTimeMs, long cpuTimeMs) {
            Logger.e(TAG, "Oops!!!! block!!! " + "msTime:" + wallClockTimeMs + " threadTime:" + cpuTimeMs);
            List<MethodInfo> methodInfoList = SamplerFactory.getMethodSampler().getRootMethodList();
            if (methodInfoList.isEmpty()) {
                return;
            }
            BlockInfo blockInfo = new BlockInfo();
            blockInfo.setCpuTimeMs(cpuTimeMs);
            blockInfo.setWallClockTimeMs(wallClockTimeMs);
            blockInfo.setRootMethodList(methodInfoList);
            DroidTelescope.BlockListener listener = DroidTelescope.getBlockListener();
            if (listener != null) {
                listener.onBlock(blockInfo);
            }
        }
    };

    //TODO 注意使用范围，目前是进程全局的
    private final Printer looperListener = new Printer() {

        private long startWallClockTimeMs;

        private long startCpuTimeMs;

        private boolean isStart = true;

        @Override
        public void println(String x) {
            if (isStart) {
                //                Log.i("zkw", "----------------loop start-----------------");
                isStart = false;
                startWallClockTimeMs = System.currentTimeMillis();
                startCpuTimeMs = SystemClock.currentThreadTimeMillis();
            } else {
                //                Log.i("zkw", "----------------loop end-----------------");
                isStart = true;
                long wallClockTimeMs = System.currentTimeMillis() - startWallClockTimeMs;
                long cpuTimeMs = SystemClock.currentThreadTimeMillis() - startCpuTimeMs;

                if (wallClockTimeMs >= warningFrameMs || config.isBlock(wallClockTimeMs, cpuTimeMs)) {
                    innerBlockListener.onBlock(wallClockTimeMs, cpuTimeMs);
                    int a = (int) (wallClockTimeMs / 16);
                    Logger.i("zkw", "-------skipped: " + a + " --- wall time:" + wallClockTimeMs);
                }

                //TODO 这里会影响性能，注意优化
                SamplerFactory.getMethodSampler().cleanRootMethodList();
            }
        }
    };

    /**
     * 计算方式：系统默认丢帧数量 * 每帧时间间隔 / 1000000
     */
    private long warningFrameMs;

    private static final int DEFAULT_FRAME_SKIP_WARNING = 30;

    public void installLooperListener() {
        //TODO 注意Looper的选择，是否考虑其他线程的looper
        Looper.getMainLooper().setMessageLogging(looperListener);
        try {
            //获取系统属性：丢失的帧数阈值
            int skipFrame = (int) MethodUtils
                    .invokeStaticMethod(Class.forName("android.os.SystemProperties"), "getInt",
                            "debug.choreographer.skipwarning", DEFAULT_FRAME_SKIP_WARNING);
            //获取设备每帧的时间间隔，单位ns
            long frameIntervalNs =
                    (long) FieldUtils.readField(Choreographer.getInstance(), "mFrameIntervalNanos");
            warningFrameMs = (int) (frameIntervalNs * skipFrame / 1000000);
        } catch (NoSuchMethodException | IllegalAccessException | ClassNotFoundException | InvocationTargetException e) {
            e.printStackTrace();
            warningFrameMs = 16 * DEFAULT_FRAME_SKIP_WARNING;
        }
    }

    private interface InnerBlockListener {
        void onBlock(long wallClockTimeNs, long useThreadTime);
    }
}
