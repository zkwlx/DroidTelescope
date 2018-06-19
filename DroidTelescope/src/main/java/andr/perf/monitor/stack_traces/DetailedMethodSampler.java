package andr.perf.monitor.stack_traces;


import java.util.Deque;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;

import andr.perf.monitor.DroidTelescope;
import andr.perf.monitor.Config;
import andr.perf.monitor.cpu.models.MethodInfo;
import andr.perf.monitor.utils.Logger;

/**
 * 详细的方法采样器，记录内容包括各自线程的方法耗时、方法调用栈。
 * Created by ZhouKeWen on 17/3/24.
 */
public class DetailedMethodSampler extends AbstractMethodSampler {

    /**
     * 临时保存不同线程的方法调用栈
     */
    private final ConcurrentHashMap<Long, Deque<MethodInfo>> threadMethodStack;

    private final ThreadLocal<Boolean> localSkip = new ThreadLocal<>();

    private final boolean configJustRecordUiThread;

    public DetailedMethodSampler() {
        super();
        configJustRecordUiThread = DroidTelescope.getConfig().justRecordUIThread();
        threadMethodStack = new ConcurrentHashMap<>();
    }

    //==============一次post任务到Looper要消耗1ms左右，所以当任务执行时间小于1ms，则不适用异步==============
    //==============实验证明，由于任务耗时很短，异步执行反而耗时变长，所以改成同步调用=========

    @Override
    public void onMethodEnter(final String cls, final String method, final String argTypes) {
        boolean isSkip = skipSampling();
        boolean is = isSkip || !TracesMonitor.isTracing;
        Logger.d("onEnter_skip:" + is + " -------" + cls + "." + method + "(" + argTypes + ")");
        localSkip.set(isSkip);
        if (isSkip) {
            return;
        }
        if (!TracesMonitor.isTracing) {
            return;
        }
        final long threadId = Thread.currentThread().getId();
        Deque<MethodInfo> methodStack = threadMethodStack.get(threadId);
        if (methodStack == null) {
            //线程首次调用，创建该线程的临时调用栈
            methodStack = new LinkedList<>();
            threadMethodStack.put(threadId, methodStack);
        }
        final String threadName = Thread.currentThread().getName();
        //TODO 考虑使用对象池！！！！
        //创建新的MethodInfo
        MethodInfo info = new MethodInfo();
        info.startTimestamp();
        info.setThreadId(threadId);
        info.setThreadName(threadName);
        info.setClassName(cls);
        info.setMethodName(method);
        info.setArgTypes(argTypes);
        //将当前方法添加到线程的临时调用栈
        methodStack.push(info);
    }

    @Override
    public void onMethodExit(final String cls, final String method, String argTypes) {
        if (localSkip.get()) {
            return;
        }
        if (!TracesMonitor.isTracing) {
            return;
        }
        final long threadId = Thread.currentThread().getId();
        //当方法非return语句退出时，不会回调onMethodExit()
        Deque<MethodInfo> methodStack = threadMethodStack.get(threadId);
        if (methodStack != null && !methodStack.isEmpty()) {
            //获取当前线程调用栈的栈顶方法，并更新记录数据
            MethodInfo topMethod = methodStack.peekFirst();
            //进入这里的方法都是正常退出的方法
            topMethod.setNormalExit();
        } else {
            //退出方法时，自己不在栈中，可能是因为DroidTelescope.startMethodTracing()方法在自己里面调用
            Logger.d("onExit no stack,or stack is empty!-------" + cls + "." + method + "(" + argTypes + ")");
        }
    }

    @Override
    public void onMethodExitFinally(String cls, final String method, String argTypes) {
        boolean isSkip = localSkip.get() || !TracesMonitor.isTracing;
        Logger.d("onFinally_skip:" + isSkip + " -------" + cls + "." + method + "(" + argTypes + ")");
        if (localSkip.get()) {
            return;
        }
        if (!TracesMonitor.isTracing) {
            return;
        }
        final long threadId = Thread.currentThread().getId();
        //方法无论是return退出还是异常throw退出，都会回调onMethodExitFinally()
        //threadMethodStack无需remove，有两个原因：一是同线程可能再次记录调用栈，所以缓存调用栈结构；二是调用栈本身会清空。
        Deque<MethodInfo> methodStack = threadMethodStack.get(threadId);
        if (methodStack != null && !methodStack.isEmpty()) {
            MethodInfo topMethod = methodStack.pop();
            topMethod.calculateDuration();
            Config config = DroidTelescope.getConfig();
            //如果是非正常退出，例如抛异常，则强制入栈
            if (!topMethod.isNormalExit() || config.shouldRecordMethod(topMethod.getWallClockTimeNs(), topMethod.getCpuTimeMs())) {
                //弹出当前线程的栈顶Method，如果栈为空了，说明这个栈顶Method是个rootMethod，保存到root列表
                if (methodStack.isEmpty()) {
                    addRootMethod(topMethod);
                } else {
                    //如果当前调用栈不为空，说明此方法是个子方法，于是添加到rootMethod的调用队列当中
                    MethodInfo rootMethod = methodStack.peekFirst();
                    rootMethod.addInnerMethod(topMethod);
                }
            }
        } else {
            //退出方法时，自己不在栈中，可能是因为DroidTelescope.startMethodTracing()方法在自己里面调用
            Logger.d("onFinally no stack,or stack is empty!-------" + cls + "." + method + "(" + argTypes + ")");
        }
    }

    @Override
    public void cleanStack() {
        // TODO 这种判断方式不太好，其他功能想要 tracing 就尴尬了
        if (!TracesMonitor.isTracing) {
            //当停止追踪时，才允许 clean。
            super.cleanStack();
            synchronized (threadMethodStack) {
                for (Deque<MethodInfo> stack : threadMethodStack.values()) {
                    if (stack != null) {
                        stack.clear();
                    }
                }
            }
        }

    }

    @Override
    public Deque<MethodInfo> cloneCurrentThreadStack() {
        final long threadId = Thread.currentThread().getId();
        Deque<MethodInfo> methodStack = threadMethodStack.get(threadId);
        Deque<MethodInfo> copy = new LinkedList<>();
        if (methodStack != null && methodStack.size() > 0) {
            for (MethodInfo info : methodStack) {
                copy.addLast(info);
            }
        }
        return copy;
    }

    private boolean skipSampling() {
        return configJustRecordUiThread && isNotUIThread();
    }
}
