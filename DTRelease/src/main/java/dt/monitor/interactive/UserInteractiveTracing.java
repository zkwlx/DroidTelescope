package dt.monitor.interactive;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.CompoundButton;

import org.json.JSONException;

import dt.monitor.DT;
import dt.monitor.UIEventRecorder;
import dt.monitor.utils.Logger;

/**
 * Created by ZhouKeWen on 2017/5/15.
 */
public class UserInteractiveTracing {

    private static final String VIEW_CLICK = "view_click";
    private static final String VIEW_LONG_CLICK = "view_long_click";
    private static final String VIEW_TOUCH_UP = "view_touch_up";
    private static final String DIALOG_CLICK = "dialog_click";
    private static final String DIALOG_KEY = "dialog_key_up";
    private static final String ITEM_CLICK = "item_click_event";
    private static final String ITEM_LONG_CLICK = "item_long_click_event";
    private static final String ITEM_SELECTED = "item_selected_event";
    private static final String MENU_ITEM_CLICK = "menu_item_click";
    private static final String VIEWPAGER_SELECTED = "viewpager_selected";
    private static final String VIEWPAGER_STATE_CHANGED = "viewpager_state_changed";
    private static final String SWIPE_REFRESH = "swipe_refresh";
    private static final String COMPOUND_BTN_CHECKED = "button_checked";
    private static final String TAB_SELECTED = "tab_selected";
    private static final String TAB_UNSELECTED = "tab_unselected";
    private static final String TAB_RESELECTED = "tab_reselected";

    //android.view.View$OnClickListener
    public void onViewClick(Object object, View view) {
        viewEvent(VIEW_CLICK, object, view);
    }

    //android.view.View$OnLongClickListener
    public void onViewLongClick(Object object, View view) {
        viewEvent(VIEW_LONG_CLICK, object, view);
    }

    //AdapterView.OnItemClickListener
    public void onItemClick(Object object, AdapterView<?> parent, View view, int position, long id) {
        itemEvent(ITEM_CLICK, object, parent, view, position, id);
    }

    //AdapterView.OnItemLongClickListener
    public void onItemLongClick(Object object, AdapterView<?> parent, View view, int position, long id) {
        itemEvent(ITEM_LONG_CLICK, object, parent, view, position, id);
    }

    //AdapterView.OnItemSelectedListener
    public void onItemSelected(Object object, AdapterView<?> parent, View view, int position, long id) {
        itemEvent(ITEM_SELECTED, object, parent, view, position, id);
    }

    //android.content.DialogInterface$OnClickListener
    public void onDialogClick(Object listener, DialogInterface dialog, int which) {
        long time = System.currentTimeMillis();

        DialogEvent dialogEvent = new DialogEvent();
        dialogEvent.setEventType(DIALOG_CLICK);
        dialogEvent.setListener(listener);
        dialogEvent.setDialog(dialog);
        dialogEvent.setWhich(which);

        addToList(dialogEvent);

        time = System.currentTimeMillis() - time;
        Logger.i("onDialogClick----ms:" + time);
    }

