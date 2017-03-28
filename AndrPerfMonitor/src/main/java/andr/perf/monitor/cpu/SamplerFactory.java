package andr.perf.monitor.cpu;

/**
 * Created by ZhouKeWen on 17/3/28.
 */

public class SamplerFactory {

    private final static AbstractMethodSampler samplerInstance;

    static {
        //TODO 这里根据具体config选择不同Sampler实现
        samplerInstance = new DetailedMethodSampler();
    }

    public static AbstractMethodSampler getSampler() {
        return samplerInstance;
    }

}
