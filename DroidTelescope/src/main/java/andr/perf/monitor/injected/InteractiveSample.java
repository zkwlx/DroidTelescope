package andr.perf.monitor.injected;

import android.content.DialogInterface;
import android.util.Log;
import android.view.View;
import android.view.ViewParent;
import android.widget.AbsListView;
import android.widget.AdapterView;

/**
 * 用户交互行为采样类，用于代码注入
 * Created by ZhouKeWen on 2017/5/10.
 */
public class InteractiveSample {

    public static boolean shouldMonitor() {
        return true;
    }

    //android.view.View$OnClickListener
    public static void onViewClick(Object object, View view) {
        Log.i("zkw", "object toString>>>>>>" + object + " view>>>>" + view + " viewParent>>>>" + view.getParent());

        Log.i("zkw", "object>>>>>>" + object.getClass().getName() + " view>>>>" + view.getClass().getName() +
                " viewParent>>>>" + view.getParent().getClass().getName());



    }

    //android.view.View$OnLongClickListener
    public static void onViewLongClick(View v) {
        ViewParent p = v.getParent();

    }

    //AdapterView.OnItemClickListener
    public static void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    //AdapterView.OnItemLongClickListener
    public static void onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

    }

    //AdapterView.OnItemSelectedListener
    public static void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    //android.content.DialogInterface$OnClickListener
    public static void onDialogClick(DialogInterface dialog, int which) {

    }

    //AbsListView.OnScrollListener_*
    public static void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    //View.OnScrollChangeListener_*
    public static void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

    }

}
