package dt.monitor.interactive;

import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 简单事件类，说明事件方法没有参数
 * Created by ZhouKeWen on 2017/5/15.
 */
public class SimpleEvent implements IEvent {

    private String listenerName;

    private String eventType;

    public void setListenerName(String listenerName) {
        this.listenerName = listenerName;
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
            json.put("listenerName", listenerName);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }
}
