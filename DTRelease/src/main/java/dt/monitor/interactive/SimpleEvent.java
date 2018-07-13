package dt.monitor.interactive;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 简单事件类，说明事件方法没有参数
 * Created by ZhouKeWen on 2017/5/15.
 */
public class SimpleEvent implements IEvent {

    private Object listener;

    private String eventType;

    public void setListener(Object listener) {
        this.listener = listener;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder(eventType);
        if (listener != null) {
            str.append(", ").append(listener.getClass().getName());
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
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }


}
