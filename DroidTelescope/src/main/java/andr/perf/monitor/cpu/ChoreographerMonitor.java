package andr.perf.monitor.cpu;

import android.os.SystemClock;
import android.view.Choreographer;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import andr.perf.monitor.Config;
import andr.perf.monitor.DroidTelescope;
import andr.perf.monitor.SamplerFactory;
import andr.perf.monitor.cpu.models.BlockInfo;
import andr.perf.monitor.cpu.models.MethodInfo;
import andr.perf.monitor.reflect_utils.FieldUtils;
import andr.perf.monitor.reflect_utils.MethodUtils;
import andr.perf.monitor.utils.Logger;

/**
 * Created by ZhouKeWen on 2017/6/30.
 */
public class ChoreographerMonitor extends BlockMonitor {

    private static final String TAG = "ChoreographerMonitor";

    private static Config config;

    /**
     * 计算方式：系统默认丢帧数量 * 每帧时间间隔，单位是ns
     */
    private long warningFrameMs;

    private static final int DEFAULT_FRAME_SKIP_WARNING = 30;

    private BlockFrameCallback frameCallback = new BlockFrameCallback();

    @Override
    public void startBlockMonitoring() {
        Choreographer.getInstance().postFrameCallback(frameCallback);
    }

    @Override
    public void stopBlockMonitoring() {
        frameCallback.exit();
        Choreographer.getInstance().removeFrameCallback(frameCallback);
    }

    ChoreographerMonitor() {
        config = DroidTelescope.getConfig();
    }

    private class BlockFrameCallback implements Choreographer.FrameCallback {

        private boolean isExit = false;

        private Choreographer choreographer = Choreographer.getInstance();

        private long startWallClockTimeMs;

        private long startCpuTimeMs;

        BlockFrameCallback() {
            try {
                //获取系统属性：丢失的帧数阈值
                int skipFrame = (int) MethodUtils
                        .invokeStaticMethod(Class.forName("android.os.SystemProperties"), "getInt",
                                "debug.choreographer.skipwarning", DEFAULT_FRAME_SKIP_WARNING);
                //获取设备每帧的时间间隔，单位ns
                long frameIntervalNs =
                        (long) FieldUtils.readField(Choreographer.getInstance(), "mFrameIntervalNanos");
                warningFrameMs = (int) (frameIntervalNs * skipFrame) / 1000000;
            } catch (NoSuchMethodException | IllegalAccessException | ClassNotFoundException | InvocationTargetException e) {
                e.printStackTrace();
                warningFrameMs = 17 * DEFAULT_FRAME_SKIP_WARNING;
            }
            Logger.i(TAG, "[ChoreographerMonitor default warning frame ms: " + warningFrameMs + "]");
            startWallClockTimeMs = System.currentTimeMillis();
            startCpuTimeMs = SystemClock.currentThreadTimeMillis();
        }

        @Override
        public void doFrame(long frameTimeNanos) {
            if (isExit) {
                return;
            }
            choreographer.postFrameCallback(this);

            long wallClockTimeMs = System.currentTimeMillis() - startWallClockTimeMs;
            long cpuTimeMs = SystemClock.currentThreadTimeMillis() - startCpuTimeMs;
            startWallClockTimeMs = System.currentTimeMillis();
            startCpuTimeMs = SystemClock.currentThreadTimeMillis();

            int a = (int) (wallClockTimeMs / 16);
            if (wallClockTimeMs >= warningFrameMs || config.isBlock(wallClockTimeMs, cpuTimeMs)) {
                Logger.e("Oops!!!! choreographer block!!! " + "msTime:" + wallClockTimeMs + " threadTime:" +
                        cpuTimeMs);
                onBlock(wallClockTimeMs, cpuTimeMs);
                Logger.i("zkw", "-------skipped: " + a + " --- wall time:" + wallClockTimeMs);
            }

            //TODO 这里会影响性能，注意优化
            SamplerFactory.getMethodSampler().cleanStack();
        }

        void exit() {
            isExit = true;
        }
    }

}
