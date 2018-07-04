package dt.monitor.interactive;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * View事件对象，用于记录一个View的Click和LongClick等事件
 * TODO 使用对象池！
 * Created by ZhouKeWen on 2017/5/15.
 */
public class ViewEvent implements IEvent {

    private String listenerName;

    private String eventType;

    private String pageName;

    private String viewObject;

    private String[] parentArray;

    public String getListenerName() {
        return listenerName;
    }

    public void setListenerName(String listenerName) {
        this.listenerName = listenerName;
    }

    public String getPageName() {
        return pageName;
    }

    public void setPageName(String pageName) {
        this.pageName = pageName;
    }

    public String getViewObject() {
        return viewObject;
    }

    public void setViewObject(String viewObject) {
        this.viewObject = viewObject;
    }

    public String[] getParentArray() {
        return parentArray;
    }

    public void setParentArray(String[] parentArray) {
        this.parentArray = parentArray;
    }

    public String getEventType() {
        return eventType;
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
            if (parentArray != null && parentArray.length > 0) {
                JSONArray jsonArray = new JSONArray(parentArray);
                json.put("parents", jsonArray);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }
}
