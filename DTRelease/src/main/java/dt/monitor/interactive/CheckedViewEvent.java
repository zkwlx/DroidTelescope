package dt.monitor.interactive;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * CompoundButton的 check 事件类
 * <p>
 * Created by ZhouKeWen on 2017/5/15.
 */
public class CheckedViewEvent implements IEvent {

    private String listenerName;

    private String eventType;

    private String pageName;

    private String viewObject;

    private String[] parentArray;

    private boolean checked;

    public void setListenerName(String listenerName) {
        this.listenerName = listenerName;
    }

    public void setPageName(String pageName) {
        this.pageName = pageName;
    }

    public void setViewObject(String viewObject) {
        this.viewObject = viewObject;
    }

    public void setParentArray(String[] parentArray) {
        this.parentArray = parentArray;
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
            json.put("pageName", pageName);
            json.put("viewObject", viewObject);
            json.put("checked", checked);
            if (parentArray != null && parentArray.length > 0) {
                JSONArray jsonArray = new JSONArray(parentArray);
                json.put("parents", jsonArray);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}
