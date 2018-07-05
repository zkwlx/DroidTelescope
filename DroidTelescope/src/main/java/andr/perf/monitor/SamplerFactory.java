package andr.perf.monitor;

import android.util.Log;

import andr.perf.monitor.interactive.UserInteractiveTracing;
import andr.perf.monitor.stack_traces.AbstractMethodSampler;
import andr.perf.monitor.stack_traces.DetailedMethodSampler;
import andr.perf.monitor.memory.ObjectReferenceSampler;
import andr.perf.monitor.stack_traces.SysTraceMethodSampler;

/**
 * Created by ZhouKeWen on 17/3/28.
 */

public class SamplerFactory {

    private static AbstractMethodSampler methodSampler;

    private static ObjectReferenceSampler objectRefSampler;

    private static UserInteractiveTracing interactiveSampler;

    public static AbstractMethodSampler getMethodSampler() {
        if (methodSampler == null) {
            Config config = DroidTelescope.getConfig();
            //TODO 根据具体config选择不同Sampler实现
            if (config.useSysTrace()) {
                Log.i("zkw", "使用 SysTrace！！！！");
                methodSampler = new SysTraceMethodSampler();
            } else {
                Log.i("zkw", "使用默认的Sampler！！！！");
                methodSampler = new DetailedMethodSampler();
            }
        }
        return methodSampler;
    }

    public static ObjectReferenceSampler getReferenceSampler() {
        if (objectRefSampler == null) {
            objectRefSampler = new ObjectReferenceSampler();
        }
        return objectRefSampler;
    }

    public static UserInteractiveTracing getInteractiveSampler() {
        if (interactiveSampler == null) {
            interactiveSampler = new UserInteractiveTracing();
        }
        return interactiveSampler;
    }

}
