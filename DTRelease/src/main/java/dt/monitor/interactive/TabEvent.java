package dt.monitor.interactive;

import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * TabLayout 事件类
 * Created by ZhouKeWen on 2017/5/15.
 */
public class TabEvent implements IEvent {

    private String listenerName;

    private String eventType;

    private int position = -1;

    private String text = null;

    public void setListenerName(String listenerName) {
        this.listenerName = listenerName;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public void setText(String text) {
        this.text = text;
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
            if (position != -1) {
                json.put("position", position);
            }
            if (!TextUtils.isEmpty(text)) {
                json.put("text", text);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }


}
