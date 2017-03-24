package plugin.gradle.my;

import android.app.Application;

import andr.perf.monitor.AndroidMonitor;

/**
 * Created by ZhouKeWen on 17/3/24.
 */

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        AndroidMonitor.install(this);
    }
}
