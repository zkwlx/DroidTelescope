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
    private long useNanoTime;

    /**
     * 方法用时，使用{@link #useNanoTime} 换算成ms
     * 单位ms
     */
    private long useMsTime = -1;

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
     * 方法所在的线程的id
     */
    private long threadId;

    private LinkedList<MethodInfo> invokeTrace;

    /**
     * 返回当前方法的所有子方法，按其调用顺序排序
     * <p><b>注意，可能返回NULL!!<b/><p/>
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
        return threadId + "---->" + signature + ":nanoTime>" + useNanoTime + " :threadTime>" + useThreadTime;
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

    public long getUseMsTime() {
        if (useMsTime == -1) {
            //ms的计算延迟到需要时
            this.useMsTime = useNanoTime / 1000000;
        }
        return useMsTime;
    }
}
