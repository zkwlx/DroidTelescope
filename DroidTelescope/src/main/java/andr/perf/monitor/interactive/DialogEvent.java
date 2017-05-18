package andr.perf.monitor.interactive;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Dialog操作事件对象，用于记录一个Dialog的Click事件等
 * TODO 使用对象池！
 * Created by ZhouKeWen on 2017/5/15.
 */
public class DialogEvent implements IEvent {

    private String listenerName;

    private String eventType;

    private String dialogName;

    private int which;

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getListenerName() {
        return listenerName;
    }

    public void setListenerName(String listenerName) {
        this.listenerName = listenerName;
    }

    public String getDialogName() {
        return dialogName;
    }

    public void setDialogName(String dialogName) {
        this.dialogName = dialogName;
    }

    public int getWhich() {
        return which;
    }

    public void setWhich(int which) {
        this.which = which;
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
            json.put("dialogName", dialogName);
            json.put("which", which);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }
}
