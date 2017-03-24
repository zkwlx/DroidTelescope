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
            Log.i("", info.toString());
        }
    }

}
