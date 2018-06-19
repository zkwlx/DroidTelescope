package andr.perf.monitor.stack_traces;

import android.os.Trace;

import java.util.Deque;
import java.util.LinkedList;

import andr.perf.monitor.cpu.models.MethodInfo;
import andr.perf.monitor.utils.Logger;

/**
 * 使用 Android 提供的 SysTrace 方法来收集调用栈耗时
 *
 * @author zhoukewen
 * @since 2018/5/22
 */
public class SysTraceMethodSampler extends AbstractMethodSampler {

    private final Deque<MethodInfo> traceStack = new LinkedList<>();

    @Override
    public void onMethodEnter(String cls, String method, String argTypes) {
        // 只收集 UI 线程的方法
        if (isNotUIThread()) {
            return;
        }
        if (!TracesMonitor.isTracing) {
            return;
        }
        String signature = cls + "." + method;
        if (signature.length() <= 127) {
            MethodInfo methodInfo = new MethodInfo();
            methodInfo.setClassName(cls);
            methodInfo.setMethodName(method);
            traceStack.push(methodInfo);
            Logger.d("Trace.begin ----------- " + signature);
            Trace.beginSection(signature);
        } else {
            Logger.i("[ERROR:] On SysTrace, method signature > 127: " + cls + "." + method + "(" + argTypes + ")");
        }
    }

    @Override
    public void onMethodExit(String cls, String method, String argTypes) {
    }

    @Override
    public void onMethodExitFinally(String cls, String method, String argTypes) {
        // 只收集 UI 线程的方法
        if (isNotUIThread()) {
            return;
        }
        if (!TracesMonitor.isTracing) {
            return;
        }
        String signature = cls + "." + method;
        if (signature.length() <= 127) {
            if (!traceStack.isEmpty()) {
                MethodInfo methodInfo = traceStack.pop();
                if (methodInfo.getClassName().equals(cls) && methodInfo.getMethodName().equals(method)) {
                    Logger.d("Trace.end ----------- " + signature);
                    Trace.endSection();
                } else {
                    String msg = "Error on method finally, stack top " + methodInfo + ", but my " + signature;
                    Logger.e(msg);
                    throw new IllegalStateException(msg);
                }
            } else {
                //退出方法时，自己不在栈中，可能是因为DroidTelescope.startMethodTracing()方法在自己里面调用
                Logger.d("onFinally stack is empty!-------" + cls + "." + method + "(" + argTypes + ")");
            }
        }
    }

    @Override
    public Deque<MethodInfo> cloneCurrentThreadStack() {
        if (isNotUIThread()) {
            String msg = "使用 SysTrace 时，cloneCurrentThreadStack()要在 UI 线程调用！";
            Logger.e(msg);
            throw new RuntimeException(msg);
        }
        Deque<MethodInfo> copy = new LinkedList<>();
        if (traceStack != null && traceStack.size() > 0) {
            for (MethodInfo info : traceStack) {
                copy.addLast(info);
            }
        }
        return copy;
    }

    @Override
    public void cleanStack() {
        // TODO 这种判断方式不太好，其他功能想要 tracing 就尴尬了
        if (!TracesMonitor.isTracing) {
            //当停止追踪时，才允许 clean。
            //理论上到这里 traceStack 应该是空的
            traceStack.clear();
        }
    }
}
