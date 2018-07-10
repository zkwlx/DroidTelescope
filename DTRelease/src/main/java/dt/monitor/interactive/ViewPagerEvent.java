package dt.monitor.interactive;

import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * ViewPager 事件类
 * Created by ZhouKeWen on 2017/5/15.
 */
public class ViewPagerEvent implements IEvent {

    private String listenerName;

    private String eventType;

    private int position = -1;

    private String state = null;

    public void setListenerName(String listenerName) {
        this.listenerName = listenerName;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public void setState(String state) {
        this.state = state;
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
            if (position != -1) {
                json.put("position", position);
            }
            if (!TextUtils.isEmpty(state)) {
                json.put("state", state);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

}
