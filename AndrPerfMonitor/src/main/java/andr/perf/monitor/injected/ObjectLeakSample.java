package andr.perf.monitor.injected;

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


}
