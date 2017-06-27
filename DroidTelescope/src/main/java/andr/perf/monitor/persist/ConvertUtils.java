package andr.perf.monitor.persist;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import andr.perf.monitor.cpu.models.BlockInfo;
import andr.perf.monitor.cpu.models.MethodInfo;
import andr.perf.monitor.interactive.IEvent;
import andr.perf.monitor.memory.SuspectWeakReference;
import andr.perf.monitor.memory.models.LeakInfo;
import andr.perf.monitor.utils.Logger;

/**
 * Created by ZhouKeWen on 17/3/24.
 */
public class ConvertUtils {

    private static final String TAG = "ConvertUtils";

    /**
     * 将{@link BlockInfo}对象转换成Json格式
     *
     * @param blockInfo
     * @return
     * @throws JSONException
     */
    public static JSONObject convertBlockInfoToJson(BlockInfo blockInfo) throws JSONException {
        if (blockInfo == null) {
            return null;
        }
        return innerConvertToJson(blockInfo.getWallClockTimeMs(), blockInfo.getCpuTimeMs(),
                blockInfo.getRootMethodList());
    }

    public static JSONObject convertLeakInfoToJson(LeakInfo leakInfo) throws JSONException {
        if (leakInfo == null) {
            return null;
        }
        List<SuspectWeakReference> referenceList = leakInfo.getReferenceList();
        return innerConvertToJson(referenceList);
    }

    private static void showMethodTrace(MethodInfo rootMethod, int depth) {
        StringBuilder tab = new StringBuilder();
        int d = depth;
        while (d > 0) {
            tab.append("===");
            d--;
        }
        tab.append(">");

        Logger.i(TAG, tab.toString() + rootMethod.toString());

        List<MethodInfo> list = rootMethod.getInvokeTraceList();
        depth++;
        for (MethodInfo method : list) {
            showMethodTrace(method, depth);
        }
    }

    private static JSONObject innerConvertToJson(List<SuspectWeakReference> referenceList) throws
            JSONException {
        JSONObject jsonObject = null;
        if (referenceList != null && !referenceList.isEmpty()) {
            JSONArray referenceArray = new JSONArray();
            for (SuspectWeakReference reference : referenceList) {
                JSONObject referenceJson = createReferenceJSONObject(reference);
                if (referenceJson != null) {
                    referenceArray.put(referenceJson);
                }
            }
            if (referenceArray.length() > 0) {
                jsonObject = new JSONObject();
                jsonObject.put("garbage_reference_list", referenceArray);
            }
        }
        return jsonObject;
    }

    private static JSONObject createReferenceJSONObject(SuspectWeakReference reference) throws JSONException {
        Object obj = reference.get();
        if (obj == null) {
            return null;
        }
        JSONObject referenceJson = new JSONObject();
        referenceJson.put("objectId", obj.toString());
        String[] createStack = reference.getCreateStack();
        if (createStack != null && createStack.length > 0) {
            StringBuilder sb = new StringBuilder(createStack[createStack.length - 1]);
            for (int i = createStack.length - 2; i >= 0; i--) {
                String createObjectId = createStack[i];
                sb.append("->").append(createObjectId);
            }
            referenceJson.put("object_create_chain", sb.toString());
        }
        IEvent[] events = reference.getViewEventArray();
        if (events != null && events.length > 0) {
            JSONArray eventArray = new JSONArray();
            for (IEvent event : events) {
                eventArray.put(event.toJson());
            }
            referenceJson.put("user_event_stack", eventArray);
        }
        return referenceJson;
    }

    private static JSONObject innerConvertToJson(long wallClockTimeMs, long cpuTimeMs,
            List<MethodInfo> methodList) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("loop_wall_clock_time", wallClockTimeMs);
        jsonObject.put("loop_cpu_time", cpuTimeMs);
        JSONArray rootMethodArray = new JSONArray();
        jsonObject.put("invoke_trace_array", rootMethodArray);
        if (methodList != null && !methodList.isEmpty()) {
            for (MethodInfo method : methodList) {
                JSONObject methodJson = createMethodJSONObject(method);
                rootMethodArray.put(methodJson);
            }
        }
        return jsonObject;
    }

    private static JSONObject createMethodJSONObject(MethodInfo method) throws JSONException {
        JSONObject methodJson = new JSONObject();
        methodJson.put("method_signature", method.getSignature());
        methodJson.put("thread_id", method.getThreadId());
        methodJson.put("wall_clock_time", method.getWallClockTimeMs());
        methodJson.put("cpu_time", method.getCpuTimeMs());

        List<MethodInfo> invokeTraceList = method.getInvokeTraceList();
        if (invokeTraceList == null || invokeTraceList.isEmpty()) {
            return methodJson;
        } else {
            JSONArray traceArray = new JSONArray();
            methodJson.put("invoke_trace", traceArray);
            for (MethodInfo subMethod : invokeTraceList) {
                JSONObject subMethodJson = createMethodJSONObject(subMethod);
                traceArray.put(subMethodJson);
            }
        }
        return methodJson;
    }

}
