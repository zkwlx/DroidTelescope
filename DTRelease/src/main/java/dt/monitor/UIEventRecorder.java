package dt.monitor;

import java.util.Deque;
import java.util.LinkedList;
import java.util.NoSuchElementException;

/**
 * UI 交互事件记录器
 *
 * @author zhoukewen
 * @since 2018/7/5
 */
public class UIEventRecorder {

    private static final int MAX_EVENT_COUNT = 50;

    private volatile static int maxCount = MAX_EVENT_COUNT;

    //出现了 removeLast 时 last node 为空的情况
    private static Deque<String> eventList = new LinkedList<>();

    /**
     * 添加一条交互事件
     *
     * @param eventContent
     */
    public static synchronized void add(String eventContent) {
        if (eventList.size() >= maxCount) {
            try {
                eventList.removeLast();
            } catch (NoSuchElementException e) {
                throw new NoSuchElementException("On event: " + eventContent);
            }
        }
        eventList.addFirst(eventContent);
    }

    /**
     * 获取用户交互事件列表，索引0代表最近发生的事件
     *
     * @return
     */
    public static synchronized String[] obtainEvents() {
        return eventList.toArray(new String[]{});
    }

    public static synchronized void clean() {
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
