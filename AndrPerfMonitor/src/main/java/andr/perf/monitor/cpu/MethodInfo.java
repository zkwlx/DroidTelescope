package andr.perf.monitor.cpu;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by ZhouKeWen on 17/3/24.
 */

public class MethodInfo {

    /**
     * 方法用时，使用System.nanoTime()计算
     * 单位ns，1ns = 1 * 1000 * 1000ms
     */
    private long useNanoTime;

    /**
     * CPU线程用时，使用SystemClock.currentThreadTimeMillis()计算
     * 单位ms
     */
    private long useThreadTime;

    /**
     * 方法签名，类似com.Foo.main(String,int)
     * TODO 暂时不细分参数之类的
     */
    private String signature;

    /**
     * 标示方法非正常return返回，比如throw、或者发生Exception
     * 如果true，说明正常return
     */
    private boolean isNormalExit = false;

    /**
     * 方法所在的线程名字
     */
    private String threadName;

    private LinkedList<MethodInfo> invokeTrace = new LinkedList<>();

    public List<MethodInfo> getInvokeTraceList() {
        return invokeTrace;
    }

    public MethodInfo getCurrentMethod() {
        return invokeTrace.peekLast();
    }

    public void addInnerMethod(MethodInfo innerMethod) {
        invokeTrace.addLast(innerMethod);
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public long getUseThreadTime() {
        return useThreadTime;
    }

    public void setUseThreadTime(long useThreadTime) {
        this.useThreadTime = useThreadTime;
    }

    public long getUseNanoTime() {
        return useNanoTime;
    }

    public void setUseNanoTime(long useNanoTime) {
        this.useNanoTime = useNanoTime;
    }

    @Override
    public String toString() {
        return threadName + "---->" + signature + ":nanoTime>" + useNanoTime + " :threadTime>" +
                useThreadTime;
    }

    public boolean isNormalExit() {
        return isNormalExit;
    }

    public void setNormalExit() {
        isNormalExit = true;
    }

    public String getThreadName() {
        return threadName;
    }

    public void setThreadName(String threadName) {
        this.threadName = threadName;
    }
}
