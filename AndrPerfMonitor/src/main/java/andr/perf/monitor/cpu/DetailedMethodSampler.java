package andr.perf.monitor.cpu;

import android.util.Log;

import java.util.Deque;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;

import andr.perf.monitor.cpu.models.MethodInfo;

/**
 * 详细的方法采样器，记录内容包括各自线程的方法耗时、方法调用栈。
 * Created by ZhouKeWen on 17/3/24.
 */
public class DetailedMethodSampler extends AbstractMethodSampler {

    /**
     * 临时保存不同线程的方法调用栈
     */
    private final ConcurrentHashMap<String, Deque<MethodInfo>> threadMethodStack;

    public DetailedMethodSampler() {
        super();
        threadMethodStack = new ConcurrentHashMap<>();
    }

    @Override
    public void onMethodEnter(String cls, String method, String argTypes) {
        //TODO 考虑使用对象池！！！！
        String threadName = Thread.currentThread().getName();
        Deque<MethodInfo> methodStack = threadMethodStack.get(threadName);
        if (methodStack == null) {
            methodStack = new LinkedList<>();
            threadMethodStack.put(threadName, methodStack);
        }
        MethodInfo info = new MethodInfo();
        info.setThreadName(threadName);
        info.setSignature(createSignature(cls, method, argTypes));
        if (!methodStack.isEmpty()) {
            MethodInfo rootMethod = methodStack.peekFirst();
            rootMethod.addInnerMethod(info);
        }
        Log.i("", "-----------push:" + method);
        methodStack.push(info);
    }

    @Override
    public void onMethodExit(long useNanoTime, long useThreadTime, String cls, String method,
            String argTypes) {
        String threadName = Thread.currentThread().getName();
        Deque<MethodInfo> methodStack = threadMethodStack.get(threadName);
        //当方法非return语句退出时，不会回调这里
        MethodInfo topMethod = methodStack.peekFirst();
        Log.i("", "normal exit-----------method:" + method + " peek: " + topMethod.getSignature());
        topMethod.setUseNanoTime(useNanoTime);
        topMethod.setUseThreadTime(useThreadTime);
        //进入这里的方法都是正常退出的方法
        topMethod.setNormalExit();
    }

    @Override
    public void onMethodExitFinally(String cls, String method, String argTypes) {
        //方法无论是return退出还是异常throw退出，都会回调这里
        String threadName = Thread.currentThread().getName();
        Deque<MethodInfo> methodStack = threadMethodStack.get(threadName);
        MethodInfo topMethod = methodStack.pop();
        Log.i("", "finally-----------method:" + method + " pop: " + topMethod.getSignature() + " normal?" +
                topMethod.isNormalExit());
        if (methodStack.isEmpty()) {
            addRootMethod(topMethod);
        }
    }

    private String createSignature(String className, String methodName, String argTypes) {
        return className + "." + methodName + "(" + argTypes + ")";
    }

}
