package dt.monitor.interactive;

import android.support.design.widget.TabLayout;
import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * TabLayout 事件类
 * Created by ZhouKeWen on 2017/5/15.
 */
public class TabEvent implements IEvent {

    private String eventType;

    private Object listener;

    private TabLayout.Tab tab;

    public void setListener(Object listener) {
        this.listener = listener;
    }

    public void setTab(TabLayout.Tab tab) {
        this.tab = tab;
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
        if (tab != null) {
            str.append(", position=").append(tab.getPosition());
            if (tab.getText() != null) {
                str.append(", text=").append(tab.getText().toString());
            }
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
            if (tab != null) {
                json.put("position", tab.getPosition());
                if (tab.getText() != null) {
                    json.put("text", tab.getText().toString());
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }
}
