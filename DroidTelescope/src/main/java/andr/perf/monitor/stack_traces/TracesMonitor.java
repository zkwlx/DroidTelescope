package andr.perf.monitor.stack_traces;

import android.content.Context;
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
        finishRemainMethod();
        if (isTracing) {
            isTracing = false;
            //TODO 加个判断比较好，因为有些 Sampler 没有栈数据，比如 SysTrace
            JSONArray jsonArray = createJSONMethodTraces();
            if (jsonArray != null) {
                String jsonString = jsonArray.toString();
                File zipFile = TraceHtmlReporter.createTraceReportFile(context, jsonString);
                return zipFile.getAbsolutePath();
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    private static void finishRemainMethod() {
        AbstractMethodSampler methodSampler = SamplerFactory.getMethodSampler();
        if (methodSampler instanceof DetailedMethodSampler) {
            //遍历当前线程的方法栈，回调他们的 Exit
            Deque<MethodInfo> methodStack = ((DetailedMethodSampler) methodSampler).getCurrentThreadStack();
            for (MethodInfo info : methodStack) {
                Logger.d("exit---- " + info.getSignature());
                SamplerFactory.getMethodSampler().onMethodExit(info.getClassName(), info.getMethodName(), info.getArgTypes());
                SamplerFactory.getMethodSampler().onMethodExitFinally(info.getClassName(), info.getMethodName(), info.getArgTypes());
            }
        }
    }

    private static JSONArray createJSONMethodTraces() {
        long wallClockTimeMs = System.currentTimeMillis() - startWallClockTimeMs;
        long cpuTimeMs = SystemClock.currentThreadTimeMillis() - startCpuTimeMs;
        List<MethodInfo> methodInfoList = SamplerFactory.getMethodSampler().getRootMethodList();
        if (methodInfoList.isEmpty()) {
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
            SamplerFactory.getMethodSampler().cleanStack();
        }
        return result;
    }
}
