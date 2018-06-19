package andr.perf.monitor.cpu.models;

import android.os.SystemClock;
import android.text.TextUtils;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import andr.perf.monitor.utils.Logger;

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

    private String className;

    private String methodName;

    private String argTypes;

    /**
     * 标示方法非正常return返回，比如throw、或者发生Exception
     * 如果true，说明正常return
     * TODO 当使用对象池时，要初始化！
     */
    private boolean isNormalExit = false;

    /**
     * 方法所在的线程的id
     */
    private long threadId;

    /**
     * 方法所在的线程名
     */
    private String threadName;

    private LinkedList<MethodInfo> invokeTrace;

    /**
     * 记录开始的时间戳
     */
    private long startNanoTime;
    private long startThreadMsTime;

    /**
     * 返回当前方法的所有子方法，按其调用顺序排序,
     * 注意，可能返回NULL！
     *
     * @return
     */
    public List<MethodInfo> getInvokeTraceList() {
        return invokeTrace;
    }

    public void startTimestamp() {
        startNanoTime = java.lang.System.nanoTime();
        startThreadMsTime = SystemClock.currentThreadTimeMillis();
    }

    public void calculateDuration() {
        if (startNanoTime == 0 || startThreadMsTime == 0) {
            Logger.e("On calculateDuration(), times is 0! method: " + signature);
        }
        this.wallClockTimeNs = System.nanoTime() - startNanoTime;
        this.cpuTimeMs = SystemClock.currentThreadTimeMillis() - startThreadMsTime;
    }

    public void addInnerMethod(MethodInfo innerMethod) {
        if (invokeTrace == null) {
            invokeTrace = new LinkedList<>();
        }
        invokeTrace.addLast(innerMethod);
    }

    public String getSignature() {
        if (TextUtils.isEmpty(signature)) {
            signature = createSignature(className, methodName, argTypes);
        }
        //TODO 当使用对象池时，签名要初始化！
        return signature;
    }

    public long getCpuTimeMs() {
        return cpuTimeMs;
    }

    public long getWallClockTimeNs() {
        return wallClockTimeNs;
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

    public String getThreadName() {
        return threadName;
    }

    public void setThreadName(String threadName) {
        this.threadName = threadName;
    }

    private String createSignature(String className, String methodName, String argTypes) {
        return className + "." + methodName + "(" + argTypes + ")";
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getArgTypes() {
        return argTypes;
    }

    public void setArgTypes(String argTypes) {
        this.argTypes = argTypes;
    }
}
