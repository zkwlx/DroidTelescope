package andr.perf.monitor.stack_traces;

import android.os.Trace;
import android.util.Log;

/**
 * 使用 Android 提供的 SysTrace 方法来收集调用栈耗时
 *
 * @author zhoukewen
 * @since 2018/5/22
 */
public class SysTraceMethodSampler extends AbstractMethodSampler {

    private boolean isStopTrace;

    @Override
    public void onMethodEnter(String cls, String method, String argTypes) {
        isStopTrace = !TracesMonitor.isTracing;
        if (isStopTrace) {
            return;
        }
        if (isNotUIThread()) {
            return;
        }
//        String signature = createSignature(cls, method, argTypes);
        String signature = cls + "." + method;
        if (signature.length() <= 127) {
            Trace.beginSection(signature);
        } else {
            Log.i("zkw", "what!?  >>>>>>" + createSignature(cls, method, argTypes));
        }
    }

    @Override
    public void onMethodExit(long wallClockTimeNs, long cpuTimeMs, String cls, String method, String argTypes) {

    }

    @Override
    public void onMethodExitFinally(String cls, String method, String argTypes) {
        if (isStopTrace) {
            return;
        }
        //TODO 只收集 UI 线程的方法
        if (isNotUIThread()) {
            return;
        }
        String signature = cls + "." + method;
        if (signature.length() <= 127) {
            //TODO 由于 SysTrace 是成对 begin、end，所以这里要注意！！有必要添加栈来维护
            Trace.endSection();
        }

    }

}
