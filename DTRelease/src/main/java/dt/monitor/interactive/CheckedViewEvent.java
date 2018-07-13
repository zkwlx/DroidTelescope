package dt.monitor.interactive;

import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * CompoundButton的 check 事件类
 * <p>
 * Created by ZhouKeWen on 2017/5/15.
 */
public class CheckedViewEvent implements IEvent {

    private Object listener;

    private String eventType;

    private View button;

    private boolean checked;

    public void setListener(Object listener) {
        this.listener = listener;
    }

    public void setButton(View button) {
        this.button = button;
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
        str.append(", checked=").append(checked);
        if (button != null) {
            str.append(", page=").append(button.getContext().getClass().getName());
            str.append(", view=").append(ViewUtils.getViewLightSign(button));
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
            if (button != null) {
                json.put("pageName", button.getContext().getClass().getName());
                json.put("view", ViewUtils.getViewLightSign(button));
//                String[] parentArray = ViewUtils.getParentArray(button);
//                if (parentArray.length > 0) {
//                    JSONArray jsonArray = new JSONArray(parentArray);
//                    json.put("parents", jsonArray);
//                }
            }
            json.put("checked", checked);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}
