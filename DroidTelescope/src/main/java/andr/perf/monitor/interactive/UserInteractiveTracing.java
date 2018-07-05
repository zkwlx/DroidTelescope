package andr.perf.monitor.interactive;

import android.content.DialogInterface;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;

import java.util.LinkedList;

import andr.perf.monitor.utils.Logger;

/**
 * Created by ZhouKeWen on 2017/5/15.
 */
public class UserInteractiveTracing {

    private static final String VIEW_CLICK_EVENT = "view_click";
    private static final String VIEW_LONG_CLICK_EVENT = "view_long_click";
    private static final String DIALOG_CLICK_EVENT = "dialog_click";
    private static final String ITEM_CLICK_EVENT = "item_click_event";
    private static final String ITEM_LONG_CLICK_EVENT = "item_long_click_event";
    private static final String ITEM_SELECTED_EVENT = "item_selected_event";

    private static final int MAX_EVENT_COUNT = 5;

    private LinkedList<IEvent> eventList = new LinkedList<>();

    public IEvent[] obtainCurrentEvents() {
        return eventList.toArray(new IEvent[]{});
    }

    //android.view.View$OnClickListener
    public void onViewClick(Object object, View view) {
        ViewEvent viewEvent = new ViewEvent();
        viewEvent.setEventType(VIEW_CLICK_EVENT);
        viewEvent.setListenerName(object.getClass().getName());
        viewEvent.setViewObject(ViewUtils.getViewSign(view));
        viewEvent.setParentArray(ViewUtils.getParentArray(view));
        viewEvent.setPageName(view.getContext().getClass().getName());

        Logger.i("zkw", "view event>>>> " + viewEvent);

        addToList(viewEvent);
    }

    //android.view.View$OnLongClickListener
    public void onViewLongClick(Object object, View view) {
        ViewEvent viewEvent = new ViewEvent();
        viewEvent.setEventType(VIEW_LONG_CLICK_EVENT);
        viewEvent.setListenerName(object.getClass().getName());
        viewEvent.setViewObject(ViewUtils.getViewSign(view));
        viewEvent.setParentArray(ViewUtils.getParentArray(view));
        viewEvent.setPageName(view.getContext().getClass().getName());

        Logger.i("zkw", "view long event>>>> " + viewEvent);
        addToList(viewEvent);
    }

    //AdapterView.OnItemClickListener
    public void onItemClick(Object object, AdapterView<?> parent, View view, int position, long id) {
        onItemEvent(ITEM_CLICK_EVENT, object, parent, view, position, id);
    }

    //AdapterView.OnItemLongClickListener
    public void onItemLongClick(Object object, AdapterView<?> parent, View view, int position, long id) {
        onItemEvent(ITEM_LONG_CLICK_EVENT, object, parent, view, position, id);
    }

    //AdapterView.OnItemSelectedListener
    public void onItemSelected(Object object, AdapterView<?> parent, View view, int position, long id) {
        onItemEvent(ITEM_SELECTED_EVENT, object, parent, view, position, id);
    }

    //android.content.DialogInterface$OnClickListener
    public void onDialogClick(Object object, DialogInterface dialog, int which) {
        DialogEvent dialogEvent = new DialogEvent();
        dialogEvent.setEventType(DIALOG_CLICK_EVENT);
        dialogEvent.setListenerName(object.getClass().getName());
        dialogEvent.setDialogName(dialog.getClass().getName());
        dialogEvent.setWhich(which);

        Logger.i("zkw", "===>" + dialogEvent);
        addToList(dialogEvent);
    }

    //AbsListView.OnScrollListener_*
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    //View.OnScrollChangeListener_*
    public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

    }

    private void onItemEvent(String eventType, Object object, AdapterView<?> parent, View view, int position,
            long id) {
        ItemEvent itemEvent = new ItemEvent();
        itemEvent.setEventType(eventType);
        itemEvent.setListenerName(object.getClass().getName());
        if (parent != null) {
            itemEvent.setPageName(parent.getContext().getClass().getName());
            if (parent.getAdapter() != null) {
                itemEvent.setAdapterName(parent.getAdapter().getClass().getName());
            }
        } else {
            itemEvent.setPageName(view.getContext().getClass().getName());
        }
        itemEvent.setViewObject(ViewUtils.getViewSign(view));
        itemEvent.setParentArray(ViewUtils.getParentArray(view));
        itemEvent.setPosition(position);
        itemEvent.setId(id);

        addToList(itemEvent);
    }

    private void addToList(IEvent eventObject) {
        int size = eventList.size();
        if (size >= MAX_EVENT_COUNT) {
            eventList.removeLast();
        }
        eventList.addFirst(eventObject);
    }

}
