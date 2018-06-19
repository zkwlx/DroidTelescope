package andr.perf.monitor.cpu;

import java.util.List;

import andr.perf.monitor.DroidTelescope;
import andr.perf.monitor.SamplerFactory;
import andr.perf.monitor.cpu.models.BlockInfo;
import andr.perf.monitor.cpu.models.MethodInfo;
import andr.perf.monitor.stack_traces.AbstractMethodSampler;
import andr.perf.monitor.stack_traces.DetailedMethodSampler;
import andr.perf.monitor.utils.Logger;

/**
 * Created by ZhouKeWen on 2017/6/30.
 */
public abstract class BlockMonitor {

    private static final String TAG = "BlockMonitor";

    public abstract void startBlockMonitoring();

    public abstract void stopBlockMonitoring();

    void onBlock(long wallClockTimeMs, long cpuTimeMs) {
        //TODO 卡顿监控不能使用 SysTrace 的采样方式！
        List<MethodInfo> methodInfoList = null;
        AbstractMethodSampler methodSampler = SamplerFactory.getMethodSampler();
        if (methodSampler instanceof DetailedMethodSampler) {
            methodInfoList = ((DetailedMethodSampler) methodSampler).getRootMethodList();
        }
        if (methodInfoList == null || methodInfoList.isEmpty()) {
            Logger.i(TAG, "On block, but method list is empty!");
            return;
        }
        BlockInfo blockInfo = new BlockInfo();
        blockInfo.setCpuTimeMs(cpuTimeMs);
        blockInfo.setWallClockTimeMs(wallClockTimeMs);
        blockInfo.setRootMethodList(methodInfoList);
        DroidTelescope.BlockListener listener = DroidTelescope.getBlockListener();
        if (listener != null) {
            listener.onBlock(blockInfo);
        } else {
            Logger.i(TAG, "On block, but listener is null!");
        }
    }

}
