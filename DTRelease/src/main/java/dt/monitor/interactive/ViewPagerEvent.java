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

    private Object listener;

    private String eventType;

    private int position = -1;

    private int state = -1;

    public void setPosition(int position) {
        this.position = position;
    }

    public void setState(int state) {
        this.state = state;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public void setListener(Object listener) {
        this.listener = listener;
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
            if (position != -1) {
                json.put("position", position);
            }
            if (state != -1) {
                switch (state) {
                    case 0:
                        json.put("state", "IDLE");
                        break;
                    case 1:
                        json.put("state", "DRAGGING");
                        break;
                    case 2:
                        json.put("state", "SETTLING");
                        break;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

}
