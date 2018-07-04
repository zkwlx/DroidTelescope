package dt.monitor.utils;

import android.util.Log;

/**
 * Created by ZhouKeWen on 2017/6/27.
 */
public class Logger {

    private static final String TAG = "zkw";

    public static boolean isDebug = false;

    public static void i(String content) {
        Log.i(TAG, content);
    }

    public static void i(String tag, String content) {
        tag = TAG + "_" + tag;
        Log.i(tag, content);
    }

    public static void d(String content) {
        if (isDebug) {
            Log.i(TAG, content);
        }
    }

    public static void d(String tag, String content) {
        if (isDebug) {
            tag = TAG + "_" + tag;
            Log.i(tag, content);
        }
    }

    public static void e(String content) {
        Log.e(TAG, content);
    }

    public static void e(String tag, String content) {
        tag = TAG + "_" + tag;
        Log.e(tag, content);
    }

}
