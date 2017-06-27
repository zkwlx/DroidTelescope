package andr.perf.monitor.injected;

import andr.perf.monitor.SamplerFactory;
import andr.perf.monitor.utils.Logger;

/**
 * 对象泄露采样类，用于代码注入
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
        Logger.i("zkw", "low memory>>>>>>>>>>>>>>>>>" + object);
        SamplerFactory.getReferenceSampler().onLowMemory(object);
    }

    public static void objectTrimMemory(Object object, int level) {
        Logger.i("zkw", "trim memory>>>>>>>>>>>>>>>>>" + object + " level:" + level);
        SamplerFactory.getReferenceSampler().onTrimMemory(object, level);
    }
}
