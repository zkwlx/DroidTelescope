package andr.perf.monitor;

import andr.perf.monitor.cpu.AbstractMethodSampler;
import andr.perf.monitor.cpu.DetailedMethodSampler;
import andr.perf.monitor.memory.ObjectReferenceSampler;

/**
 * Created by ZhouKeWen on 17/3/28.
 */

public class SamplerFactory {

    private final static AbstractMethodSampler methodSampler;

    private final static ObjectReferenceSampler objectRefSampler;

    static {
        //TODO 这里根据具体config选择不同Sampler实现
        methodSampler = new DetailedMethodSampler();
        objectRefSampler = new ObjectReferenceSampler();
    }

    public static AbstractMethodSampler getMethodSampler() {
        return methodSampler;
    }

    public static ObjectReferenceSampler getReferenceSampler() {
        return objectRefSampler;
    }

}
