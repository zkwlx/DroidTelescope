package dt.monitor.injected;

import android.content.DialogInterface;
import android.support.design.widget.TabLayout;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.CompoundButton;

import dt.monitor.interactive.UserInteractiveTracing;

/**
 * 用户交互行为采样类，用于代码注入
 * Created by ZhouKeWen on 2017/5/10.
 */
public class InteractiveInjected {

    private static UserInteractiveTracing tracing = new UserInteractiveTracing();

    public static void setInteractiveListener(UserInteractiveTracing.InteractiveListener listener) {
        tracing.setInteractiveListener(listener);
    }

    //android.view.View$OnClickListener
    public static void onViewClick(Object object, View view) {
        tracing.onViewClick(object, view);
    }

    //android.view.View$OnLongClickListener
    public static void onViewLongClick(Object object, View v) {
        tracing.onViewLongClick(object, v);
    }

    //AdapterView.OnItemClickListener
    public static void onItemClick(Object object, AdapterView<?> parent, View view, int position, long id) {
        tracing.onItemClick(object, parent, view, position, id);
    }

    //AdapterView.OnItemLongClickListener
    public static void onItemLongClick(Object object, AdapterView<?> parent, View view, int position,
                                       long id) {
        tracing.onItemLongClick(object, parent, view, position, id);
    }

    //AdapterView.OnItemSelectedListener
    public static void onItemSelected(Object object, AdapterView<?> parent, View view, int position,
                                      long id) {
        tracing.onItemSelected(object, parent, view, position, id);
    }

    //android.content.DialogInterface$OnClickListener
    public static void onDialogClick(Object object, DialogInterface dialog, int which) {
        tracing.onDialogClick(object, dialog, which);
    }

    //AbsListView.OnScrollListener_*
    public static void onScrollStateChanged(AbsListView view, int scrollState) {
        tracing.onScrollStateChanged(view, scrollState);
    }

    //View.OnScrollChangeListener_*
    public static void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
        tracing.onScrollChange(v, scrollX, scrollY, oldScrollX, oldScrollY);
    }

    //android.support.v4.view.ViewPager\$OnPageChangeListener.onPageSelected
    public static void onPageSelected(Object object, int position) {
        tracing.onPageSelected(object, position);
    }

    //android.support.v4.view.ViewPager\$OnPageChangeListener.onPageScrollStateChanged
    public static void onPageScrollStateChanged(Object object, int state) {
        tracing.onPageScrollStateChanged(object, state);
    }

    //android.support.v7.widget.PopupMenu\$OnMenuItemClickListener
    //android.widget.PopupMenu$OnMenuItemClickListener
    //android.support.v7.widget.Toolbar$OnMenuItemClickListener
    //android.view.MenuItem$OnMenuItemClickListener
    public static void onMenuItemClick(Object object, MenuItem item) {
        tracing.onMenuItemClick(object, item);
    }

    //android.support.v4.widget.SwipeRefreshLayout\$OnRefreshListener
    public static void onSwipeRefresh(Object object) {
        tracing.onSwipeRefresh(object);
    }

    //android.widget.CompoundButton$OnCheckedChangeListener
    public static void onCheckedChanged(Object object, CompoundButton button, boolean isChecked) {
        tracing.onCheckedChanged(object, button, isChecked);
    }

    //android.support.design.widget.TabLayout$OnTabSelectedListener
    public static void onTabSelected(Object object, TabLayout.Tab tab) {
        tracing.onTabSelected(object, tab);
    }

    //android.support.design.widget.TabLayout$OnTabSelectedListener
    public static void onTabUnselected(Object object, TabLayout.Tab tab) {
        tracing.onTabUnselected(object, tab);
    }

    //android.support.design.widget.TabLayout$OnTabSelectedListener
    public static void onTabReselected(Object object, TabLayout.Tab tab) {
        tracing.onTabReselected(object, tab);
    }

    //android.content.DialogInterface$OnKeyListener
    public static void onDialogKey(Object object, DialogInterface dialog, int keyCode, KeyEvent event) {
        tracing.onDialogKey(object, dialog, keyCode, event);
    }

    //android.view.View$OnTouchListener
    public static void onViewTouch(Object object, View view, MotionEvent event) {
        tracing.onViewTouch(object, view, event);
    }

}
