package andr.perf.monitor;

import andr.perf.monitor.cpu.AbstractMethodSampler;
import andr.perf.monitor.cpu.DetailedMethodSampler;
import andr.perf.monitor.interactive.UserInteractiveSampler;
import andr.perf.monitor.memory.ObjectReferenceSampler;

/**
 * Created by ZhouKeWen on 17/3/28.
 */

public class SamplerFactory {

    private static AbstractMethodSampler methodSampler;

    private static ObjectReferenceSampler objectRefSampler;

    private static UserInteractiveSampler interactiveSampler;

    public static AbstractMethodSampler getMethodSampler() {
        if (methodSampler == null) {
            //TODO 这里根据具体config选择不同Sampler实现
            methodSampler = new DetailedMethodSampler();
        }
        return methodSampler;
    }

    public static ObjectReferenceSampler getReferenceSampler() {
        if (objectRefSampler == null) {
            objectRefSampler = new ObjectReferenceSampler();
        }
        return objectRefSampler;
    }

    public static UserInteractiveSampler getInteractiveSampler() {
        if (interactiveSampler == null) {
            interactiveSampler = new UserInteractiveSampler();
        }
        return interactiveSampler;
    }

}
