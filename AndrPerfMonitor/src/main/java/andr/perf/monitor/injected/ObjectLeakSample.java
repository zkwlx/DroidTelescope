package andr.perf.monitor.injected;

import android.util.Log;

import andr.perf.monitor.SamplerFactory;

/**
 * Created by ZhouKeWen on 17/3/31.
 */
public class ObjectLeakSample {

    public static boolean shouldMonitor() {
        return true;
    }

    public static void objectCreate(Object object) {
        SamplerFactory.getReferenceSampler().onKeyObjectCreate(object);
    }

    public static void objectDestroy(Object object) {
        SamplerFactory.getReferenceSampler().onKeyObjectDestroy(object);
    }

    public static void objectLowMemory(Object object) {
        Log.i("zkw", "low memory>>>>>>>>>>>>>>>>>" + object);
        SamplerFactory.getReferenceSampler().onLowMemory(object);
    }

    public static void objectTrimMemory(Object object, int level) {
        Log.i("zkw", "trim memory>>>>>>>>>>>>>>>>>" + object + " level:" + level);
        SamplerFactory.getReferenceSampler().onTrimMemory(object, level);
    }
}
