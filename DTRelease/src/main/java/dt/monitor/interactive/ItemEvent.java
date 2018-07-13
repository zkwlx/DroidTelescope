package dt.monitor.interactive;

import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 列表中item的操作事件对象，用于记录列表中一个item的Click、LongClick、selected事件等
 * TODO 使用对象池！
 * Created by ZhouKeWen on 2017/5/15.
 */
public class ItemEvent implements IEvent {

    private Object listener;

    private String eventType;

    private AdapterView<?> parent;

    private View itemView;

    private int position;

    private long id;

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder(eventType);
        if (listener != null) {
            str.append(", ").append(listener.getClass().getName());
        }
        str.append(", position=").append(position);
        str.append(", id=").append(id);
        if (parent != null) {
            str.append(", page=").append(parent.getContext().getClass().getName());
            if (parent.getAdapter() != null) {
                str.append(", adapter=").append(parent.getAdapter().getClass().getName());
            }
        } else if (itemView != null) {
            str.append(", page=").append(itemView.getContext().getClass().getName());
        }
        if (itemView != null) {
            str.append(", item=").append(ViewUtils.getViewLightSign(itemView));
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
            if (parent != null) {
                json.put("pageName", parent.getContext().getClass().getName());
                if (parent.getAdapter() != null) {
                    json.put("adapterName", parent.getAdapter().getClass().getName());
                }
            } else if (itemView != null) {
                json.put("pageName", itemView.getContext().getClass().getName());
            }

            if (itemView != null) {
                json.put("itemView", ViewUtils.getViewLightSign(itemView));
//                String[] parentArray = ViewUtils.getParentArray(itemView);
//                if (parentArray.length > 0) {
//                    JSONArray jsonArray = new JSONArray(parentArray);
//                    json.put("parents", jsonArray);
//                }
            }
            json.put("position", position);
            json.put("id", id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }


    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setParent(AdapterView<?> parent) {
        this.parent = parent;
    }

    public void setItemView(View view) {
        this.itemView = view;
    }

    public void setListener(Object listener) {
        this.listener = listener;
    }
}
