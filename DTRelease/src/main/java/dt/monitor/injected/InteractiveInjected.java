package dt.monitor.injected;

import android.content.DialogInterface;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;

import dt.monitor.interactive.UserInteractiveTracing;

/**
 * 用户交互行为采样类，用于代码注入
 * Created by ZhouKeWen on 2017/5/10.
 */
public class InteractiveInjected {

    private static UserInteractiveTracing sampler = new UserInteractiveTracing();

    //android.view.View$OnClickListener
    public static void onViewClick(Object object, View view) {
        sampler.onViewClick(object, view);
    }

    //android.view.View$OnLongClickListener
    public static void onViewLongClick(Object object, View v) {
        sampler.onViewLongClick(object, v);
    }

    //AdapterView.OnItemClickListener
    public static void onItemClick(Object object, AdapterView<?> parent, View view, int position, long id) {
        sampler.onItemClick(object, parent, view, position, id);
    }

    //AdapterView.OnItemLongClickListener
    public static void onItemLongClick(Object object, AdapterView<?> parent, View view, int position,
                                       long id) {
        sampler.onItemLongClick(object, parent, view, position, id);
    }

    //AdapterView.OnItemSelectedListener
    public static void onItemSelected(Object object, AdapterView<?> parent, View view, int position,
                                      long id) {
        sampler.onItemSelected(object, parent, view, position, id);
    }

    //android.content.DialogInterface$OnClickListener
    public static void onDialogClick(Object object, DialogInterface dialog, int which) {
        sampler.onDialogClick(object, dialog, which);
    }

    //AbsListView.OnScrollListener_*
    public static void onScrollStateChanged(AbsListView view, int scrollState) {
        sampler.onScrollStateChanged(view, scrollState);
    }

    //View.OnScrollChangeListener_*
    public static void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
        sampler.onScrollChange(v, scrollX, scrollY, oldScrollX, oldScrollY);
    }

}
