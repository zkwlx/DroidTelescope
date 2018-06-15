package andr.perf.monitor;

/**
 * Created by ZhouKeWen on 17/3/28.
 */

public class Config {

    /**
     * 根据一次loop的耗时，判断是否发生卡顿。
     * <p><b>注意：尽量不要添加自己实现，因为框架内部根据不同设备动态计算了判断卡顿的阈值</b></p>
     *
     * @param wallClockTimeMs in millisecond
     * @param cpuTimeMs       in millisecond
     * @return true if blocked, else false
     */
    public boolean isBlock(long wallClockTimeMs, long cpuTimeMs) {
        return false;
    }

    /**
     * 判断一个方法是否应该记录。如果方法执行时间很短，则不记录，节省内存
     *
     * @param wallClockTimeNs
     * @param cpuTimeMs
     * @return
     */
    public boolean shouldRecordMethod(long wallClockTimeNs, long cpuTimeMs) {
        return wallClockTimeNs > 1000000 || cpuTimeMs > 1;
    }

    /**
     * 是否使用Choreographer监控器，如果false，则使用Looper监控器
     *
     * @return
     */
    public boolean useChoreographerMonitor() {
        return false;
    }

    /**
     * 记录方法信息时，仅记录ui线程的方法
     *
     * @return
     */
    public boolean justRecordUIThread() {
        return false;
    }

    /**
     * 使用框架自带的耗时收集模块，还是使用 Android 提供的 SysTrace 耗时检测模块
     *
     * @return
     */
    public boolean useSysTrace() {
        return false;
    }

    /**
     * 是否打印 debug 日志
     *
     * @return
     */
    public boolean debugLog() {
        return false;
    }

}
