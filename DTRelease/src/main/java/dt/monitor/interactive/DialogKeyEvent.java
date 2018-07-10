package dt.monitor.interactive;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Dialog操作事件对象，用于记录一个Dialog的Click事件等
 * TODO 使用对象池！
 * Created by ZhouKeWen on 2017/5/15.
 */
public class DialogKeyEvent implements IEvent {

    private String listenerName;

    private String eventType;

    private String dialogName;

    private int keyCode;

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public void setListenerName(String listenerName) {
        this.listenerName = listenerName;
    }

    public void setDialogName(String dialogName) {
        this.dialogName = dialogName;
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
            json.put("keyCode", keyCode);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    public void setKeyCode(int keyCode) {
        this.keyCode = keyCode;
    }
}
