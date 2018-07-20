package plugin.gradle.my;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import dt.monitor.DT;

/**
 * Created by ZhouKeWen on 17/3/24.
 */

public class MyApplication extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        DT.init(this);
        DroidTelescopeProxy.init();
        DroidTelescopeProxy.startMethodTracing();
        fuck();
        b();
    }

    private void fuck() {
        Log.d("zkw", "----");
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        a();
    }

    private void a() {
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void b() {
        try {
            Thread.sleep(400);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }


}
