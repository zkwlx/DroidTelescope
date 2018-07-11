package dt.monitor.interactive;

import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;
import android.view.MenuItem;

import org.json.JSONException;
import org.json.JSONObject;

import dt.monitor.DT;

/**
 * Menu item 相关事件
 * Created by ZhouKeWen on 2017/5/15.
 */
public class MenuItemEvent implements IEvent {

    private Object listener;

    private String eventType;

    private MenuItem item;

    public void setListener(Object listener) {
        this.listener = listener;
    }

    public void setItem(MenuItem item) {
        this.item = item;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    @Override
    public String toString() {
        JSONObject json = toJson();
        return json.toString();
    }

    @Override
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        try {
            json.put("eventType", eventType);
            if (listener != null) {
                json.put("listenerName", listener.getClass().getName());
            }
            if (item != null) {
                CharSequence title = item.getTitle();
                if (title != null) {
                    json.put("title", title);
                }
                String itemId = String.valueOf(item.getItemId());
                if (DT.weakContext != null) {
                    Context context = DT.weakContext.get();
                    if (context != null) {
                        // id 转成 String
                        String resource = ViewUtils.getResourceId(context.getResources(), item.getItemId());
                        if (!TextUtils.isEmpty(resource)) {
                            itemId = resource;
                        }
                    }
                }
                json.put("id", itemId);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }
}
