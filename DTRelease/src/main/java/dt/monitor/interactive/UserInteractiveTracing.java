package dt.monitor.interactive;

import android.content.DialogInterface;
import android.support.design.widget.TabLayout;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.CompoundButton;

import dt.monitor.UIEventRecorder;
import dt.monitor.utils.Logger;

/**
 * Created by ZhouKeWen on 2017/5/15.
 */
public class UserInteractiveTracing {

    private static final String VIEW_CLICK = "viewClick";
    private static final String VIEW_LONG_CLICK = "viewLongClick";
    private static final String VIEW_TOUCH_UP = "viewTouchUp";
    private static final String DIALOG_CLICK = "dialogClick";
    private static final String DIALOG_KEY = "dialogKeyUp";
    private static final String ITEM_CLICK = "itemClick";
    private static final String ITEM_LONG_CLICK = "itemLongClick";
    private static final String ITEM_SELECTED = "itemSelected";
    private static final String MENU_ITEM_CLICK = "menuItemClick";
    private static final String VIEWPAGER_SELECTED = "viewpagerSelected";
    private static final String VIEWPAGER_STATE_CHANGED = "viewpagerStateChanged";
    private static final String SWIPE_REFRESH = "swipeRefresh";
    private static final String COMPOUND_BTN_CHECKED = "buttonChecked";
    private static final String TAB_SELECTED = "tabSelected";
    private static final String TAB_UNSELECTED = "tabUnselected";
    private static final String TAB_RESELECTED = "tabReselected";

    //提前初始化好 Event，全复用这些对象提高性能，由于在 UI 线程执行，所以没有多线程安全问题
    private DialogEvent dialogEvent = new DialogEvent();
    private ViewPagerEvent viewPagerEvent = new ViewPagerEvent();
    private MenuItemEvent menuItemEvent = new MenuItemEvent();
    private CheckedViewEvent checkedEvent = new CheckedViewEvent();
    private DialogKeyEvent dialogKeyEvent = new DialogKeyEvent();
    private SimpleEvent simpleEvent = new SimpleEvent();
    private ViewEvent viewEvent = new ViewEvent();
    private ItemEvent itemEvent = new ItemEvent();
    private TabEvent tabEvent = new TabEvent();

    private InteractiveListener interactiveListener;

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
        DialogEvent event = dialogEvent;
        event.setEventType(DIALOG_CLICK);
        event.setListener(listener);
        event.setDialog(dialog);
        event.setWhich(which);

        dispatch(event);
    }

    //AbsListView.OnScrollListener_*
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        //TODO 暂时不做
    }

    //View.OnScrollChangeListener_*
    public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
        //TODO 暂时不做
    }

    //android.support.v4.view.ViewPager\$OnPageChangeListener.onPageSelected
    public void onPageSelected(Object listener, int position) {
        ViewPagerEvent event = viewPagerEvent;
        event.reInit();
        event.setEventType(VIEWPAGER_SELECTED);
        event.setListener(listener);
        event.setPosition(position);

        dispatch(event);
    }

    //android.support.v4.view.ViewPager\$OnPageChangeListener.onPageScrollStateChanged
    public void onPageScrollStateChanged(Object listener, int state) {
        if (state == 2) {//SETTLING
            ViewPagerEvent event = viewPagerEvent;
            event.reInit();
            event.setEventType(VIEWPAGER_STATE_CHANGED);
            event.setState(state);
            event.setListener(listener);

            dispatch(event);
        }
    }

    //android.support.v7.widget.PopupMenu\$OnMenuItemClickListener
    //android.widget.PopupMenu$OnMenuItemClickListener
    //android.support.v7.widget.Toolbar$OnMenuItemClickListener
    //android.view.MenuItem$OnMenuItemClickListener
    public void onMenuItemClick(Object listener, MenuItem item) {
        MenuItemEvent event = menuItemEvent;
        event.setEventType(MENU_ITEM_CLICK);
        event.setItem(item);
        event.setListener(listener);

        dispatch(event);
    }

    //android.support.v4.widget.SwipeRefreshLayout\$OnRefreshListener
    public void onSwipeRefresh(Object object) {
        simpleEvent(object, SWIPE_REFRESH);
    }

    //android.widget.CompoundButton$OnCheckedChangeListener
    public void onCheckedChanged(Object listener, CompoundButton button, boolean isChecked) {
        CheckedViewEvent event = checkedEvent;
        event.setEventType(COMPOUND_BTN_CHECKED);
        event.setListener(listener);
        event.setButton(button);
        event.setChecked(isChecked);

        dispatch(event);
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
            DialogKeyEvent event = dialogKeyEvent;
            event.setEventType(DIALOG_KEY);
            event.setListener(listener);
            event.setDialog(dialog);
            event.setKeyCode(keyCode);

            dispatch(event);
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
        SimpleEvent event = simpleEvent;
        event.setEventType(type);
        event.setListener(listener);

        dispatch(event);
    }

    private void viewEvent(String eventType, Object listener, View view) {
        ViewEvent event = viewEvent;
        event.setEventType(eventType);
        event.setListener(listener);
        event.setView(view);

        dispatch(event);
    }

    private void itemEvent(String eventType, Object listener, AdapterView<?> parent, View view, int position,
                           long id) {
        ItemEvent event = itemEvent;
        event.setEventType(eventType);
        event.setItemView(view);
        event.setParent(parent);
        event.setListener(listener);
        event.setPosition(position);
        event.setId(id);

        dispatch(event);
    }

    private void tabEvent(String eventType, Object listener, TabLayout.Tab tab) {
        TabEvent event = tabEvent;
        event.setEventType(eventType);
        event.setListener(listener);
        event.setTab(tab);

        dispatch(event);
    }

    private void dispatch(IEvent eventObject) {
        String content = eventObject.toString();
        addToRecorder(content);
        if (interactiveListener != null) {
            interactiveListener.onInteract(content);
        }
    }

    private void addToRecorder(String content) {
        UIEventRecorder.add(content);
    }

    public void setInteractiveListener(InteractiveListener interactiveListener) {
        this.interactiveListener = interactiveListener;
    }

    /**
     * 交互监听器，当系统回调被监听的交互接口时，回调 onInteract()
     */
    public interface InteractiveListener {
        void onInteract(String content);
    }

}
