package andr.perf.monitor.stack_traces;

import android.content.Context;
import android.os.Looper;
import android.os.SystemClock;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.util.Deque;
import java.util.List;

import andr.perf.monitor.SamplerFactory;
import andr.perf.monitor.cpu.models.BlockInfo;
import andr.perf.monitor.cpu.models.MethodInfo;
import andr.perf.monitor.persist.ConvertUtils;
import andr.perf.monitor.utils.Logger;

/**
 * @author zhoukewen
 * @since 2018/5/7
 */
public class TracesMonitor {

    private static final String TAG = TracesMonitor.class.getName();

    private static long startWallClockTimeMs;
    private static long startCpuTimeMs;

    public static volatile boolean isTracing = false;

    public static void startTracing() {
        if (isTracing) {
            //不允许重复追踪
            throw new StartTracingException("不允许多次启动[方法追踪]，请停止之后再启动！");
        }
        if (isUseSysTrace() && isNotUIThread()) {
            //使用 SysTrace 时，只能在 UI 线程追踪
            throw new StartTracingException("使用 SysTrace 时，startMethodTracing()和stopMethodTracing()要在 UI 线程成对调用！");
        }
        startWallClockTimeMs = System.currentTimeMillis();
        startCpuTimeMs = SystemClock.currentThreadTimeMillis();
        isTracing = true;
    }

    /**
     * 结束方法调用耗时追踪，返回生成的 js 报告文件路径
     *
     * @return
     */
    public static String stopTracing(Context context) {
        boolean isSysTrace = isUseSysTrace();
        if (isSysTrace && isNotUIThread()) {
            //使用 SysTrace 时，只能在 UI 线程追踪
            throw new StartTracingException("使用 SysTrace 时，startMethodTracing()和stopMethodTracing()要在 UI 线程成对调用！");
        }
        finishRemainMethod();
        if (isTracing) {
            isTracing = false;
            if (!isSysTrace) {
                //有些 Sampler 没有栈数据，比如 SysTrace
                JSONArray jsonArray = createJSONMethodTraces();
                if (jsonArray != null) {
                    String jsonString = jsonArray.toString();
                    File zipFile = TraceHtmlReporter.createTraceReportFile(context, jsonString);
                    return zipFile.getAbsolutePath();
                }
            }
        }
        return null;
    }

    /**
     * 由于 stopTracing 所在方法的调用栈中可能还有方法，所以手动结束剩下方法
     */
    private static void finishRemainMethod() {
        AbstractMethodSampler methodSampler = SamplerFactory.getMethodSampler();
        //遍历当前线程的方法栈，回调他们的 Exit
        Deque<MethodInfo> methodStack = methodSampler.cloneCurrentThreadStack();
        for (MethodInfo info : methodStack) {
            Logger.d("exit---- " + info.getSignature());
            methodSampler.onMethodExit(info.getClassName(), info.getMethodName(), info.getArgTypes());
            methodSampler.onMethodExitFinally(info.getClassName(), info.getMethodName(), info.getArgTypes());
        }
    }

    private static JSONArray createJSONMethodTraces() {
        long wallClockTimeMs = System.currentTimeMillis() - startWallClockTimeMs;
        long cpuTimeMs = SystemClock.currentThreadTimeMillis() - startCpuTimeMs;
        AbstractMethodSampler methodSampler = SamplerFactory.getMethodSampler();
        List<MethodInfo> methodInfoList = null;
        if (methodSampler instanceof DetailedMethodSampler) {
            methodInfoList = ((DetailedMethodSampler) methodSampler).getRootMethodList();
        }
        if (methodInfoList == null || methodInfoList.isEmpty()) {
            Logger.i(TAG, "On tracing, but method list is empty!");
            return null;
        }
        //先用 BlockInfo 代替
        BlockInfo blockInfo = new BlockInfo();
        blockInfo.setCpuTimeMs(cpuTimeMs);
        blockInfo.setWallClockTimeMs(wallClockTimeMs);
        blockInfo.setRootMethodList(methodInfoList);
        JSONArray result = null;
        try {
            result = ConvertUtils.convertTracesInfoToJson(blockInfo);
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            // 调用栈使用完后清理！
            methodSampler.cleanStack();
        }
        return result;
    }

    private static boolean isNotUIThread() {
        final long mainThreadId = Looper.getMainLooper().getThread().getId();
        final long currentId = Thread.currentThread().getId();
        return mainThreadId != currentId;
    }

    private static boolean isUseSysTrace() {
        AbstractMethodSampler methodSampler = SamplerFactory.getMethodSampler();
        return methodSampler instanceof SysTraceMethodSampler;
    }
}
