package andr.perf.monitor.cpu;

import java.util.Deque;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;

import andr.perf.monitor.DroidTelescope;
import andr.perf.monitor.Config;
import andr.perf.monitor.cpu.models.MethodInfo;

/**
 * 详细的方法采样器，记录内容包括各自线程的方法耗时、方法调用栈。
 * Created by ZhouKeWen on 17/3/24.
 */
public class DetailedMethodSampler extends AbstractMethodSampler {

    /**
     * 临时保存不同线程的方法调用栈
     */
    private final ConcurrentHashMap<Long, Deque<MethodInfo>> threadMethodStack;

    public DetailedMethodSampler() {
        super();
        threadMethodStack = new ConcurrentHashMap<>();
    }

    //==============一次post任务到Looper要消耗1ms左右，所以当任务执行时间小于1ms，则不适用异步==============
    //==============实验证明，由于任务耗时很短，异步执行反而耗时变长，所以改成同步调用=========

    @Override
    public void onMethodEnter(final String cls, final String method, final String argTypes) {
        final long threadId = Thread.currentThread().getId();
        Deque<MethodInfo> methodStack = threadMethodStack.get(threadId);
        if (methodStack == null) {
            //线程首次调用，创建该线程的临时调用栈
            methodStack = new LinkedList<>();
            threadMethodStack.put(threadId, methodStack);
        }
        //TODO 考虑使用对象池！！！！
        //创建新的MethodInfo
        MethodInfo info = new MethodInfo();
        info.setThreadId(threadId);
        info.setSignature(createSignature(cls, method, argTypes));
        //将当前方法添加到线程的临时调用栈
        methodStack.push(info);
    }

    @Override
    public void onMethodExit(final long wallClockTimeNs, final long cpuTimeMs, final String cls,
            final String method, String argTypes) {
        final long threadId = Thread.currentThread().getId();
        //当方法非return语句退出时，不会回调onMethodExit()
        Deque<MethodInfo> methodStack = threadMethodStack.get(threadId);
        //获取当前线程调用栈的栈顶方法，并更新记录数据
        MethodInfo topMethod = methodStack.peekFirst();
        topMethod.setWallClockTimeNs(wallClockTimeNs);
        topMethod.setCpuTimeMs(cpuTimeMs);
        //进入这里的方法都是正常退出的方法
        topMethod.setNormalExit();
    }

    @Override
    public void onMethodExitFinally(String cls, final String method, String argTypes) {
        final long threadId = Thread.currentThread().getId();
        //方法无论是return退出还是异常throw退出，都会回调onMethodExitFinally()
        //threadMethodStack无需remove，有两个原因：一是同线程可能再次记录调用栈，所以缓存调用栈结构；二是调用栈本身会清空。
        Deque<MethodInfo> methodStack = threadMethodStack.get(threadId);
        MethodInfo topMethod = methodStack.pop();
        Config config = DroidTelescope.getConfig();
        if (config == null) {
            //TODO 还未初始化好，一般发生在Application.<init>方法中
            return;
        }
        if (config.shouldRecordMethod(topMethod.getWallClockTimeNs(), topMethod.getCpuTimeMs())) {
            //弹出当前线程的栈顶Method，如果栈为空了，说明这个栈顶Method是个rootMethod，保存到root列表
            if (methodStack.isEmpty()) {
                addRootMethod(topMethod);
            } else {
                //如果当前调用栈不为空，说明此方法是个子方法，于是添加到rootMethod的调用队列当中
                MethodInfo rootMethod = methodStack.peekFirst();
                rootMethod.addInnerMethod(topMethod);
            }
        }
    }

    private String createSignature(String className, String methodName, String argTypes) {
        return className + "." + methodName + "(" + argTypes + ")";
    }

}
