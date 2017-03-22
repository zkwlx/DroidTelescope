package plugin.gradle.my;

import android.content.Intent;
import android.util.Log;

import andr.perf.monitor.CpuMonitor;

/**
 * Created by ZhouKeWen on 17/3/17.
 */

public class SomeTest implements Runnable {
    @Override
    public void run() {

    }

    public int someM() {
//        if (SomeTest.class.getName().endsWith("")) {
//            Log.i("zkw", "rrrr");
//            return 8888;
//        } else {
//            Log.i("zkw", "ssss");
//            return 6666;
//        }
        Log.i("zkw", "gogogo");
        return 9999;
    }
}
