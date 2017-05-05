package andr.perf.monitor.cpu.models;

import java.util.List;

/**
 * Created by ZhouKeWen on 17/3/28.
 */
public class BlockInfo {

    private long wallClockTimeMs;

    private long cpuTimeMs;

    private List<MethodInfo> rootMethodList;

    public List<MethodInfo> getRootMethodList() {
        return rootMethodList;
    }

    public void setRootMethodList(List<MethodInfo> rootMethodList) {
        this.rootMethodList = rootMethodList;
    }

    public long getCpuTimeMs() {
        return cpuTimeMs;
    }

    public void setCpuTimeMs(long cpuTimeMs) {
        this.cpuTimeMs = cpuTimeMs;
    }

    public long getWallClockTimeMs() {
        return wallClockTimeMs;
    }

    public void setWallClockTimeMs(long wallClockTimeMs) {
        this.wallClockTimeMs = wallClockTimeMs;
    }
}
