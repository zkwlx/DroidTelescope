package dt.monitor.injected;

import dt.monitor.lifecycle.LifecycleTracing;

/**
 * @author zhoukewen
 * @since 2018/7/5
 */
public class LifecycleInjected {

    private static LifecycleTracing tracing = new LifecycleTracing();

    public static void traceFragment(String event, Object fragmentObject) {
        tracing.traceFragment(event, fragmentObject);
    }

    public static void traceActivity(String event, Object activityObject) {
        tracing.traceActivity(event, activityObject);
    }

}
