package plugin.gradle.my;

import android.util.Log;

/**
 * Created by ZhouKeWen on 17/3/17.
 */

public class SomeTest implements Runnable {
    @Override
    public void run() {
        try {
            Thread.sleep(600);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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
        long i = 10000L;
        String a = "a";
        for (int n = 0; n < i; n++) {
            a += n;
        }
        return 9999;
    }
}
