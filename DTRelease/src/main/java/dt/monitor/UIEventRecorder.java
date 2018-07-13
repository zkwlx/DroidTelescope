package dt.monitor;

import java.util.LinkedList;

import dt.monitor.utils.Logger;

/**
 * UI 交互事件记录器
 *
 * @author zhoukewen
 * @since 2018/7/5
 */
public class UIEventRecorder {

    private static final int MAX_EVENT_COUNT = 50;

    private static int maxCount = MAX_EVENT_COUNT;

    //由于是 ui 线程调用，暂时不用考虑线程安全
    private static LinkedList<String> eventList = new LinkedList<>();

    /**
     * 添加一条交互事件
     *
     * @param eventContent
     */
    public static void add(String eventContent) {
        if (eventList.size() >= maxCount) {
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

    public static void clean() {
        eventList.clear();
    }

    /**
     * 设置记录的交互事件数量上限，超过上限则移除最旧的事件
     *
     * @param maxCount
     */
    public static void setMaxCount(int maxCount) {
        UIEventRecorder.maxCount = maxCount;
    }
}
