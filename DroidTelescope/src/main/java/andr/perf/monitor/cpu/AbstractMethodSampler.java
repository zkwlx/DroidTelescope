package andr.perf.monitor.cpu;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import andr.perf.monitor.cpu.models.MethodInfo;

/**
 * Created by ZhouKeWen on 17/3/28.
 */
public abstract class AbstractMethodSampler {

    /**
     * root方法列表，可以根据每个root方法解析出调用栈
     */
    private final Collection<MethodInfo> rootMethodList;

    AbstractMethodSampler() {
        rootMethodList = new ArrayList<>();
    }

    /**
     * 当进入方法时，回调该接口
     *
     * @param cls
     * @param method
     * @param argTypes
     */
    public abstract void onMethodEnter(String cls, String method, String argTypes);

    /**
     * 当方法正常return时，回调该接口
     *
     * @param wallClockTimeNs
     * @param cpuTimeMs
     * @param cls
     * @param method
     * @param argTypes
     */
    public abstract void onMethodExit(long wallClockTimeNs, long cpuTimeMs, String cls, String method,
            String argTypes);

    /**
     * 方法无论怎样退出（比如异常退出、throw等），都回调此接口
     *
     * @param cls
     * @param method
     * @param argTypes
     */
    public abstract void onMethodExitFinally(String cls, String method, String argTypes);

    void addRootMethod(MethodInfo method) {
        synchronized (rootMethodList) {
            rootMethodList.add(method);
        }
    }

    public List<MethodInfo> getRootMethodList() {
        ArrayList<MethodInfo> list;
        synchronized (rootMethodList) {
            list = new ArrayList<>(rootMethodList);
        }
        return list;
    }

    public void cleanRootMethodList() {
        synchronized (rootMethodList) {
            rootMethodList.clear();
        }
    }
}
