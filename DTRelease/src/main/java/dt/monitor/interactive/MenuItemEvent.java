package dt.monitor.interactive;

import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Menu item 相关事件
 * Created by ZhouKeWen on 2017/5/15.
 */
public class MenuItemEvent implements IEvent {

    private String listenerName;

    private String eventType;

    private int itemId;

    private String itemIdStr;

    private String title;

    public void setListenerName(String listenerName) {
        this.listenerName = listenerName;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public void setItemIdStr(String itemIdStr) {
        this.itemIdStr = itemIdStr;
    }

    public void setTitle(String title) {
        this.title = title;
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
            json.put("listenerName", listenerName);
            json.put("title", title);
            if (TextUtils.isEmpty(itemIdStr)) {
                json.put("id", itemId);
            } else {
                json.put("id", itemIdStr);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }
}
