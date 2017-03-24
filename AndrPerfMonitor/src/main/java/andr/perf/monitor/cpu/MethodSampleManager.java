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
    private MethodInfo rootMethod = null;

    public static MethodSampleManager getInstance() {
        return ourInstance;
    }

    private MethodSampleManager() {

    }

    public void recordMethodEnter(String cls, String method, String argTypes) {
        MethodInfo info = new MethodInfo();
        info.setSignature(cls + "." + method + "(" + argTypes + ")");
        if (rootMethod == null) {
            //说明进入的是root方法
            rootMethod = info;
        } else {
            rootMethod.addMethodInvoke(info);
        }
    }

    public void recordMethodExit(long useNanoTime, long useThreadTime, String cls, String method,
            String argTypes) {
        MethodInfo subMethod = rootMethod.getCurrentMethod();

        MethodInfo info = new MethodInfo();
        info.setUseNanoTime(useNanoTime);
        info.setUseThreadTime(useThreadTime);
        info.setSignature(cls + "." + method + "(" + argTypes + ")");
    }

    public List<MethodInfo> getMethodInfoList() {
        return new ArrayList<>(methodInfoList);
    }

    public void clean() {
        methodInfoList.clear();
    }
}
