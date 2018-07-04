package dt.monitor.interactive;

import android.support.annotation.Nullable;
import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 列表中item的操作事件对象，用于记录列表中一个item的Click、LongClick、selected事件等
 * TODO 使用对象池！
 * Created by ZhouKeWen on 2017/5/15.
 */
public class ItemEvent implements IEvent {

    private String listenerName;

    private String eventType;

    private String pageName;

    private int position;

    private long id;

    @Nullable
    private String adapterName;

    private String[] parentArray;

    private String viewObject;

    @Override
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        try {
            json.put("eventType", eventType);
            json.put("listenerName", listenerName);
            json.put("pageName", pageName);
            json.put("viewObject", viewObject);
            json.put("position", position);
            json.put("id", id);
            if (!TextUtils.isEmpty(adapterName)) {
                json.put("adapterName", adapterName);
            }
            if (parentArray != null && parentArray.length > 0) {
                JSONArray jsonArray = new JSONArray(parentArray);
                json.put("parents", jsonArray);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    public String getListenerName() {
        return listenerName;
    }

    public void setListenerName(String listenerName) {
        this.listenerName = listenerName;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getPageName() {
        return pageName;
    }

    public void setPageName(String pageName) {
        this.pageName = pageName;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getAdapterName() {
        return adapterName;
    }

    public void setAdapterName(String adapterName) {
        this.adapterName = adapterName;
    }

    public String[] getParentArray() {
        return parentArray;
    }

    public void setParentArray(String[] parentArray) {
        this.parentArray = parentArray;
    }

    public String getViewObject() {
        return viewObject;
    }

    public void setViewObject(String viewObject) {
        this.viewObject = viewObject;
    }
}
