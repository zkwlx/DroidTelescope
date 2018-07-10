package dt.monitor;

import android.content.Context;

import java.lang.ref.WeakReference;

/**
 * @author zhoukewen
 * @since 2018/7/6
 */
public class DT {

    public static WeakReference<Context> weakContext;

    public static void init(Context context) {
        weakContext = new WeakReference<>(context.getApplicationContext());
    }

}
