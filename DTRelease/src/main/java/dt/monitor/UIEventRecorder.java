package dt.monitor;

import java.util.LinkedList;

import dt.monitor.utils.Logger;

/**
 * @author zhoukewen
 * @since 2018/7/5
 */
public class UIEventRecorder {

    private static final int MAX_EVENT_COUNT = 25;

    private static LinkedList<String> eventList = new LinkedList<>();

    public static void add(String eventContent) {
        Logger.i("event>>>---" + eventContent);
        int size = eventList.size();
        if (size >= MAX_EVENT_COUNT) {
            eventList.removeLast();
        }
        eventList.addFirst(eventContent);
    }

    /**
     * 获取用户交互事件列表，索引0代表最近发生的事件
     *
     * @return
     */
    public static String[] obtainEvents() {
        return eventList.toArray(new String[]{});
    }

    //TODO 别忘了清理！？！？？！
    public static void clean() {
        eventList.clear();
    }


}
