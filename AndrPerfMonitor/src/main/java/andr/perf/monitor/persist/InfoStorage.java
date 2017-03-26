package andr.perf.monitor.persist;

import android.util.Log;

import java.util.List;

import andr.perf.monitor.cpu.MethodInfo;

/**
 * Created by ZhouKeWen on 17/3/24.
 */

public class InfoStorage {

    public static void onStorageForMethod(List<MethodInfo> methodInfos) {
        for (MethodInfo info : methodInfos) {
            showMethodTrace(info, 0);
        }
    }

    private static void showMethodTrace(MethodInfo rootMethod, int depth) {
        StringBuilder tab = new StringBuilder();
        int d = depth;
        while (d > 0) {
            tab.append("===");
            d--;
        }
        tab.append(">");

        Log.i("zkw", tab.toString() + rootMethod.toString());

        List<MethodInfo> list = rootMethod.getInvokeTraceList();
        depth++;
        for (MethodInfo method : list) {
            showMethodTrace(method, depth);
        }
    }

}
