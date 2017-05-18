package andr.perf.monitor.interactive;

import org.json.JSONObject;

/**
 * 列表中item的操作事件对象，用于记录列表中一个item的Click、LongClick、selected事件等
 * TODO 使用对象池！
 * Created by ZhouKeWen on 2017/5/15.
 */
public class ItemEvent implements IEvent {
    @Override
    public JSONObject toJson() {
        return null;
    }
}
