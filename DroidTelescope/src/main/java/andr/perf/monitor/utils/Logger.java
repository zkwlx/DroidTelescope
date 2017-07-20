package andr.perf.monitor.utils;

import android.util.Log;

/**
 * Created by ZhouKeWen on 2017/6/27.
 */
public class Logger {

    private static final String TAG = "zkw";

    private static boolean isDebug = true;

    public static void i(String content) {
        if (isDebug) {
            Log.i(TAG, content);
        }
    }

    public static void i(String tag, String content) {
        if (isDebug) {
            Log.i(tag, content);
        }
    }

    public static void e(String content) {
        if (isDebug) {
            Log.e(TAG, content);
        }
    }

    public static void e(String tag, String content) {
        if (isDebug) {
            Log.e(tag, content);
        }
    }

}
