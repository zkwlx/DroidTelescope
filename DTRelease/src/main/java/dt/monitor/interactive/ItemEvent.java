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

    public void setListenerName(String listenerName) {
        this.listenerName = listenerName;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public void setPageName(String pageName) {
        this.pageName = pageName;
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

    public void setAdapterName(String adapterName) {
        this.adapterName = adapterName;
    }

    public void setParentArray(String[] parentArray) {
        this.parentArray = parentArray;
    }

    public void setViewObject(String viewObject) {
        this.viewObject = viewObject;
    }
}
