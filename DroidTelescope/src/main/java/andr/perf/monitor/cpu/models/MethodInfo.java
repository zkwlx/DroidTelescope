package andr.perf.monitor.cpu.models;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by ZhouKeWen on 17/3/24.
 */

public class MethodInfo implements Serializable {

    /**
     * 方法用时，使用System.nanoTime()计算
     * 单位ns，1ns = 1 * 1000 * 1000ms
     */
    private long wallClockTimeNs;

    /**
     * 方法用时，使用{@link #wallClockTimeNs} 换算成ms
     * 单位ms
     */
    private double wallClockTimeMs = -1;

    /**
     * CPU线程用时，使用SystemClock.currentThreadTimeMillis()计算
     * 单位ms
     */
    private long cpuTimeMs;

    /**
     * 方法签名，类似com.Foo.main(String,int)
     */
    private String signature;

    /**
     * 标示方法非正常return返回，比如throw、或者发生Exception
     * 如果true，说明正常return
     */
    private boolean isNormalExit = false;

    /**
     * 方法所在的线程的id
     */
    private long threadId;

    private LinkedList<MethodInfo> invokeTrace;

    /**
     * 返回当前方法的所有子方法，按其调用顺序排序,
     * 注意，可能返回NULL！
     *
     * @return
     */
    public List<MethodInfo> getInvokeTraceList() {
        return invokeTrace;
    }

    public void addInnerMethod(MethodInfo innerMethod) {
        if (invokeTrace == null) {
            invokeTrace = new LinkedList<>();
        }
        invokeTrace.addLast(innerMethod);
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public long getCpuTimeMs() {
        return cpuTimeMs;
    }

    public void setCpuTimeMs(long cpuTimeMs) {
        this.cpuTimeMs = cpuTimeMs;
    }

    public long getWallClockTimeNs() {
        return wallClockTimeNs;
    }

    public void setWallClockTimeNs(long wallClockTimeNs) {
        this.wallClockTimeNs = wallClockTimeNs;
    }

    @Override
    public String toString() {
        return threadId + "---->" + signature + ":nanoTime>" + wallClockTimeNs + " :threadTime>" + cpuTimeMs;
    }

    public boolean isNormalExit() {
        return isNormalExit;
    }

    public void setNormalExit() {
        isNormalExit = true;
    }

    public long getThreadId() {
        return threadId;
    }

    public void setThreadId(long threadId) {
        this.threadId = threadId;
    }

    public double getWallClockTimeMs() {
        //ms的计算延迟到需要时
        if (wallClockTimeMs == -1) {
            //这样除的目的是保留2位有效数字
            wallClockTimeMs = wallClockTimeNs / 10000;
            wallClockTimeMs = wallClockTimeMs / 100.0;
        }
        return wallClockTimeMs;
    }
}
