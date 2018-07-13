package dt.monitor.interactive;

import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * View事件对象，用于记录一个View的Click和LongClick等事件
 * TODO 使用对象池！
 * Created by ZhouKeWen on 2017/5/15.
 */
public class ViewEvent implements IEvent {

    private Object listener;

    private String eventType;

    private View view;

    public void setListener(Object listener) {
        this.listener = listener;
    }

    public void setView(View view) {
        this.view = view;
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
        if (view != null) {
            str.append(", page=").append(view.getContext().getClass().getName());
            str.append(", view=").append(ViewUtils.getViewLightSign(view));
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
            if (view != null) {
                json.put("pageName", view.getContext().getClass().getName());
                json.put("view", ViewUtils.getViewLightSign(view));
//                String[] parentArray = ViewUtils.getParentArray(view);
//                if (parentArray.length > 0) {
//                    JSONArray jsonArray = new JSONArray(parentArray);
//                    json.put("parents", jsonArray);
//                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }
}
