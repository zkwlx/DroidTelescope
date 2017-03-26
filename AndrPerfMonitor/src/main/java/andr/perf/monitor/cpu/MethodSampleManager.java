package andr.perf.monitor.cpu;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by ZhouKeWen on 17/3/24.
 */

public class MethodSampleManager {

    private static final MethodSampleManager ourInstance = new MethodSampleManager();

    private List<MethodInfo> methodInfoList = new ArrayList<>();

    //TODO 注意不同线程的调用！
    private Deque<MethodInfo> rootMethodStack = new LinkedList<>();

    public static MethodSampleManager getInstance() {
        return ourInstance;
    }

    private MethodSampleManager() {

    }

    public void recordMethodEnter(String cls, String method, String argTypes) {
        MethodInfo info = new MethodInfo();
        info.setSignature(cls + "." + method + "(" + argTypes + ")");
        if (!rootMethodStack.isEmpty()) {
            MethodInfo rootMethod = rootMethodStack.peekFirst();
            rootMethod.addInnerMethod(info);
        }
        rootMethodStack.push(info);
    }

    public void recordMethodExit(long useNanoTime, long useThreadTime, String cls, String method,
                                 String argTypes) {
        MethodInfo exitMethod = rootMethodStack.pop();
        exitMethod.setUseNanoTime(useNanoTime);
        exitMethod.setUseThreadTime(useThreadTime);
        //TODO 写注释！！
        if (rootMethodStack.isEmpty()) {
            methodInfoList.add(exitMethod);
        }
    }

    public List<MethodInfo> getMethodInfoList() {
        return new ArrayList<>(methodInfoList);
    }

    public void clean() {
        methodInfoList.clear();
    }
}
