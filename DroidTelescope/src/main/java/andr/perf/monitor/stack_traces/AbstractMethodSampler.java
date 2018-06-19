package andr.perf.monitor.stack_traces;

import android.os.Looper;

import java.util.Deque;

import andr.perf.monitor.cpu.models.MethodInfo;

/**
 * Created by ZhouKeWen on 17/3/28.
 */
public abstract class AbstractMethodSampler {

    AbstractMethodSampler() {
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
     * @param cls
     * @param method
     * @param argTypes
     */
    public abstract void onMethodExit(String cls, String method, String argTypes);

    /**
     * 方法无论怎样退出（比如异常退出、throw等），都回调此接口
     *
     * @param cls
     * @param method
     * @param argTypes
     */
    public abstract void onMethodExitFinally(String cls, String method, String argTypes);

    /**
     * 获取当前调用线程的调用栈
     * <p><b>注意要返回 clone 后的堆栈，因为 Sampler 会动态修改堆栈</b></p>
     *
     * @return
     */
    public abstract Deque<MethodInfo> cloneCurrentThreadStack();

    /**
     * 清理内部数据结构
     */
    public abstract void cleanStack();

    boolean isNotUIThread() {
        final long mainThreadId = Looper.getMainLooper().getThread().getId();
        final long currentId = Thread.currentThread().getId();
        return mainThreadId != currentId;
    }

}
