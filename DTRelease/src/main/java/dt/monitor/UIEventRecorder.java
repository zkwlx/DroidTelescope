package dt.monitor;

import java.util.LinkedList;

import dt.monitor.utils.Logger;

/**
 * @author zhoukewen
 * @since 2018/7/5
 */
public class UIEventRecorder {

    private static final int MAX_EVENT_COUNT = 25;

    private static long eventSize;

    private static LinkedList<String> eventList = new LinkedList<>();

    public static void add(String eventContent) {
        //TODO 关闭日志
        Logger.i("别忘了关闭 event>>>---" + eventContent);
        int size = eventList.size();
        if (size >= MAX_EVENT_COUNT) {
            String removed = eventList.removeLast();
            eventSize -= removed.length();
        }
        eventSize += eventContent.length();
        eventList.addFirst(eventContent);

        Logger.i("------event count:" + eventList.size() + ", size::" + eventSize);
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
        eventSize = 0;
        eventList.clear();
    }


}
