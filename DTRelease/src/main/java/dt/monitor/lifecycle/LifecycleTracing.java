package dt.monitor.lifecycle;

import dt.monitor.UIEventRecorder;

/**
 * @author zhoukewen
 * @since 2018/7/5
 */
public class LifecycleTracing {

    public void traceFragment(String event, Object fragmentObject) {
        String msg = "Fragment : " + event + " : " + fragmentObject.getClass().getCanonicalName() + " hash " + fragmentObject.hashCode();
        UIEventRecorder.add(msg);
    }

    public void traceActivity(String event, Object activityObject) {
        String msg = "Activity : " + event + " : " + activityObject.getClass().getCanonicalName() + " hash " + activityObject.hashCode();
        UIEventRecorder.add(msg);
    }

}
