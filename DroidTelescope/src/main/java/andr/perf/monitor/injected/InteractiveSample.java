package andr.perf.monitor.injected;

import android.content.DialogInterface;
import android.util.Log;
import android.view.View;
import android.view.ViewParent;
import android.widget.AbsListView;
import android.widget.AdapterView;

import andr.perf.monitor.SamplerFactory;

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
        SamplerFactory.getInteractiveSampler().onViewClick(object, view);

    }

    //android.view.View$OnLongClickListener
    public static void onViewLongClick(Object object, View v) {
        SamplerFactory.getInteractiveSampler().onViewLongClick(object, v);

    }

    //AdapterView.OnItemClickListener
    public static void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        SamplerFactory.getInteractiveSampler().onItemClick(parent, view, position, id);

    }

    //AdapterView.OnItemLongClickListener
    public static void onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        SamplerFactory.getInteractiveSampler().onItemLongClick(parent, view, position, id);
    }

    //AdapterView.OnItemSelectedListener
    public static void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        SamplerFactory.getInteractiveSampler().onItemSelected(parent, view, position, id);

    }

    //android.content.DialogInterface$OnClickListener
    public static void onDialogClick(Object object, DialogInterface dialog, int which) {
        SamplerFactory.getInteractiveSampler().onDialogClick(object, dialog, which);

    }

    //AbsListView.OnScrollListener_*
    public static void onScrollStateChanged(AbsListView view, int scrollState) {
        SamplerFactory.getInteractiveSampler().onScrollStateChanged(view, scrollState);

    }

    //View.OnScrollChangeListener_*
    public static void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
        SamplerFactory.getInteractiveSampler().onScrollChange(v, scrollX, scrollY, oldScrollX, oldScrollY);

    }

}
