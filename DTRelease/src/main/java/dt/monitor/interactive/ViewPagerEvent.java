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

    public void reInit() {
        position = -1;
        state = -1;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder(eventType);
        if (listener != null) {
            str.append(", ").append(listener.getClass().getName());
        }
        if (position != -1) {
            str.append(", position=").append(position);
        }
        String stateName = getStateName(state);
        if (!TextUtils.isEmpty(stateName)) {
            str.append(", state=").append(stateName);
        }
        return str.toString();
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
            String stateName = getStateName(state);
            if (!TextUtils.isEmpty(stateName)) {
                json.put("state", stateName);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    private String getStateName(int state) {
        if (state != -1) {
            switch (state) {
                case 0:
                    return "IDLE";
                case 1:
                    return "DRAGGING";
                case 2:
                    return "SETTLING";
            }
        }
        return null;
    }

}