    //AbsListView.OnScrollListener_*
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        //TODO ?
    }

    //View.OnScrollChangeListener_*
    public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
        //TODO ?
    }

    //android.support.v4.view.ViewPager\$OnPageChangeListener.onPageSelected
    public void onPageSelected(Object listener, int position) {
        ViewPagerEvent event = new ViewPagerEvent();
        event.setEventType(VIEWPAGER_SELECTED);
        event.setListener(listener);
        event.setPosition(position);

        addToList(event);
    }

    //android.support.v4.view.ViewPager\$OnPageChangeListener.onPageScrollStateChanged
    public void onPageScrollStateChanged(Object listener, int state) {
        if (state == 2) {//SETTLING
            ViewPagerEvent event = new ViewPagerEvent();
            event.setEventType(VIEWPAGER_STATE_CHANGED);
            event.setState(state);
            event.setListener(listener);

            addToList(event);
        }
    }

    //android.support.v7.widget.PopupMenu\$OnMenuItemClickListener
    //android.widget.PopupMenu$OnMenuItemClickListener
    //android.support.v7.widget.Toolbar$OnMenuItemClickListener
    //android.view.MenuItem$OnMenuItemClickListener
    public void onMenuItemClick(Object listener, MenuItem item) {
        MenuItemEvent event = new MenuItemEvent();
        event.setEventType(MENU_ITEM_CLICK);
        event.setItem(item);
        event.setListener(listener);

        addToList(event);
    }

    //android.support.v4.widget.SwipeRefreshLayout\$OnRefreshListener
    public void onSwipeRefresh(Object object) {
        simpleEvent(object, SWIPE_REFRESH);
    }

    //android.widget.CompoundButton$OnCheckedChangeListener
    public void onCheckedChanged(Object listener, CompoundButton button, boolean isChecked) {
        CheckedViewEvent event = new CheckedViewEvent();
        event.setEventType(COMPOUND_BTN_CHECKED);
        event.setListener(listener);
        event.setButton(button);
        event.setChecked(isChecked);

        addToList(event);
    }

    //android.support.design.widget.TabLayout$OnTabSelectedListener
    public void onTabSelected(Object object, TabLayout.Tab tab) {
        tabEvent(TAB_SELECTED, object, tab);
    }

    //android.support.design.widget.TabLayout$OnTabSelectedListener
    public void onTabUnselected(Object object, TabLayout.Tab tab) {
        tabEvent(TAB_UNSELECTED, object, tab);
    }

    //android.support.design.widget.TabLayout$OnTabSelectedListener
    public void onTabReselected(Object object, TabLayout.Tab tab) {
        tabEvent(TAB_RESELECTED, object, tab);
    }

    //android.content.DialogInterface$OnKeyListener
    public void onDialogKey(Object listener, DialogInterface dialog, int keyCode, KeyEvent keyEvent) {
        if (keyEvent.getAction() == KeyEvent.ACTION_UP) {
            long time = System.currentTimeMillis();
            DialogKeyEvent event = new DialogKeyEvent();
            event.setEventType(DIALOG_KEY);
            event.setListener(listener);
            event.setDialog(dialog);
            event.setKeyCode(keyCode);

            addToList(event);

            time = System.currentTimeMillis() - time;
            Logger.i("onDialogKey----ms:" + time);
        }
    }

    //android.view.View$OnTouchListener
    public void onViewTouch(Object object, View view, MotionEvent event) {
        final int action = event.getActionMasked();
        if (action == MotionEvent.ACTION_UP) {
            viewEvent(VIEW_TOUCH_UP, object, view);
        }
    }

    private void simpleEvent(Object listener, String type) {
        SimpleEvent event = new SimpleEvent();
        event.setEventType(type);
        event.setListener(listener);

        addToList(event);
    }

    private void viewEvent(String eventType, Object listener, View view) {
        long time = System.currentTimeMillis();

        ViewEvent viewEvent = new ViewEvent();
        viewEvent.setEventType(eventType);
        viewEvent.setListener(listener);
        viewEvent.setView(view);

        addToList(viewEvent);

        time = System.currentTimeMillis() - time;
        Logger.i(eventType + "----ms:" + time);
    }

    private void itemEvent(String eventType, Object listener, AdapterView<?> parent, View view, int position,
                           long id) {
        long time = System.currentTimeMillis();

        ItemEvent itemEvent = new ItemEvent();
        itemEvent.setEventType(eventType);
        itemEvent.setItemView(view);
        itemEvent.setParent(parent);
        itemEvent.setListener(listener);
        itemEvent.setPosition(position);
        itemEvent.setId(id);

        addToList(itemEvent);

        time = System.currentTimeMillis() - time;
        Logger.i(eventType + "----ms:" + time);
    }

    private void tabEvent(String eventType, Object listener, TabLayout.Tab tab) {
        TabEvent event = new TabEvent();
        event.setEventType(eventType);
        event.setListener(listener);
        event.setTab(tab);

        addToList(event);
    }

    private void addToList(IEvent eventObject) {
        //TODO 考虑异步
        try {
            UIEventRecorder.add(eventObject.toJson().toString(3));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
