package andr.perf.monitor.cpu;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by ZhouKeWen on 17/3/24.
 */

public class MethodSampleManager {

    private static final MethodSampleManager ourInstance = new MethodSampleManager();

    private final Collection<MethodInfo> methodInfoList;

    /**
     * 临时保存不同线程的方法调用栈
     */
    private final ConcurrentHashMap<String, Deque<MethodInfo>> threadMethodStack;

    public static MethodSampleManager getInstance() {
        return ourInstance;
    }

    private MethodSampleManager() {
        threadMethodStack = new ConcurrentHashMap<>();
        methodInfoList = new ArrayList<>();
    }

    public void recordMethodEnter(String cls, String method, String argTypes) {
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

    public void recordMethodExit(long useNanoTime, long useThreadTime, String cls, String method,
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

    public void recordMethodExitFinally(String cls, String method, String argTypes) {
        //TODO 注意考虑多线程调用
        //方法无论是return退出还是异常throw退出，都会回调这里
        String threadName = Thread.currentThread().getName();
        Deque<MethodInfo> methodStack = threadMethodStack.get(threadName);
        MethodInfo topMethod = methodStack.pop();
        Log.i("", "finally-----------method:" + method + " pop: " + topMethod.getSignature() + " normal?" +
                topMethod.isNormalExit());
        if (methodStack.isEmpty()) {
            synchronized (methodInfoList) {
                methodInfoList.add(topMethod);
            }
        }
    }

    public List<MethodInfo> getMethodInfoList() {
        ArrayList<MethodInfo> list;
        synchronized (methodInfoList) {
            list = new ArrayList<>(methodInfoList);
        }
        return list;
    }

    public void clean() {
        synchronized (methodInfoList) {
            methodInfoList.clear();
        }
    }

    private String createSignature(String className, String methodName, String argTypes) {
        return className + "." + methodName + "(" + argTypes + ")";
    }
}
