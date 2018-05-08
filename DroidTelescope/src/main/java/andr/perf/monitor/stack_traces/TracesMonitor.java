package andr.perf.monitor.stack_traces;

import android.os.SystemClock;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

    private static volatile boolean isTracing = false;

    public static void startTracing() {
        if (isTracing) {
            //不允许重复追踪
            return;
        }
        startWallClockTimeMs = System.currentTimeMillis();
        startCpuTimeMs = SystemClock.currentThreadTimeMillis();
        isTracing = true;
    }

    /**
     * 结束方法调用耗时追踪，返回解析好的 Json 对象，便于分析
     *
     * @return
     */
    public static String stopTracing() {
        if (isTracing) {
            isTracing = false;
            JSONArray jsonArray = createJSONMethodTraces();
            if (jsonArray != null) {
                return jsonArray.toString();
            } else {
                return null;
            }
        } else {
            return null;
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
            SamplerFactory.getMethodSampler().cleanRootMethodList();
        }
        return result;
    }
}
