package dt.monitor.interactive;

import android.content.DialogInterface;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Dialog操作事件对象，用于记录一个Dialog的Click事件等
 * TODO 使用对象池！
 * Created by ZhouKeWen on 2017/5/15.
 */
public class DialogKeyEvent implements IEvent {

    private Object listener;

    private String eventType;

    private DialogInterface dialog;

    private int keyCode;

    public void setListener(Object listener) {
        this.listener = listener;
    }

    public void setDialog(DialogInterface dialog) {
        this.dialog = dialog;
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
            if (listener != null) {
                json.put("listenerName", listener.getClass().getName());
            }
            if (dialog != null) {
                json.put("dialogName", dialog.getClass().getName());
            }
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
